package uk.gov.ons.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;

import lombok.Getter;

@Configuration
@EnableReactiveElasticsearchRepositories
public class Config extends ReactiveElasticsearchConfiguration {
	
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
	
	@Value("${aims.elasticsearch.client.socket-timeout}")
	private long elasticSocketTimeout;
	
	@Value("${aims.elasticsearch.client.connect-timeout}")
	private long elasticConnectTimeout;

	@Override
	public ClientConfiguration clientConfiguration() {
		return ClientConfiguration.builder() //
				.connectedTo(elasticSearchEndpoint)
				.withConnectTimeout(elasticConnectTimeout)
				.withSocketTimeout(elasticSocketTimeout)
				.build();
	}
}
