package ch.epfl.sdp.appart.location;

/**
 * The location service interface that any real location service must implement.
 * This provides an API for getting the current location of the user,
 * transforming an address into latitude and longitude...
 */
public interface LocationService {

    Location getCurrentLocation();
    void setupLocationUpdate();
}
