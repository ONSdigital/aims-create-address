package uk.gov.ons.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.ons.util.CreateAddressConstants.CountryCode;

import java.util.List;

@Document(indexName = "#{@config.auxIndexName}")
@TypeAlias("address")
@AllArgsConstructor
public @Data class Address {

	@TypeAlias("lpi")
	public static @Data class Lpi {
		private String streetDescriptor = "";
		private Short paoStartNumber = 0;
		private String paoStartSuffix = "";
		private String secondarySort = "";
	}

	@TypeAlias("nisra")
	public static @Data class Nisra {
		private String thoroughfare = "";
		private Short paoStartNumber = 0;
		private String secondarySort = "";
	}

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
	private List<Lpi> lpi;
	private List<Nisra> nisra;
}