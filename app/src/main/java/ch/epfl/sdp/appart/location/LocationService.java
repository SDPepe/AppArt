package ch.epfl.sdp.appart.location;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * The location service interface that any real location service must implement.
 * This provides an API for getting the current location of the user,
 * transforming an address into latitude and longitude...
 */
public interface LocationService {

    CompletableFuture<Location> getCurrentLocation();

    void setupLocationUpdate(Consumer<List<Location>> callback);
}
