package uk.gov.ons.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TokensTest {
	
	private final String organisationName	= "ACME FLOWERS LTD";
	private final String subBuildingName= "FLAT C";
	private final String buildingName = "CHESTERFIELD LODGE";
	private final String buildingNumber = "78";
	private final String paoStartNumber = "78";
	private final String saoStartSuffix = "C";
	private final String streetName = "BEULAH HILL";
	private final String locality = "LOCKS HEATH";
	private final String townName = "LONDON";
	private final String postcode = "SE19 3EX";
	
	private final String addressLine1 = "ACME FLOWERS LTD";
	private final String addressLine2 = "FLAT C CHESTERFIELD LODGE";
	private final String addressLine3 = "78 BEULAH HILL";

    @Test
	void testAddressAllTownOnly() {
		
		Tokens tokens = new Tokens.TokensBuilder()
				.organisationName(organisationName)
				.subBuildingName(subBuildingName)
				.buildingName(buildingName)
				.buildingNumber(Short.valueOf(buildingNumber))
				.paoStartNumber(Short.valueOf(paoStartNumber))
				.saoStartSuffix(saoStartSuffix)
				.streetName(streetName)
				.townName(townName)
				.postcode(postcode)
				.addressLine1(addressLine1)
				.addressLine2(addressLine2)
				.addressLine3(addressLine3)
				.build();

        String excpectedAddressAll3 = "ACME FLOWERS LTD FLAT C CHESTERFIELD LODGE 78 BEULAH HILL LONDON SE19 3EX";
        assertEquals(excpectedAddressAll3, tokens.getAddressAll());
	}
	
	@Test
	void testAddressAllLocalityOnly() {
		
		Tokens tokens = new Tokens.TokensBuilder()
				.organisationName(organisationName)
				.subBuildingName(subBuildingName)
				.buildingName(buildingName)
				.buildingNumber(Short.valueOf(buildingNumber))
				.paoStartNumber(Short.valueOf(paoStartNumber))
				.saoStartSuffix(saoStartSuffix)
				.streetName(streetName)
				.locality(locality)
				.postcode(postcode)
				.addressLine1(addressLine1)
				.addressLine2(addressLine2)
				.addressLine3(addressLine3)
				.build();

        String excpectedAddressAll2 = "ACME FLOWERS LTD FLAT C CHESTERFIELD LODGE 78 BEULAH HILL LOCKS HEATH SE19 3EX";
        assertEquals(excpectedAddressAll2, tokens.getAddressAll());
	}
	
	@Test
	void testAddressAllLocalityAndTownName() {
				
		Tokens tokens = new Tokens.TokensBuilder()
				.organisationName(organisationName)
				.subBuildingName(subBuildingName)
				.buildingName(buildingName)
				.buildingNumber(Short.valueOf(buildingNumber))
				.paoStartNumber(Short.valueOf(paoStartNumber))
				.saoStartSuffix(saoStartSuffix)
				.streetName(streetName)
				.locality(locality)
				.townName(townName)
				.postcode(postcode)
				.addressLine1(addressLine1)
				.addressLine2(addressLine2)
				.addressLine3(addressLine3)
				.build();

        String excpectedAddressAll1 = "ACME FLOWERS LTD FLAT C CHESTERFIELD LODGE 78 BEULAH HILL LOCKS HEATH LONDON SE19 3EX";
        assertEquals(excpectedAddressAll1, tokens.getAddressAll());
	}

}
