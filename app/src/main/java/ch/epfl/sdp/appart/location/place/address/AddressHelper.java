package ch.epfl.sdp.appart.location.place.address;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.location.place.Place;
import ch.epfl.sdp.appart.location.place.locality.LocalityFactory;

public class AddressHelper {

    public static Place buildAddress(Ad ad) {
        String city = ad.getCity();
        Matcher extractPostalCodeMatcher = Pattern.compile("\\d+").matcher(city);
        Place place;
        if(extractPostalCodeMatcher.find()) {
            String postalCode = extractPostalCodeMatcher.group();
            String locality =
                    ad.getCity().replaceAll("\\d+", "");
            place = AddressFactory.makeAddress(ad.getStreet(), postalCode, locality);

        } else {
            place = LocalityFactory.makeLocality(city);
        }
       return  place;
    }
}
