package uk.gov.ons.entities;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public @Data class CSVAddress extends InputAddress {	
	
	@CsvBindByName
	private String oa;
	@CsvBindByName
	private String lsoa;
	@CsvBindByName
	private String msoa;
	@CsvBindByName
	private String lad;
}
