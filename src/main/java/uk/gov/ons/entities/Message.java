package uk.gov.ons.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class Message {
	
	private Event event;
	private Payload payload;
}
