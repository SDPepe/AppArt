package ch.epfl.sdp.appart.location;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MockLocationService implements LocationService{

    private static final Location currentLocation = new Location(6.6322734, 46.5196535);

    @Override
    public CompletableFuture<Location> getCurrentLocation() {
        return CompletableFuture.completedFuture(currentLocation);
    }

    @Override
    public CompletableFuture<Void> setupLocationUpdate(Consumer<List<Location>> callback) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> teardownLocationUpdate() {
        return CompletableFuture.completedFuture(null);
    }
}
