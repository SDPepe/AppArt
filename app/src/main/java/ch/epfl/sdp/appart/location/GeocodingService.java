package ch.epfl.sdp.appart.location;


import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.location.address.Address;

public interface GeocodingService {
    CompletableFuture<Address> getAddress(Location location);
    CompletableFuture<Location> getLocation(Address address);
}
