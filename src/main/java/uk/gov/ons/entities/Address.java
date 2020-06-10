package uk.gov.ons.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Data;

@Document(indexName = "new-addresses")
@TypeAlias("address")
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

	public Address(long uprn, String postcodeIn, String postcodeOut, String classificationCode,
			String censusAddressType, String censusEstabType, CountryCode countryCode, String postcode,
			Tokens tokens) {
		super();
		this.uprn = uprn;
		this.postcodeIn = postcodeIn;
		this.postcodeOut = postcodeOut;
		this.classificationCode = classificationCode;
		this.censusAddressType = censusAddressType;
		this.censusEstabType = censusEstabType;
		this.countryCode = countryCode;
		this.postcode = postcode;
		this.tokens = tokens;
	}
}