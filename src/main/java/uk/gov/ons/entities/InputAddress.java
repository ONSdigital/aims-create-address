package uk.gov.ons.entities;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;

import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public @Data class InputAddress {

	@CsvBindByName(column = "address_line1")
	private String addressLine1;
	@CsvBindByName(column = "address_line2")
	private String addressLine2;
	@CsvBindByName(column = "address_line3")
	private String addressLine3;
	@CsvBindByName(column = "town_name")
	private String townName;
	@CsvBindByName
	private String postcode;
	@CsvBindByName
	private String region;
	@CsvBindByName(column = "address_type")
	private String addressType;
	@CsvBindByName(column = "address_level")
	private String addressLevel;
	@CsvBindByName(column = "estab_type")
	private String estabType;
	@CsvBindByName
	private String latitude;
	@CsvBindByName
	private String longitude;
	@CsvBindByName
	@NotBlank(message = "UPRN cannot be blank")
	private String uprn;
	@CsvBindByName(column = "abp_code")
	private String abpCode;
	@CsvBindByName(column = "organisation_name")
	private String organisationName;
	@CsvBindByName(column = "estab_uprn")
	private String estabUprn;
	
	@JsonCreator
	public InputAddress(@JsonProperty(value = "addressLine1", required = true) String addressLine1, 
			@JsonProperty(value = "addressLine2", required = true) String addressLine2, @JsonProperty(value = "addressLine3", required = true) String addressLine3, 
			@JsonProperty(value = "townName", required = true) String townName, @JsonProperty(value = "postcode", required = true) String postcode,
			@JsonProperty(value = "region", required = true) String region, @JsonProperty(value = "addressType", required = true) String addressType, 
			@JsonProperty(value = "addressLevel", required = true) String addressLevel, @JsonProperty(value = "estabType", required = true) String estabType, 
			@JsonProperty(value = "latitude", required = true) String latitude, @JsonProperty(value = "longitude", required = true) String longitude,
			@JsonProperty(value = "uprn", required = true) String uprn, @JsonProperty(value = "abpCode", required = true) String abpCode, 
			@JsonProperty(value = "organisationName", required = true) String organisationName, @JsonProperty(value = "estabUprn", required = true) String estabUprn) {
		super();
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.addressLine3 = addressLine3;
		this.townName = townName;
		this.postcode = postcode;
		this.region = region;
		this.addressType = addressType;
		this.addressLevel = addressLevel;
		this.estabType = estabType;
		this.latitude = latitude;
		this.longitude = longitude;
		this.uprn = uprn;
		this.abpCode = abpCode;
		this.organisationName = organisationName;
		this.estabUprn = estabUprn;
	}
	
	public String getAddressAll() {
		
		return Stream.of(addressLine1, addressLine2, addressLine3, townName, postcode)
				.filter(s -> s != null && !s.isEmpty()).collect(Collectors.joining(" "));
	}
}