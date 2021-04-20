package ch.epfl.sdp.appart.location;

import android.location.Criteria;
import android.location.LocationManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.hilt.LocationModule;

@Singleton
public final class AndroidLocationService implements LocationService {
    private final LocationManager locationManager;
    private final String locationProvider;
    private final Criteria locationCriteria;

    private CompletableFuture<Location> firstLocation = new CompletableFuture<>();

    private Location location = new Location();

    @Inject
    AndroidLocationService(LocationManager locationManager, @LocationModule.LocationProvider @Nullable String locationProvider, Criteria locationCriteria) {

        this.locationManager = locationManager;
        this.locationProvider = locationProvider;
        this.locationCriteria = locationCriteria;



    }

    private String getLocationProvider() {
        if (this.locationProvider != null) {
            return this.locationProvider;
        }

        return this.locationManager.getBestProvider(this.locationCriteria, true);
    }

    @Override
    public Location getCurrentLocation() {
        Location locationReturn = new Location();
        locationReturn.latitude = location.latitude;
        locationReturn.longitude = location.longitude;
        return locationReturn;
    }

    @Override
    public void setupLocationUpdate() {
         try {
            locationManager.requestLocationUpdates(locationProvider, 100, 100, location -> {
                this.location.latitude = location.getLatitude();
                this.location.longitude = location.getLongitude();
                this.firstLocation.complete(this.location);
            });
        } catch(SecurityException e) {
            throw e;
        }
    }
}
