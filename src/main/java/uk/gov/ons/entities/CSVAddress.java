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
	@CsvBindByName(column = "htc_willingness")
	private String htcWillingness;
	@CsvBindByName(column = "htc_digital")
	private String htcDigital;
	@CsvBindByName(column = "treatment_code")
	private String treatmentCode;
	@CsvBindByName(column = "feildcoordinator_id")
	private String fieldCoordinatorId;
	@CsvBindByName(column = "fieldofficer_id")
	private String fieldOfficerId;
	@CsvBindByName(column = "ce_expected_capacity")
	private String ceExpectedCapacity;
	@CsvBindByName(column = "ce_secure")
	private String ceSecure;
	@CsvBindByName(column = "print_batch")
	private String printBatch;
}
