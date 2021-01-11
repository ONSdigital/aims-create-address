package uk.gov.ons.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.ons.util.CreateAddressConstants.CountryCode;


@Document(indexName = "#{@config.auxIndexName}")
@TypeAlias("address")
@AllArgsConstructor
public @Data class Address {

	@Id
	private Long uprn;
	private String postcodeIn;
	private String postcodeOut;
	private String classificationCode;
	private String censusAddressType;
	private String censusEstabType;
	private Long censusEstabUprn;
	private CountryCode countryCode;
	private String postcode;
	private Tokens tokens;
}