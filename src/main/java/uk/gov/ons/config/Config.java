package uk.gov.ons.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Configuration
public class Config extends AbstractElasticsearchConfiguration {
	
	@Value("${spring.elasticsearch.rest.uris}")
	private String elasticSearchEndpoint;
	
	@Override
	public RestHighLevelClient elasticsearchClient() {

        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()  
            .connectedTo(elasticSearchEndpoint)
            .build();      
        
        return RestClients.create(clientConfiguration).rest();      
    }
}
