package ch.epfl.sdp.appart.place;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

import ch.epfl.sdp.appart.location.place.address.Address;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.place.address.AddressFactory;

public class PlaceOfInterest {

    private String placeId;
    private Address address;
    private Location location;
    private String name;
    private Set<String> types;
    private double rating;

    public PlaceOfInterest() {}

    public String getName() {
        return name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public double getRating() {
        return rating;
    }

    public Set<String> getTypes() {
        return Sets.newHashSet(types);
    }

    public Location getLocation() {
        return location;
    }

    public Address getAddress() {
        return address;
    }

    public boolean hasLocation() {
        return location != null;
    }

    public boolean hasAddress() {
        return address != null;
    }

    public void setId(String id) {
        this.placeId = id;
    }

    public void setAddress(String address) {
        this.address = AddressFactory.makeAddressOrElse(address, () -> { return null; });
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(double longitude, double latitude) {
        if (!(Double.isNaN(longitude) || Double.isNaN(latitude))) {
            location = new Location(longitude, latitude);
        }
        else {
            location = null;
        }
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
