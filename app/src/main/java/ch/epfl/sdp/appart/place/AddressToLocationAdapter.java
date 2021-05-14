package ch.epfl.sdp.appart.place;

import ch.epfl.sdp.appart.location.address.Address;
import ch.epfl.sdp.appart.location.Location;

public class AddressToLocationAdapter {

    private final Location location;
    public AddressToLocationAdapter(Address address) {
        location = new Location();
    }

    public Location getLocation() {
        return location;
    }
}
