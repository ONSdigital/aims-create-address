package uk.gov.ons.entities;

import lombok.Data;

public @Data class Event {
	
	private String type;
	private String source;
	private String channel;
	private String dateTime;
	private String transactionId;
}
