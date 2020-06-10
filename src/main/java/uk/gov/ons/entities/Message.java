package uk.gov.ons.entities;

import lombok.Data;

public @Data class Message {
	
	private Event event;
	private Payload payload;
}
