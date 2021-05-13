package ch.epfl.sdp.appart.place;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import ch.epfl.sdp.appart.location.Location;

public class PlaceAdapter {

    private final PlaceOfInterest placeOfInterest = new PlaceOfInterest();

    public PlaceAdapter(Place place) {
        LatLng coords = place.getLatLng();
        placeOfInterest.setAddress(new Address(place.getAddress(), new Location(coords.longitude, coords.latitude)));
        placeOfInterest.setId(place.getId());
        placeOfInterest.setName(place.getName());
    }

    public PlaceOfInterest getPlaceOfInterest() {
        return placeOfInterest;
    }
}
