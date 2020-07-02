package uk.gov.ons.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Document(indexName = "new-addresses")
@TypeAlias("address")
@AllArgsConstructor
public @Data class Address {

	@Id
	private long uprn;
	private String postcodeIn;
	private String postcodeOut;
	private String classificationCode;
	private String censusAddressType;
	private String censusEstabType;
	private CountryCode countryCode;
	private String postcode;
	private Tokens tokens;

	public enum CountryCode {
		E, W, N, S
	}
}