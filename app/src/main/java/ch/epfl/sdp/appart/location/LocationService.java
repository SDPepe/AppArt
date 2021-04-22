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

    /**
     * This method is used to get the current location of the user.
     *
     * @return a future that will contain the location if the operation
     * succeeds, otherwise it will complete exceptionally
     */
    CompletableFuture<Location> getCurrentLocation();

    /**
     * This method requests location updates to the google apis. This will be
     * needed when for example, something inside an activity will need
     * regular position updates. The callback wil be called every time we
     * receive a position update.
     *
     * @param callback the function that will be executed on update
     * @return a future that will indicate if the task succeeded or not
     */
    CompletableFuture<Void> setupLocationUpdate(Consumer<List<Location>> callback);

    /**
     * This method is used to stop the position updates. Indeed, we do not
     * want the app to receive updates when it doesn't need them. This will be used,
     * when for instance, the user exits an activity that needed position
     * updates.
     *
     * @return a future that will indicate if the task succeeded or not.
     */
    CompletableFuture<Void> teardownLocationUpdate();
}
