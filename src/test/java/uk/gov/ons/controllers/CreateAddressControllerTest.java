package uk.gov.ons.controllers;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import uk.gov.ons.service.AddressService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreateAddressControllerTest {
	
    @Autowired
    private WebTestClient client;
    
    @Autowired
    AddressService service;
	
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
				
		/*
		 *  Returns page showing addresses that were attempted to load to ES. No indication if it was successful or not.
		 *  The exception in the log when this is run is a result of the parser api not being accessible. Back end is tested
		 *  in other test case.
		 *  TODO: Get rid of error.
		 */
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
