package ch.epfl.sdp.appart.location.place.locality;

import ch.epfl.sdp.appart.location.place.Place;

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
