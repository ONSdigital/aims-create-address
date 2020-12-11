package uk.gov.ons.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

import lombok.Getter;

@Configuration
public class Config extends AbstractElasticsearchConfiguration {
	
	@Value("${spring.elasticsearch.rest.uris}")
	private String elasticSearchEndpoint;
	
	@Getter
	@Value("${aims.elasticsearch.index.aux.name}")
	private String auxIndexName;
	
	@Getter
	@Value("${aims.elasticsearch.index.fat.name}")
	private String fatIndexName;
	
	@Getter
	@Value("${aims.elasticsearch.index.skinny.name}")
	private String skinnyIndexName;
	
	@Override
	public RestHighLevelClient elasticsearchClient() {

        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()  
            .connectedTo(elasticSearchEndpoint)
            .build();      
        
        return RestClients.create(clientConfiguration).rest();      
    }
}