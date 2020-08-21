package uk.gov.ons.entities;

import lombok.Data;

public @Data class CollectionCase {
	
	private String id;
	private String caseType;
	private String survey;
	private InputAddress address;
	private boolean handDelivery;
	private boolean skeleton;
	private boolean surveyLaunched;
}
