package ch.epfl.sdp.appart.location;

import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class AndroidLocationService implements LocationService {
    private final FusedLocationProviderClient locationProvider;

    private LocationCallback locationCallback;

    @Inject
    public AndroidLocationService(FusedLocationProviderClient locationProvider) {
        if (locationProvider == null) throw new IllegalArgumentException();
        this.locationProvider = locationProvider;

    }

    @Override
    public CompletableFuture<Location> getCurrentLocation() {
        CompletableFuture<Location> futureLocation = new CompletableFuture<>();
        try {
            locationProvider.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    android.location.Location androidLoc = task.getResult();
                    Location myLocation = new Location();
                    myLocation.latitude = androidLoc.getLatitude();
                    myLocation.longitude = androidLoc.getLongitude();
                    futureLocation.complete(myLocation);
                } else {
                    futureLocation.completeExceptionally(task.getException());
                }
            });
        } catch (SecurityException e) {
            throw e;
        }

        return futureLocation;
    }

    @Override
    public CompletableFuture<Void> setupLocationUpdate(Consumer<List<Location>> callback) {
        CompletableFuture<Void> futureSuccess = new CompletableFuture<>();
        try {
            LocationRequest request = LocationRequest.create();
            this.locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    List<Location> locations =
                            locationResult.getLocations().stream().map(androidLocation -> {
                                Location loc = new Location();
                                loc.longitude = androidLocation.getLongitude();
                                loc.latitude = androidLocation.getLatitude();
                                return loc;
                            }).collect(Collectors.toList());
                    callback.accept(locations);
                }
            };
            locationProvider.requestLocationUpdates(request,
                    this.locationCallback, Looper.getMainLooper()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    futureSuccess.complete(null);
                } else {
                    futureSuccess.completeExceptionally(task.getException());
                }
            });
            return futureSuccess;
        } catch (SecurityException e) {
            throw e;
        }
    }

    @Override
    public CompletableFuture<Void> teardownLocationUpdate() {
        CompletableFuture<Void> futureSuccess = new CompletableFuture<>();
        locationProvider.removeLocationUpdates(this.locationCallback).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                futureSuccess.complete(null);
            } else {
                futureSuccess.completeExceptionally(task.getException());
            }
        });
        return futureSuccess;
    }
}
