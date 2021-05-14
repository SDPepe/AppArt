package ch.epfl.sdp.appart.location.address;
import java.util.regex.Pattern;

public class AddressFactory {

    private static final String LOCALITY_PATTERN = "^[a-zA-Z-']+";
    private static final String STREET_PATTERN = "^[a-zA-Z '-]+\\d+[a-zA-Z]?$";
    private static final String POSTAL_CODE_PATTERN = "^\\d{4}$";
    private static final String ADDRESS_WITH_POSTAL_CODE_PATTERN = "^[a-zA-Z '-]+\\d+[a-zA-Z]?, \\d{4} [a-zA-Z-']+$";
    private static final String ADDRESS_WITHOUT_POSTAL_CODE_PATTERN = "^[a-zA-Z '-]+\\d+[a-zA-Z]?, [a-zA-Z-']+$";

    private static final Pattern localityPattern = Pattern.compile(LOCALITY_PATTERN);
    private static final Pattern streetPattern = Pattern.compile(STREET_PATTERN);
    private static final Pattern postalCodePattern = Pattern.compile(POSTAL_CODE_PATTERN);
    private static final Pattern addressWithPostalCodePattern = Pattern.compile(ADDRESS_WITH_POSTAL_CODE_PATTERN);
    private static final Pattern addressWithoutPostalCodePattern = Pattern.compile(ADDRESS_WITHOUT_POSTAL_CODE_PATTERN);

    private AddressFactory() {}

    public static Address makeAddress(String street, String postalCode, String locality) {

        String streetFiltered = street.trim();
        String localityFiltered = locality.trim();
        String postalCodeFiltered = postalCode.trim();

        if (!streetPattern.matcher(streetFiltered).matches()) {
            throw new MalformedAddressException("malformed street string, expected format : " + STREET_PATTERN + "  but was " + streetFiltered);
        }

        if (!localityPattern.matcher(localityFiltered).matches()) {
            throw new MalformedAddressException("malformed locality string, expected format : " + LOCALITY_PATTERN + "but was :" + localityFiltered);
        }

        if (!postalCodePattern.matcher(postalCodeFiltered).matches()) {
            throw new MalformedAddressException("malformed postal code string, expected format : " + POSTAL_CODE_PATTERN + "but was :" + postalCode);
        }

        return new Address(streetFiltered, localityFiltered, postalCodeFiltered);
    }

    public static Address makeAddress(String address) {

        String filteredAddress = address.trim();

        if (addressWithPostalCodePattern.matcher(filteredAddress).matches()) {
            return new Address("", "", "");
        }

        if (addressWithoutPostalCodePattern.matcher(filteredAddress).matches()) {
            return new Address("", "");
        }

        throw new MalformedAddressException("the address was malformed : expected formats : "
                + ADDRESS_WITH_POSTAL_CODE_PATTERN + " or " + ADDRESS_WITHOUT_POSTAL_CODE_PATTERN + " but was : " + filteredAddress);

    }

}
