package uk.gov.ons.entities;

import lombok.Data;

public @Data class NewAddress {
	private String sourceCaseId;
	private CollectionCase collectionCase;
}
