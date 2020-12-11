package uk.gov.ons.entities;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.geo.Point;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@TypeAlias("lpi")
@EqualsAndHashCode(callSuper=true)
public @Data class Lpi extends LpiSkinny {
	
	@Field(name = "organisation")
	private String organisationName;
	private String locality;
	private Short paoEndNumber;
	private String paoEndSuffix;
	private String paoText;
	private Short saoEndNumber;
	private String saoEndSuffix;
	private String saoStartSuffix;
	private String townName;
	
	public Lpi(LpiBuilder builder) {
		this.organisationName = builder.organisationName;
		this.locality = builder.locality;
		this.paoEndNumber = builder.paoEndNumber;
		this.paoEndSuffix = builder.paoEndSuffix;
		this.paoText = builder.paoText;
		this.saoEndNumber = builder.saoEndNumber;
		this.saoEndSuffix = builder.saoEndSuffix;
		this.saoStartSuffix = builder.saoStartSuffix;
		this.townName = builder.townName;
		setDepartmentName(builder.departmentName);
		setSubBuildingName(builder.subBuildingName);
		setBuildingName(builder.buildingName);
		setBuildingNumber(builder.buildingNumber);
		setStreetName(builder.streetName);
		setPostcode(builder.postcode);
		setAddressBasePostal(builder.addressBasePostal);
		setCountry(builder.country);
		setEasting(builder.easting);
		setNorthing(builder.northing);
		setLanguage(builder.language);
		setLocation(new Point(super.parseStringToOptionalDouble(builder.latitude).orElse(0.0),
				super.parseStringToOptionalDouble(builder.longitude).orElse(0.0)));
		setLatitude(builder.latitude);
		setLongitude(builder.longitude);
		setLpiLogicalStatus(builder.lpiLogicalStatus);
		setMixedNag(builder.mixedNag);
		setMixedNagStart(builder.mixedNagStart); // keyword
		setMixedWelshNag(builder.mixedWelshNag);
		setMixedWelshNagStart(builder.mixedWelshNagStart); // keyword
		setNagAll(super.getNagAll());
		setPaoStartNumber(builder.paoStartNumber);
		setPaoStartSuffix(builder.paoStartSuffix);
		setParentUprn(builder.parentUprn);
		setPostcodeLocator(builder.postcodeLocator);
		setSaoStartNumber(builder.saoStartNumber);
		setSecondarySort(builder.secondarySort);
		setStreetDescriptor(builder.streetDescriptor);
		setUprn(builder.uprn);
		setAddressLine1(builder.addressLine1);
		setAddressLine2(builder.addressLine2);
		setAddressLine3(builder.addressLine3);
	}

	public static class LpiBuilder {
		
		private String organisationName;
		private String departmentName;
		private String subBuildingName;
		private String buildingName;
		private Short buildingNumber;
		private String streetName;
		private String locality;
		private String townName;
		private String postcode;
		private String addressBasePostal;
		private String country;
		private Float easting;
		private Float northing;
		private String language;
		private String latitude;
		private String longitude;
		private Byte lpiLogicalStatus;
		private String mixedNag;
		private String mixedNagStart; // keyword
		private String mixedWelshNag;
		private String mixedWelshNagStart; // keyword
		private String nagAll;
		private Short paoStartNumber;
		private String paoStartSuffix;
		private Long parentUprn;
		private String postcodeLocator;
		private Short saoStartNumber;
		private String secondarySort;
		private String streetDescriptor;
		private Long uprn;
		private String addressLine1;
		private String addressLine2;
		private String addressLine3;
		
		private Short paoEndNumber;
		private String paoEndSuffix;
		private String paoText;
		private Short saoEndNumber;
		private String saoEndSuffix;
		private String saoStartSuffix;
		
		public LpiBuilder organisationName(String organisationName) {
			this.organisationName = organisationName;
			return this;
		}
		
		public LpiBuilder departmentName(String departmentName) {
			this.departmentName = departmentName;
			return this;
		}
		
		public LpiBuilder subBuildingName(String subBuildingName) {
			this.subBuildingName = subBuildingName;
			return this;
		}
		
		public LpiBuilder buildingName(String buildingName) {
			this.buildingName = buildingName;
			return this;
		}
		
		public LpiBuilder buildingNumber(Short buildingNumber) {
			this.buildingNumber = buildingNumber;
			return this;
		}
		
		public LpiBuilder streetName(String streetName) {
			this.streetName = streetName;
			return this;
		}
		
		public LpiBuilder locality(String locality) {
			this.locality = locality;
			return this;
		}
		
		public LpiBuilder townName(String townName) {
			this.townName = townName;
			return this;
		}
		
		public LpiBuilder postcode(String postcode) {
			this.postcode = postcode;
			return this;
		}
				
		public LpiBuilder addressBasePostal(String addressBasePostal) {
			this.addressBasePostal = addressBasePostal;
			return this;
		}
		
		public LpiBuilder country(String country) {
			this.country = country;
			return this;
		}
		
		public LpiBuilder easting(Float easting) {
			this.easting = easting;
			return this;
		}
		
		public LpiBuilder northing(Float northing) {
			this.northing = northing;
			return this;
		}
		
		public LpiBuilder language(String language) {
			this.language = language;
			return this;
		}
		
		public LpiBuilder latitude(String latitude) {
			this.latitude = latitude;
			return this;
		}
		
		public LpiBuilder longitude(String longitude) {
			this.longitude = longitude;
			return this;
		}
		
		public LpiBuilder lpiLogicalStatus(Byte lpiLogicalStatus) {
			this.lpiLogicalStatus = lpiLogicalStatus;
			return this;
		}
		
		public LpiBuilder mixedNag(String mixedNag) {
			this.mixedNag = mixedNag;
			return this;
		}
		
		public LpiBuilder mixedNagStart(String mixedNagStart) {
			this.mixedNagStart = mixedNagStart;
			return this;
		}
		
		public LpiBuilder mixedWelshNag(String mixedWelshNag) {
			this.mixedWelshNag = mixedWelshNag;
			return this;
		}
		
		public LpiBuilder mixedWelshNagStart(String mixedWelshNagStart) {
			this.mixedWelshNagStart = mixedWelshNagStart;
			return this;
		}
		
		public LpiBuilder nagAll(String nagAll) {
			this.nagAll = nagAll;
			return this;
		}
		
		public LpiBuilder paoStartNumber(Short paoStartNumber) {
			this.paoStartNumber = paoStartNumber;
			return this;
		}
		
		public LpiBuilder paoStartSuffix(String paoStartSuffix) {
			this.paoStartSuffix = paoStartSuffix;
			return this;
		}
		
		public LpiBuilder parentUprn(Long parentUprn) {
			this.parentUprn = parentUprn;
			return this;
		}
		
		public LpiBuilder postcodeLocator(String postcodeLocator) {
			this.postcodeLocator = postcodeLocator;
			return this;
		}
		
		public LpiBuilder saoStartNumber(Short saoStartNumber) {
			this.saoStartNumber = saoStartNumber;
			return this;
		}
		
		public LpiBuilder secondarySort(String secondarySort) {
			this.secondarySort = secondarySort;
			return this;
		}
		
		public LpiBuilder streetDescriptor(String streetDescriptor) {
			this.streetDescriptor = streetDescriptor;
			return this;
		}
		
		public LpiBuilder uprn(Long uprn) {
			this.uprn = uprn;
			return this;
		}
		
		public LpiBuilder addressLine1(String addressLine1) {
			this.addressLine1 = addressLine1;
			return this;
		}
		
		public LpiBuilder addressLine2(String addressLine2) {
			this.addressLine2 = addressLine2;
			return this;
		}
		
		public LpiBuilder addressLine3(String addressLine3) {
			this.addressLine3 = addressLine3;
			return this;
		}
		
		public LpiBuilder paoEndNumber(Short paoEndNumber) {
			this.paoEndNumber = paoEndNumber;
			return this;
		}
		
		public LpiBuilder paoEndSuffix(String paoEndSuffix) {
			this.paoEndSuffix = paoEndSuffix;
			return this;
		}
		
		public LpiBuilder paoText(String paoText) {
			this.paoText = paoText;
			return this;
		}
		
		public LpiBuilder saoEndNumber(Short saoEndNumber) {
			this.saoEndNumber = saoEndNumber;
			return this;
		}
		
		public LpiBuilder saoEndSuffix(String saoEndSuffix) {
			this.saoEndSuffix = saoEndSuffix;
			return this;
		}
		
		public LpiBuilder saoStartSuffix(String saoStartSuffix) {
			this.saoStartSuffix = saoStartSuffix;
			return this;
		}
				
		public Lpi build() {
			return new Lpi(this);
		}
	}
	
}
