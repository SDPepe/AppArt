package ch.epfl.sdp.appart.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.os.ConfigurationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
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
    private final Geocoder geocoder;

    @Inject
    public AndroidLocationService(Context context) {
        if (context == null)
            throw new IllegalArgumentException();
        this.locationProvider = LocationServices.getFusedLocationProviderClient(context);
        this.geocoder = new Geocoder(context, ConfigurationCompat.getLocales(context.getResources().getConfiguration()).get(0));
        /*try {
            this.locationProvider.setMockMode(true).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Log.d("MOCK LOCATION", "Successful");
                }
            });
            android.location.Location fakeLoc = new android.location.Location("gps");
            fakeLoc.setAccuracy(5.0f);
            fakeLoc.setLongitude(5.0);
            fakeLoc.setLatitude(5.0);
            this.locationProvider.setMockLocation(fakeLoc).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Log.d("MOCK LOCATION", "Successful");
                }
            });
        } catch(SecurityException e) {
            throw e;
        }*/

    }

    @Override
    public CompletableFuture<Location> getCurrentLocation() {
        CompletableFuture<Location> futureLocation = new CompletableFuture<>();
        try {
            locationProvider.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, null).addOnCompleteListener(task -> {
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

        try {
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

    @Override
    public Location getLocationFromName(String name) {
        if (name == null) return null;

        final List<Address> addresses;
        try {
            addresses = this.geocoder.getFromLocationName(name, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (addresses.isEmpty()) return null;

        Location cityLoc = new Location();
        cityLoc.latitude = addresses.get(0).getLatitude();
        cityLoc.longitude = addresses.get(0).getLongitude();

        return cityLoc;
    }
}
