package ch.epfl.sdp.appart.location.geocoding;

import android.content.Context;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.address.Address;
import ch.epfl.sdp.appart.location.address.AddressAdapter;
import ch.epfl.sdp.appart.location.geocoding.GeocodingService;
import dagger.hilt.android.scopes.ActivityScoped;

@ActivityScoped
public class GoogleGeocodingService implements GeocodingService {

    private static final String LOCATION_PROVIDER = "network";
    private final Geocoder geocoder;

    @Inject
    public GoogleGeocodingService(Context context) {
        this.geocoder = new Geocoder(context, Locale.FRENCH);
    }

    @Override
    public CompletableFuture<Address> getAddress(Location location) {
        CompletableFuture<Address> result = new CompletableFuture<>();
        CompletableFuture.supplyAsync(() -> {

            List<android.location.Address> addresses = null;
            try {
                addresses = this.geocoder.getFromLocation(location.latitude, location.longitude, 5);
            } catch (IOException e) {
                e.printStackTrace();
                result.completeExceptionally(e);
            }
            Address a = addresses.stream()
                    .map(AddressAdapter::fromAndroidToAppartAddress)
                    .filter(Objects::nonNull).findFirst().orElseGet(null);
            result.complete(a);
            return null;
        });
        return result;
    }

    @Override
    public CompletableFuture<Location> getLocation(Address address) {

        CompletableFuture<Location> result = new CompletableFuture<>();
        CompletableFuture.supplyAsync(() -> {
            List<android.location.Address> androidAddresses;
            try {
                androidAddresses = geocoder.getFromLocationName(address.getAddress(), 5);
            } catch (IOException e) {
                e.printStackTrace();
                result.completeExceptionally(e);
                return null;
            }

            Location location = androidAddresses.stream()
                    .filter(a -> a.hasLatitude() && a.hasLongitude())
                    .findFirst()
                    .map(valid -> new Location(valid.getLongitude(), valid.getLatitude()))
                    .orElseGet(null);
            result.complete(location);
            return null;
        });

        return result;
    }

    @Override
    public CompletableFuture<Float> getDistance(Location a, Location b) {

        CompletableFuture<Float> distanceFuture = new CompletableFuture<>();
        CompletableFuture.supplyAsync(() -> {
            try {
                android.location.Location l1 = new android.location.Location(LOCATION_PROVIDER);
                android.location.Location l2 = new android.location.Location(LOCATION_PROVIDER);
                l1.setLongitude(a.longitude);
                l1.setLatitude(a.latitude);
                l2.setLongitude(b.longitude);
                l2.setLatitude(b.latitude);

                distanceFuture.complete(l1.distanceTo(l2));
            } catch(Exception e) {
                distanceFuture.completeExceptionally(e);
            }
            return null;
        });

        return distanceFuture;
    }

    @Override
    public CompletableFuture<Float> getDistance(Address a, Address b) {

        CompletableFuture<Location> l1 = getLocation(a);
        CompletableFuture<Location> l2 = getLocation(b);
        CompletableFuture<Float> result = new CompletableFuture<>();

        CompletableFuture.allOf(l1, l2).thenAccept(aVoid -> {
            try {
                Location location1 = l1.get();
                Location location2 = l2.get();
                CompletableFuture<Float> distanceFuture = getDistance(location1, location2);
                distanceFuture.thenAccept(result::complete);
                distanceFuture.exceptionally(throwable -> {
                    result.completeExceptionally(throwable);
                    return null;
                });
            } catch (ExecutionException | InterruptedException e) {
                result.completeExceptionally(e);
            }
        });
        return result;
    }

}
