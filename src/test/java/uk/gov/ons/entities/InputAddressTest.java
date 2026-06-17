package uk.gov.ons.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InputAddressTest {

    @Test
	void testInputAddressCreation() {
		
		InputAddress inputAddress1 = new InputAddress();

        String addressLine1 = "ACME FLOWERS LTD";
        inputAddress1.setAddressLine1(addressLine1);
        String addressLine2 = "FLAT C CHESTERFIELD LODGE";
        inputAddress1.setAddressLine2(addressLine2);
        String addressLine3 = "78 BEULAH HILL";
        inputAddress1.setAddressLine3(addressLine3);
        String townName = "LONDON";
        inputAddress1.setTownName(townName);
        String postcode = "SE19 3EX";
        inputAddress1.setPostcode(postcode);

        String expectedAddressAll = "ACME FLOWERS LTD FLAT C CHESTERFIELD LODGE 78 BEULAH HILL LONDON SE19 3EX";
        assertEquals(expectedAddressAll, inputAddress1.getAddressAll());
		
		InputAddress inputAddress2 = new InputAddress();

        String addressLine21 = "205b Nash House Old Oak Lane";
        inputAddress2.setAddressLine1(addressLine21);
        String addressLine22 = "";
        inputAddress2.setAddressLine2(addressLine22);
        String addressLine23 = "";
        inputAddress2.setAddressLine3(addressLine23);
		inputAddress2.setTownName(townName);
        String postcode2 = "GU16 6DG";
        inputAddress2.setPostcode(postcode2);

        String expectedAddressAll2 = "205b Nash House Old Oak Lane LONDON GU16 6DG";
        assertEquals(expectedAddressAll2, inputAddress2.getAddressAll());
	}

}
