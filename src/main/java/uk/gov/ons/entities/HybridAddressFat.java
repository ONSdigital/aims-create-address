package uk.gov.ons.entities;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.ons.util.CreateAddressConstants.CountryCode;

@Document(indexName = "#{@config.fatIndexName}", createIndex = false)
@TypeAlias("unit-address-fat")
@EqualsAndHashCode(callSuper = true)
public @Data class HybridAddressFat extends HybridAddress {

	private Lpi lpi;
	
	public HybridAddressFat(Long uprn, Lpi lpi, String classificationCode, String censusAddressType, String censusEstabType,
			String postcode, CountryCode countryCode, String postTown) {
		super(uprn, classificationCode, censusAddressType, censusEstabType, postcode, countryCode, postTown);
		this.lpi = lpi;
	}
}
