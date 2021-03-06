package uk.gov.ons.entities;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.ons.util.CreateAddressConstants.CountryCode;

@AllArgsConstructor
public @Data class HybridAddress {

	@Id
	private Long uprn;
	private String classificationCode;
	private String censusAddressType;
	private String censusEstabType;
//	private Long censusEstabUprn;
	private String postcode;
	private CountryCode countryCode;
	private String postcodeStreetTown;
	private String postTown;

}
