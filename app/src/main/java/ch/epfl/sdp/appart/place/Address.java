package ch.epfl.sdp.appart.place;

import ch.epfl.sdp.appart.location.Location;

public class Address {
    private final String address;
    private final Location location;

    public Address(String address, Location location) {
        this.address = address;
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public Location getLocation() {
        return location;
    }
}
