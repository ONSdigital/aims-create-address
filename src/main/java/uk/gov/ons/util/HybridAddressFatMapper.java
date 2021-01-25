package uk.gov.ons.util;

import uk.gov.ons.entities.HybridAddressFat;
import uk.gov.ons.entities.Lpi;
import uk.gov.ons.entities.UnitAddress;
import uk.gov.ons.json.TokeniserResponse;
import uk.gov.ons.util.CreateAddressConstants.CountryCode;

import java.util.List;

public final class HybridAddressFatMapper {
	
	public static HybridAddressFat from(UnitAddress unitAddress, TokeniserResponse tokeniserResponse) throws NumberFormatException
	{
		Lpi lpi = new Lpi.LpiBuilder()
				.organisationName(tokeniserResponse.getOrganisationName()) // Will have been upper cased by parser
				.organisation(unitAddress.getOrganisationName())
				.departmentName(tokeniserResponse.getDepartmentName())
				.subBuildingName(tokeniserResponse.getSubBuildingName())
				.buildingName(tokeniserResponse.getBuildingName())
				.buildingNumber(!tokeniserResponse.getBuildingNumber().isEmpty() ? Short.parseShort(tokeniserResponse.getBuildingNumber()) : null)
				.streetName(tokeniserResponse.getStreetName())
				.locality(tokeniserResponse.getLocality())
				.townName(tokeniserResponse.getTownName())
				.townNameUnitAddress(unitAddress.getTownName())
				.postcode(tokeniserResponse.getPostcode())
				.easting(!unitAddress.getBngEasting().isEmpty() ? Float.parseFloat(unitAddress.getBngEasting()): null)
				.northing(!unitAddress.getBngNorthing().isEmpty() ? Float.parseFloat(unitAddress.getBngNorthing()): null)
				.latitude(unitAddress.getLatitude())
				.longitude(unitAddress.getLongitude())
				.paoStartNumber(!tokeniserResponse.getPaoStartNumber().isEmpty() ? Short.parseShort(tokeniserResponse.getPaoStartNumber()) : null)
				.paoStartSuffix(tokeniserResponse.getPaoStartSuffix())
				.postcodeLocator(tokeniserResponse.getPostcode())
				.saoStartNumber(!tokeniserResponse.getSaoStartNumber().isEmpty() ? Short.parseShort(tokeniserResponse.getSaoStartNumber()) : null)
				.streetDescriptor(tokeniserResponse.getStreetName())
				.uprn(!unitAddress.getUprn().isEmpty() ? Long.parseLong(unitAddress.getUprn()): null)
				.addressLine1(unitAddress.getAddressLine1())
				.addressLine2(unitAddress.getAddressLine2())
				.addressLine3(unitAddress.getAddressLine3())
				.paoEndNumber(!tokeniserResponse.getPaoEndNumber().isEmpty() ? Short.parseShort(tokeniserResponse.getPaoEndNumber()) : null)
				.paoEndSuffix(tokeniserResponse.getPaoEndSuffix())
				.saoEndNumber(!tokeniserResponse.getSaoEndNumber().isEmpty() ? Short.parseShort(tokeniserResponse.getSaoEndNumber()) : null)
				.saoEndSuffix(tokeniserResponse.getSaoEndSuffix())
				.saoStartSuffix(tokeniserResponse.getSaoStartSuffix())
				.lpiLogicalStatus((byte) 1)
				.language("ENG")
				.build();
			
		CountryCode countryCode;
		
		switch (unitAddress.getRegion().charAt(0)) {
		case 'E':
			countryCode = CountryCode.E;
			break;
		case 'W':
			countryCode = CountryCode.W;
			break;
		case 'N':
			countryCode = CountryCode.N;
			break;
		case 'S':
			countryCode = CountryCode.W;
			break;
		case 'L':
			countryCode = CountryCode.L;
			break;
		case 'M':
			countryCode = CountryCode.M;
			break;
		case 'J':
			countryCode = CountryCode.J;
			break;
		default:
			countryCode = null;	
		}

		String postcodeStreetTown = (
					tokeniserResponse.getPostcode() + "_" +
					tokeniserResponse.getStreetName() + "_" +
					tokeniserResponse.getTownName() )
				.replace(".","")
				.replace("'","");

		return new HybridAddressFat(
				lpi.getUprn(),
				List.of(lpi),
				unitAddress.getAbpCode(),
				unitAddress.getAddressType(),
				unitAddress.getEstabType(),
				tokeniserResponse.getPostcode(),
				countryCode,
				postcodeStreetTown,
				tokeniserResponse.getTownName());
	}
}
