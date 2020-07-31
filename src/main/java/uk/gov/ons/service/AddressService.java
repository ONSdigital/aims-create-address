package uk.gov.ons.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import uk.gov.ons.entities.Address;
import uk.gov.ons.entities.CSVAddress;
import uk.gov.ons.entities.InputAddress;
import uk.gov.ons.exception.CreateAddressRuntimeException;
import uk.gov.ons.json.TokeniserResponse;
import uk.gov.ons.repository.AddressRepository;
import uk.gov.ons.util.AddressMapper;

@Service
public class AddressService {

	private Logger logger = LoggerFactory.getLogger(AddressService.class);
	
	@Autowired
	private AddressRepository addressRepository;
	
	private final WebClient webClient;
	
	@Value("${aims.tokeniser.path}")
	private String path;

	@Autowired
	public AddressService(RestHighLevelClient client, ResourceLoader resourceLoader, 
			@Value("${aims.elasticsearch.index.name}") String indexName,
			@Value("${aims.tokeniser.uri}") String tokeniserEndpoint,
			WebClient.Builder webClientBuilder) {

		this.webClient = webClientBuilder.baseUrl(tokeniserEndpoint).build();
		GetIndexRequest request = new GetIndexRequest("new-addresses");

		try {
			if (!client.indices().exists(request, RequestOptions.DEFAULT)) {
				try (Reader mappingReader = new InputStreamReader(
						resourceLoader.getResource("classpath:mappings.json").getInputStream(),
						Charset.forName("UTF-8"));
						Reader settingsReader = new InputStreamReader(
								resourceLoader.getResource("classpath:settings.json").getInputStream(),
								Charset.forName("UTF-8"))) {

					CreateIndexRequest createRequest = new CreateIndexRequest(indexName);

					createRequest.settings(FileCopyUtils.copyToString(settingsReader), XContentType.JSON);
					createRequest.mapping(FileCopyUtils.copyToString(mappingReader), XContentType.JSON);
					CreateIndexResponse createIndexResponse = client.indices().create(createRequest,
							RequestOptions.DEFAULT);

					if (!createIndexResponse.isAcknowledged()) {
						logger.error(String.format("Can not create index %s", indexName));
						throw new CreateAddressRuntimeException(String.format("Can not create index %s", indexName));
					}

				} catch (IOException ioe) {
					logger.error(String.format("Can not create index %s", indexName), ioe);
					throw new CreateAddressRuntimeException(String.format("Can not create index %s", indexName), ioe);
				}
			}
		} catch (IOException ioe) {
			logger.error(String.format("Can not create index %s", indexName), ioe);
			throw new CreateAddressRuntimeException(String.format("Can not create index %s", indexName), ioe);
		}
	}

	public Mono<Address> createAddress(Address address) {
		return addressRepository.save(address);
	}

	public Flux<Address> createAddresses(List<Address> addresses) {
		return addressRepository.saveAll(addresses);
	}
	
	public Flux<Address> createAddressesFromCsv(List<CSVAddress> addresses) {
	
		return Flux.fromIterable(addresses).parallel().runOn(Schedulers.elastic())
				.flatMap(csvAddress -> addressRepository.saveAll(buildAddress(csvAddress)))
				.sequential()
				.doOnError(ex -> Flux.just("Error: " + ex.getMessage()));
	}
	
	public Mono<Address> createAddressFromMsg(InputAddress pubSubAddress) {
	
		return buildAddress(pubSubAddress)
				.flatMap(address -> addressRepository.save(address))
				.doOnError(ex -> Mono.just("Error: " + ex.getMessage()))
				.doOnSuccess(address -> logger.debug(String.format("Added address: %s", address)));
	}	
	
	private Mono<Address> buildAddress(InputAddress inputAddress) {
		
		return webClient.get()
				.uri(path, inputAddress.getAddressAll())
				.retrieve()
				.bodyToMono(TokeniserResponse.class)
				.map(tokeniserResponse -> {
	                Address address = AddressMapper.from(inputAddress, tokeniserResponse);
	                return address;});
	}
}