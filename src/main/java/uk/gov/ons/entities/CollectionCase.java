package uk.gov.ons.entities;

import lombok.Data;

public @Data class CollectionCase {
	
	private String id;
	private String caseType;
	private String survey;
	private String fieldCoordinatorId;
	private String fieldOfficerId;
	private InputAddress address;
	private String oa;
	private String lsoa;
	private String msoa;
	private String lad;
	private String htcWillingness;
	private String htcDigital;
	private String treatmentCode;
}
