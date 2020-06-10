package uk.gov.ons.util;

import uk.gov.ons.entities.Address;
import uk.gov.ons.entities.CSVAddress;
import uk.gov.ons.entities.InputAddress;
import uk.gov.ons.entities.Tokens;
import uk.gov.ons.json.TokeniserResponse;

public final class AddressMapper {
	
	public static Address from(InputAddress inputAddress, TokeniserResponse tokeniserResponse) throws NumberFormatException
	{
		Tokens tokens = new Tokens.TokensBuilder()
				.organisationName(tokeniserResponse.getOrganisationName())
				.departmentName(tokeniserResponse.getDepartmentName())
				.subBuildingName(tokeniserResponse.getSubBuildingName())
				.buildingName(tokeniserResponse.getBuildingName())
				.buildingNumber(!tokeniserResponse.getBuildingNumber().isEmpty() ? Short.parseShort(tokeniserResponse.getBuildingNumber()) : null)
				.paoStartNumber(!tokeniserResponse.getPaoStartNumber().isEmpty() ? Short.parseShort(tokeniserResponse.getPaoStartNumber()) : null)
				.paoStartSuffix(tokeniserResponse.getPaoStartSuffix())
				.paoEndNumber(!tokeniserResponse.getPaoEndNumber().isEmpty() ? Short.parseShort(tokeniserResponse.getPaoEndNumber()) : null)
				.paoEndSuffix(tokeniserResponse.getPaoEndSuffix())
				.saoStartNumber(!tokeniserResponse.getSaoStartNumber().isEmpty() ? Short.parseShort(tokeniserResponse.getSaoStartNumber()) : null)
				.saoStartSuffix(tokeniserResponse.getSaoStartSuffix())
				.saoEndNumber(!tokeniserResponse.getSaoEndNumber().isEmpty() ? Short.parseShort(tokeniserResponse.getSaoEndNumber()) : null)
				.saoEndSuffix(tokeniserResponse.getSaoEndSuffix())
				.streetName(tokeniserResponse.getStreetName())
				.locality(tokeniserResponse.getLocality())
				.townName(tokeniserResponse.getTownName())
				.postcode(tokeniserResponse.getPostcode())
				.addressLevel(inputAddress.getAddressLevel())
				.uprn(!inputAddress.getUprn().isEmpty() ? Long.parseLong(inputAddress.getUprn()): null)
				.latitude(inputAddress.getLatitude())
				.longitude(inputAddress.getLongitude())
				.addressLine1(inputAddress.getAddressLine1())
				.addressLine2(inputAddress.getAddressLine2())
				.addressLine3(inputAddress.getAddressLine3())
				.build();
		
		Address.CountryCode countryCode;
		
		switch (inputAddress.getRegion().charAt(0)) {
		case 'E':
			countryCode = Address.CountryCode.E;
			break;
		case 'W':
			countryCode = Address.CountryCode.W;
			break;
		case 'N':
			countryCode = Address.CountryCode.N;
			break;
		case 'S':
			countryCode = Address.CountryCode.W;
			break;
		default:
			countryCode = null;	
		}
		
		Address address = new Address(
				!inputAddress.getUprn().isEmpty() ? Long.parseLong(inputAddress.getUprn()): null, 
				tokeniserResponse.getPostcodeIn(),
				tokeniserResponse.getPostcodeOut(),
				inputAddress instanceof CSVAddress ? ((CSVAddress) inputAddress).getAbpCode(): "", // No ABP Code in msg
				inputAddress.getAddressType(),
				inputAddress.getEstabType(),
				countryCode, 
				tokeniserResponse.getPostcode(),
				tokens);
		
		return address;
	}
}
