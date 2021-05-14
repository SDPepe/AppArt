package ch.epfl.sdp.appart.place;

import java.util.HashSet;
import java.util.Set;

import ch.epfl.sdp.appart.location.address.Address;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.address.AddressFactory;

public class PlaceOfInterest {

    private String placeId;
    private Address address;
    private Location location;
    private String name;
    private Set<String> types;
    private double rating;

    PlaceOfInterest() {}

    public void setId(String id) {
        this.placeId = id;
    }

    public void setAddress(String address) {
        this.address = AddressFactory.makeAddress(address);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(double longitude, double latitude) {
        location = new Location(longitude, latitude);
    }

    public void setTypes(Set<String> types) {
        this.types = new HashSet<>();
        for (String t : types) {
            if (!t.isEmpty()) {
                this.types.add(t);
            }
        }
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

}
