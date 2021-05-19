package ch.epfl.sdp.appart.location.place.locality;

import java.util.regex.Pattern;

import ch.epfl.sdp.appart.location.place.address.MalformedAddressException;

public class LocalityFactory {

    public static final String LOCALITY_PATTERN = "[a-zA-ZÀ-ÿ '-]+";
    public static final Pattern localityPattern =
            Pattern.compile(LOCALITY_PATTERN);

    public static Locality makeLocality(String locality) {
        if (locality == null) throw new IllegalArgumentException();

        String localityFiltered = locality.trim();

        if (!localityPattern.matcher(localityFiltered).matches()) {
            throw new MalformedLocalityException("malformed locality string, " +
                    "expected format : " + LOCALITY_PATTERN + "but was :" + localityFiltered);
        }

        return new Locality(localityFiltered);
    }
}
