package uk.gov.ons.controllers;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
public class CreateAddressControllerTest {
	
    @Autowired
    private WebTestClient client;
	
	private final ElasticsearchContainer elastic;
	private static MockWebServer mockBackEnd;
    
	public CreateAddressControllerTest() throws IOException {
		
		/*
		 *  Service is properly tested in AddressServiceTest.
		 *  ES and MockWebServer required to stop exceptions.
		 */
		elastic = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.3.1");
		elastic.start();

		System.setProperty("spring.elasticsearch.rest.uris",
				elastic.getContainerIpAddress() + ":" + elastic.getFirstMappedPort());
		System.setProperty("spring.data.elasticsearch.client.reactive.endpoints",
				elastic.getContainerIpAddress() + ":" + elastic.getFirstMappedPort());
		
		mockBackEnd = new MockWebServer();
        mockBackEnd.start();
      	    
      	System.setProperty("aims.tokeniser.uri", String.format("http://localhost:%s", mockBackEnd.getPort()));
      	
      	mockBackEnd.enqueue(new MockResponse()
      	      .setBody("")
      	      .addHeader("Content-Type", "application/json"));
	}	
	
	@AfterAll
	public void tear() throws IOException {
		
		elastic.stop();
		elastic.close();
		mockBackEnd.shutdown();
	}
	
	@Test
	void testIndex() {
		client.get().uri("/")
	        .exchange()
	        .expectStatus().isOk();
	}
	
	@Test 
	void testUploadAddresses() {
		
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.part("file", new ClassPathResource("test.csv")).contentType(MediaType.MULTIPART_FORM_DATA);
				
		// Returns page showing addresses that were attempted to load to ES. No indication if it was successful or not.
		client.post().uri("/upload-csv-file")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.consumeWith(response -> {
				// Does the response contain the 2 ARID values uploaded?
				assertTrue(response.toString().contains("DDR200314000000009201"));
				assertTrue(response.toString().contains("DDR200314000000009202"));
			});
	}	
}
