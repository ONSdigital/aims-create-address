package uk.gov.ons.entities;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

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
	private String uprn;
	@CsvBindByName(column = "abp_code")
	private String abpCode;
	@CsvBindByName(column = "organisation_name")
	private String organisationName;
	@CsvBindByName(column = "estab_uprn")
	private String estabUprn;

	public String getAddressAll() {
	
		return Stream.of(addressLine1, addressLine2, addressLine3, townName, postcode)
				.filter(s -> s != null && !s.isEmpty()).collect(Collectors.joining(" "));
	}
}