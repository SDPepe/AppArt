package ch.epfl.sdp.appart.location.geocoding;

import android.content.Context;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.place.Place;
import ch.epfl.sdp.appart.location.place.address.Address;
import ch.epfl.sdp.appart.location.place.address.AddressAdapter;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class GoogleGeocodingService implements GeocodingService {

    private static final String LOCATION_PROVIDER = "network";
    private final Geocoder geocoder;

    @Inject
    public GoogleGeocodingService(@ApplicationContext Context context) {
        this.geocoder = new Geocoder(context, Locale.FRENCH);
    }

    @Override
    public CompletableFuture<Address> getAddress(Location location) {
        return CompletableFuture.supplyAsync(() -> {

            List<android.location.Address> addresses = null;
            try {
                addresses = this.geocoder.getFromLocation(location.latitude,
                        location.longitude, 5);
            } catch (IOException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
            Address a = addresses.stream()
                    .map(AddressAdapter::fromAndroidToAppartAddress)
                    .filter(Objects::nonNull).findFirst().orElseGet(null);
            return a;
        });
    }

    @Override
    public CompletableFuture<Location> getLocation(Place place) {

        return CompletableFuture.supplyAsync(() -> {
            List<android.location.Address> androidAddresses;
            try {
                androidAddresses =
                        geocoder.getFromLocationName(place.getName(), 5);
            } catch (IOException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }

            Location location = androidAddresses.stream()
                    .filter(a -> a.hasLatitude() && a.hasLongitude())
                    .findFirst()
                    .map(valid -> new Location(valid.getLongitude(),
                            valid.getLatitude()))
                    .orElseGet(null);
            return location;
        });
    }

    @Override
    public CompletableFuture<Float> getDistance(Location a, Location b) {

        return CompletableFuture.supplyAsync(() -> {
            android.location.Location l1 =
                    new android.location.Location(LOCATION_PROVIDER);
            android.location.Location l2 =
                    new android.location.Location(LOCATION_PROVIDER);
            l1.setLongitude(a.longitude);
            l1.setLatitude(a.latitude);
            l2.setLongitude(b.longitude);
            l2.setLatitude(b.latitude);

            return l1.distanceTo(l2);
        });
    }

    @Override
    public Float getDistanceSync(Location a, Location b) {

        android.location.Location l1 =
                new android.location.Location(LOCATION_PROVIDER);
        android.location.Location l2 =
                new android.location.Location(LOCATION_PROVIDER);
        l1.setLongitude(a.longitude);
        l1.setLatitude(a.latitude);
        l2.setLongitude(b.longitude);
        l2.setLatitude(b.latitude);

        return l1.distanceTo(l2);
    }

    @Override
    public CompletableFuture<Float> getDistance(Place a, Place b) {

        CompletableFuture<Location> l1 = getLocation(a);
        CompletableFuture<Location> l2 = getLocation(b);
        return CompletableFuture.allOf(l1, l2).thenCompose(aVoid -> {
            try {
                Location location1 = l1.get();
                Location location2 = l2.get();
                return getDistance(location1, location2);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
    }

}
