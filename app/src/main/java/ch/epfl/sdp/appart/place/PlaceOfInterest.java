package ch.epfl.sdp.appart.place;

import ch.epfl.sdp.appart.location.Location;

public class PlaceOfInterest {

    private String id;
    private Address address;
    private String name;
    private String type;

    PlaceOfInterest() {}

    public void setId(String id) {
        this.id = id;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

}
