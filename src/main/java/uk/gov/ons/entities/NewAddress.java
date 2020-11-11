package uk.gov.ons.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class NewAddress {
	private String sourceCaseId;
	private CollectionCase collectionCase;
}
