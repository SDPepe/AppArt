package ch.epfl.sdp.appart.location;

import android.content.Context;
import android.location.Geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.location.address.Address;
import ch.epfl.sdp.appart.location.address.AddressAdapter;
import dagger.hilt.android.scopes.ActivityScoped;

@ActivityScoped
public class GoogleGeocodingService implements GeocodingService {

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

}
