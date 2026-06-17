package uk.gov.ons.controllers;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class CreateAddressControllerTest {

    @Autowired
    private WebTestClient client;
    public static final DockerImageName ELASTIC_IMAGE = DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.14.3");
    private static final ElasticsearchContainer elastic = new ElasticsearchContainer(ELASTIC_IMAGE)
            .withEnv("xpack.security.enabled", "false")
            .withEnv("discovery.type", "single-node");
    private static MockWebServer mockBackEnd;

    @DynamicPropertySource
    static void elasticProps(DynamicPropertyRegistry registry) {
        if (!elastic.isRunning()) {
            elastic.start();
        }
        if (mockBackEnd == null) {
            try {
                mockBackEnd = new MockWebServer();
                mockBackEnd.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        registry.add("spring.elasticsearch.rest.uris", () -> elastic.getHost() + ":" + elastic.getFirstMappedPort());
        registry.add("spring.data.elasticsearch.client.reactive.endpoints", () -> elastic.getHost() + ":" + elastic.getFirstMappedPort());
        registry.add("aims.tokeniser.uri",
                () -> "http://localhost:" + mockBackEnd.getPort());
    }

    @AfterAll
    public void tear() throws IOException {

        elastic.stop();
        elastic.close();
        mockBackEnd.shutdown();
    }

    @Test
    @Order(value = 3)
    void testIndex() {
        client.get().uri("/")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @Order(value = 1)
    void testUploadAuxAddresses() {

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("aux-addresses-test-good.csv")).contentType(MediaType.MULTIPART_FORM_DATA);

        // Returns page showing addresses that were attempted to load to ES and bad addresses.
        client.post().uri("/upload-csv-aux-file")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    // Does the response contain the 2 UPRN values uploaded?
                    Assertions.assertTrue(response.toString().contains("1234567891011"));
                    Assertions.assertTrue(response.toString().contains("1234567891012"));
                });
    }

    @Test
    @Order(value = 2)
    void testUploadUnitAddresses() {

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("unit-addresses-test.csv")).contentType(MediaType.MULTIPART_FORM_DATA);

        // Returns page showing addresses that were attempted to load to ES and bad addresses.
        client.post().uri("/upload-csv-unit-file")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    // Does the response contain the 2 UPRN values uploaded?
                    Assertions.assertTrue(response.toString().contains("8881000006833"));
                    Assertions.assertTrue(response.toString().contains("8881000008109"));
                });
    }


}
