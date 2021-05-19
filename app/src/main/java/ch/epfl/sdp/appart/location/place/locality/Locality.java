package ch.epfl.sdp.appart.location.place.locality;

import ch.epfl.sdp.appart.location.place.Place;

/**
 * This class represents a locality. It is a kind of location that only
 * contains a city.
 */
public class Locality implements Place {

    private final String locality;

    protected Locality(String locality) {
        this.locality = locality;
    }


    @Override
    public String getName() {
        return locality;
    }
}
