package ch.epfl.sdp.appart.location.address;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressFactory {

    private static final String LOCALITY_PATTERN = "[a-zA-ZÀ-ÿ '-]+";
    private static final String STREET_PATTERN = "[a-zA-ZÀ-ÿ '-]+\\d+[a-zA-Z]?";
    private static final String POSTAL_CODE_PATTERN = "\\d{4}";
    private static final String ADDRESS_WITH_POSTAL_CODE_PATTERN = "[a-zA-ZÀ-ÿ '-]+\\d+[a-zA-Z]?, ?\\d{4} [a-zA-ZÀ-ÿ '-]+";
    private static final String ADDRESS_WITHOUT_POSTAL_CODE_PATTERN = "[a-zA-ZÀ-ÿ '-]+\\d+[a-zA-Z]?, ?[a-zA-ZÀ-ÿ '-]+";

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
        String[] split = filteredAddress.split(",");
        Matcher streetMatcher = streetPattern.matcher(split[0]);
        Matcher localityMatcher = null;
        if (split.length > 1) {
            localityMatcher = localityPattern.matcher(split[1]);
        }

        if (addressWithPostalCodePattern.matcher(filteredAddress).matches()) {

            Matcher postalCodeMatcher = postalCodePattern.matcher(split[1]);
            //we can find and match safely
            streetMatcher.find();
            postalCodeMatcher.find();
            localityMatcher.find();
            String street = streetMatcher.group();
            String postalCode = postalCodeMatcher.group();
            String locality = localityMatcher.group();

            return new Address(street, postalCode, locality);

        }

        if (addressWithoutPostalCodePattern.matcher(filteredAddress).matches()) {

            //we can find and match safely
            streetMatcher.find();
            localityMatcher.find();
            String street = streetMatcher.group();
            String locality = localityMatcher.group();
            return new Address(street, locality);

        }

        throw new MalformedAddressException("the address was malformed : expected formats : "
                + ADDRESS_WITH_POSTAL_CODE_PATTERN + " or " + ADDRESS_WITHOUT_POSTAL_CODE_PATTERN + " but was : " + filteredAddress);

    }

    public static Address makeAddressOrElse(String address, Supplier<Address> orElse) {
        try {
            return makeAddress(address);
        } catch (MalformedAddressException e) {
            return orElse.get();
        }
    }

    public static Address makeAddressOrElse(String street, String postalCode, String locality, Supplier<Address> orElse) {
        try {
            return makeAddress(street, postalCode, locality);
        } catch (MalformedAddressException e) {
            return orElse.get();
        }
    }

}
