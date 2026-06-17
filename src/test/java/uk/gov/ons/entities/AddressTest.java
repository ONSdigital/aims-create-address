package uk.gov.ons.entities;

import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Point;
import uk.gov.ons.util.CreateAddressConstants.CountryCode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressTest {

    private final String organisationName = "ACME FLOWERS LTD";
    private final Short buildingNumber = Short.parseShort("78");
    private final String buildingName = "CHESTERFIELD LODGE";
    private final String subBuildingName = "FLAT C";
    private final Short paoStartNumber = Short.parseShort("78");
    private final String saoStartSuffix = "C";
    private final String addressLine1 = "ACME FLOWERS LTD";
    private final String addressLine2 = "FLAT C CHESTERFIELD LODGE";
    private final String addressLine3 = "78 BEULAH HILL";
    private final String streetName = "BEULAH HILL";
    private final String townName = "LONDON";
    private final String postcode = "SE19 3EX";
    private final String postcodeIn = "3EX";
    private final String postcodeOut = "SE19";
    private final String addressLevel = "U";
    private final String latitude = "55.55";
    private final String longitude = "-1.23";
    private final Point location = new Point(55.55, -1.23);
    private final Long uprn = 99L;
    private final String classificationCode = "RD03";
    private final String censusAddressType = "HH";
    private final String censusEstabType = "Household";
    private final Long censusEstabUprn = 808L;
    private final CountryCode countryCode = CountryCode.E;

    private final Tokens tokens = new Tokens.TokensBuilder()
            .organisationName(organisationName)
            .subBuildingName(subBuildingName)
            .buildingName(buildingName)
            .buildingNumber(buildingNumber)
            .paoStartNumber(paoStartNumber)
            .saoStartSuffix(saoStartSuffix)
            .streetName(streetName)
            .townName(townName)
            .addressLevel(addressLevel)
            .uprn(uprn)
            .latitude(latitude)
            .longitude(longitude)
            .addressLine1(addressLine1)
            .addressLine2(addressLine2)
            .addressLine3(addressLine3)
            .postcode(postcode).build();

    String postcodeStreetTown = (
            postcode + "_" +
                    streetName + "_" +
                    townName)
            .replace(".", "")
            .replace("'", "");

    private Address address = new Address(uprn, postcodeIn, postcodeOut, classificationCode, censusAddressType, censusEstabType, censusEstabUprn, countryCode, postcode, postcodeStreetTown, tokens.getAddressAll(), tokens, List.of(new Address.Lpi()), List.of(new Address.Nisra()));

    @Test
    void testAddressCreation() {

        String addressAll = "ACME FLOWERS LTD FLAT C CHESTERFIELD LODGE 78 BEULAH HILL LONDON SE19 3EX";
        assertEquals(addressAll, address.getTokens().getAddressAll());
        assertEquals(location, address.getTokens().getLocation());

        // Null Point data
        Tokens tokensNullLatLng = new Tokens.TokensBuilder()
                .latitude(null)
                .longitude(null)
                .build();

        Address addressNullLatLng = address;
        addressNullLatLng.setTokens(tokensNullLatLng);

        assertEquals(0.0, addressNullLatLng.getTokens().getLocation().getX());
        assertEquals(0.0, addressNullLatLng.getTokens().getLocation().getY());

        // Non parseable Point data
        Tokens tokensCharLatLng = new Tokens.TokensBuilder()
                .latitude("X")
                .longitude("Y")
                .build();

        Address addressCharLatLng = address;
        addressCharLatLng.setTokens(tokensCharLatLng);

        assertEquals(0.0, addressCharLatLng.getTokens().getLocation().getX());
        assertEquals(0.0, addressCharLatLng.getTokens().getLocation().getY());
    }
}
