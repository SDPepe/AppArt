package ch.epfl.sdp.appart.location.geocoding;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.place.Place;
import ch.epfl.sdp.appart.location.place.address.Address;
import ch.epfl.sdp.appart.location.place.address.AddressFactory;

/**
 * Mock of the geocoding service. This allows testing over some classes that are using it.
 * The only address that can be returned is Dummy street 1c, 1000 DummyCity. The only location
 * that can be returned is (0, 0) and the only distances that can be computed are 0.0f.
 */
public class MockGeocodingService implements GeocodingService {

    private final Address dummyAddress = AddressFactory.makeAddress("Dummy street 1c, 1000 DummyCity");
    private final Location dummyLocation = new Location(0, 0);

    @Override
    public CompletableFuture<Address> getAddress(Location location) {
        return CompletableFuture.supplyAsync(new Supplier<Address>() {
            @Override
            public Address get() {
                return dummyAddress;
            }
        });
    }

    @Override
    public CompletableFuture<Location> getLocation(Place place) {
        return CompletableFuture.supplyAsync(new Supplier<Location>() {
            @Override
            public Location get() {
                return dummyLocation;
            }
        });
    }

    @Override
    public CompletableFuture<Float> getDistance(Location a, Location b) {
        return CompletableFuture.supplyAsync(new Supplier<Float>() {
            @Override
            public Float get() {
                return 0.0f;
            }
        });
    }

    @Override
    public CompletableFuture<Float> getDistance(Place a, Place b) {
        return CompletableFuture.supplyAsync(new Supplier<Float>() {
            @Override
            public Float get() {
                return 0.0f;
            }
        });
    }
}
