package ch.epfl.sdp.appart.location.geocoding;


import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.address.Address;

public interface GeocodingService {

    /**
     * Retrieve the Address if it exists at the given location.
     * @param location the Address for which we want to find the location.
     *                 if the Address is not found it will be null.
     * @return CompletableFuture<Location> containing the location
     */
    CompletableFuture<Address> getAddress(Location location);

    /**
     * Retrieve the location of the given address.
     * @param address the Address for which we want to find the location
     * @return CompletableFuture<Location> containing the location
     */
    CompletableFuture<Location> getLocation(Address address);

    /**
     * Retrieve the distance between two locations in meters
     * @param a the source location
     * @param b the target location
     * @return CompletableFuture<Float> containing the distance in meters
     */
    CompletableFuture<Float> getDistance(Location a, Location b);

    /**
     * Retrieve the distance between two Addresses in meters
     * @param a the source location
     * @param b the target location
     * @return CompletableFuture<Float> containing the distance in meters
     */
    CompletableFuture<Float> getDistance(Address a, Address b);
}
