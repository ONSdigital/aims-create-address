package uk.gov.ons.entities;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.ons.util.CreateAddressConstants.CountryCode;

@Document(indexName = "#{@config.skinnyIndexName}", createIndex = false)
@TypeAlias("unit-address")
@EqualsAndHashCode(callSuper=true)
public @Data class HybridAddressSkinny extends HybridAddress {

	private LpiSkinny lpi;

	public HybridAddressSkinny(Long uprn, LpiSkinny lpi, String classificationCode, String censusAddressType, String censusEstabType,
			String postcode, CountryCode countryCode, String postTown) {
		super(uprn, classificationCode, censusAddressType, censusEstabType, postcode, countryCode, postTown);
		this.lpi = lpi;
	}	
}