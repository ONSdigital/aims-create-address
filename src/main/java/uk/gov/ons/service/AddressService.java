package uk.gov.ons.service;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import uk.gov.ons.entities.*;
import uk.gov.ons.exception.CreateAddressException;
import uk.gov.ons.exception.CreateAddressRuntimeException;
import uk.gov.ons.json.TokeniserResponse;
import uk.gov.ons.repository.fat.AddressRepository;
import uk.gov.ons.repository.fat.HybridAddressFatRepository;
import uk.gov.ons.repository.skinny.HybridAddressSkinnyRepository;
import uk.gov.ons.util.AddressMapper;
import uk.gov.ons.util.HybridAddressFatMapper;
import uk.gov.ons.util.HybridAddressSkinnyMapper;
import uk.gov.ons.util.ValidatedAddress;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Validated
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private HybridAddressFatRepository hybridAddressFatRepository;

    @Autowired
    private HybridAddressSkinnyRepository hybridAddressSkinnyRepository;

    private final WebClient webClient;

    @Value("${aims.tokeniser.path}")
    private String path;

    @Value("gs://${aims.gcp.bucket}/")
    private String gcsBucket;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    ReactiveElasticsearchOperations operations;

    @Autowired
    ReactiveElasticsearchClient elasticsearchClient;

    @Autowired
    RestClient restClient;


    private static final String datePattern = "yyyyMMdd_HHmmss";
    private final DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern(datePattern);

    @Autowired
    public AddressService(ReactiveElasticsearchClient elasticsearchClient, ResourceLoader resourceLoader,
                          @Value("${aims.elasticsearch.index.aux.name}") String indexName,
                          @Value("${aims.tokeniser.uri}") String tokeniserEndpoint,
                          @Value("${aims.elasticsearch.cluster.fat-enabled}") boolean fatClusterEnabled,
                          WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder.clientConnector((ClientHttpConnector) new ReactorClientHttpConnector(HttpClient.create()
                .wiretap(true))).baseUrl(tokeniserEndpoint).build();

        if (fatClusterEnabled) {
            try {
                if (elasticsearchClient.indices().exists(ExistsRequest.of(e -> e.index(indexName))).equals(false)) {
                    if (Boolean.FALSE.equals(elasticsearchClient.indices().exists(ExistsRequest.of(e -> e.index(indexName))).flatMap(response -> Mono.just(response.value())).block()))
                        try (Reader mappingReader = new InputStreamReader(
                                resourceLoader.getResource("classpath:mappings.json").getInputStream(),
                                StandardCharsets.UTF_8);
                             Reader settingsReader = new InputStreamReader(
                                     resourceLoader.getResource("classpath:settings.json").getInputStream(),
                                     StandardCharsets.UTF_8)) {


                            TypeMapping tm = new TypeMapping.Builder().withJson(mappingReader).build();
                            IndexSettings is = new IndexSettings.Builder().withJson(settingsReader).build();
                            CreateIndexRequest createRequest = CreateIndexRequest.of(builder -> builder.index(indexName).settings(is).mappings(tm));
                            Mono<CreateIndexResponse> CreateResult = elasticsearchClient.indices().create(createRequest).
                                    doOnError(throwable -> log.error(String.format("Can not create index %s", indexName)));
                        } catch (IOException ioe) {
                            log.error(String.format("Can not create index %s", indexName), ioe);
                            throw new CreateAddressRuntimeException(String.format("Can not create index %s", indexName), ioe);
                        }
                }
            } catch (Exception ioe) {
                log.error(String.format("Can not create index %s", indexName), ioe);
                throw new CreateAddressRuntimeException(String.format("Can not create index %s", indexName), ioe);
            }
        }
    }

    public Mono<Address> createAddress(Address address) {
        return addressRepository.save(address);

    }

    public Flux<Address> createAddresses(List<Address> addresses) {
        return addressRepository.saveAll(addresses);
    }

    public Flux<Address> createAuxAddressesFromCsv(List<ValidatedAddress<AuxAddress>> addresses) {
        return Flux.fromIterable(addresses).limitRate(20)
                .flatMap(validatedAddress -> addressRepository.saveAll(buildAddress(validatedAddress.getAddress()))
                        .onErrorResume(ex -> {
                            log.warn("Skipping aux address due to error: {}", ex.toString());
                            return Flux.empty();
                        }));
    }

    public Flux<HybridAddressSkinny> createSkinnyUnitAddressesFromCsv(List<ValidatedAddress<UnitAddress>> addresses) {
        return Flux.fromIterable(addresses).limitRate(20)
                .flatMap(validatedAddress -> hybridAddressSkinnyRepository.saveAll(buildHybridAddressSkinny(validatedAddress.getAddress()))
                        .onErrorResume(ex -> {
                            log.warn("Skipping skinny unit address due to error: {}", ex.toString());
                            return Flux.empty();
                        }));
    }

    public Flux<HybridAddressFat> createFatUnitAddressesFromCsv(List<ValidatedAddress<UnitAddress>> addresses) {
        return Flux.fromIterable(addresses).limitRate(20)
                .flatMap(validatedAddress -> hybridAddressFatRepository.saveAll(buildHybridAddressFat(validatedAddress.getAddress()))
                        .onErrorResume(ex -> {
                            log.warn("Skipping fat unit address due to error: {}", ex.toString());
                            return Flux.empty();
                        }));
    }

    public Mono<Address> createAddressFromMsg(InputAddress pubSubAddress) throws CreateAddressException {

        /*
         * Can't @Valid the InputAddress for a PubSub msg as it can have legitimate
         * empty fields e.g. lat/long. Only UPRN is mandatory.
         */
        if (pubSubAddress.getUprn().isBlank()) {
            throw new CreateAddressException("UPRN is mandatory.");
        }

        return buildAddress(pubSubAddress).flatMap(address -> addressRepository.save(address))
                .doOnSuccess(address -> log.debug(String.format("Added address: %s", address)));
    }

    private Mono<Address> buildAddress(InputAddress inputAddress) {

        log.debug(String.format("Input Address: %s", inputAddress.toString()));

        return webClient.get().uri(path, inputAddress.getAddressAll()).retrieve().bodyToMono(TokeniserResponse.class)
                .map(tokeniserResponse -> {
                    log.debug(String.format("Tokeniser Response: %s", tokeniserResponse.toString()));
                    return AddressMapper.from(inputAddress, tokeniserResponse);
                });
    }

    private Mono<HybridAddressSkinny> buildHybridAddressSkinny(UnitAddress unitAddress) {

        log.debug(String.format("Input Address: %s", unitAddress.toString()));

        return webClient.get().uri(path, unitAddress.getAddressAll())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new CreateAddressRuntimeException("Client error")))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new CreateAddressRuntimeException("Server error")))
                .bodyToMono(TokeniserResponse.class)
                .timeout(Duration.ofSeconds(5))
                .map(tokeniserResponse -> {
                    log.debug(String.format("Tokeniser Response: %s", tokeniserResponse.toString()));
                    return HybridAddressSkinnyMapper.from(unitAddress, tokeniserResponse);
                });
    }

    private Mono<HybridAddressFat> buildHybridAddressFat(UnitAddress unitAddress) {

        log.debug(String.format("Input Address: %s", unitAddress.toString()));

        return webClient.get().uri(path, unitAddress.getAddressAll())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new CreateAddressRuntimeException("Client error")))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new CreateAddressRuntimeException("Server error")))
                .bodyToMono(TokeniserResponse.class)
                .timeout(Duration.ofSeconds(5))
                .map(tokeniserResponse -> {
                    log.debug(String.format("Tokeniser Response: %s", tokeniserResponse.toString()));
                    return HybridAddressFatMapper.from(unitAddress, tokeniserResponse);
                });
    }

    public <T> String writeBadAddressesCsv(List<ValidatedAddress<T>> badAddresses, String fileName) throws Exception {

        // TODO: Make this asynchronous - it could be a large file!
        Resource gcsFile = resourceLoader.getResource(
                String.format("%s%s_%s", gcsBucket, LocalDateTime.now().format((dateTimeFormater)), fileName));

        CSVWriter writer = new CSVWriter(new OutputStreamWriter(((WritableResource) gcsFile).getOutputStream()));

        // Write the header
        writer.writeNext(badAddresses.getFirst().getHeader().toArray(new String[0]));
        badAddresses.forEach(address -> writer.writeNext(address.getRow().toArray(new String[0])));
        writer.close();

        return gcsFile.getFilename();
    }
}
