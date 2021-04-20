package ch.epfl.sdp.appart.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import javax.inject.Inject;

import ch.epfl.sdp.appart.hilt.LocationModule;

public class AndroidLocationService implements LocationService {
    private final LocationManager locationManager;
    private final String locationProvider;
    private final Criteria locationCriteria;

    private Location location = new Location();

    @Inject
    AndroidLocationService(LocationManager locationManager, @LocationModule.LocationProvider @Nullable String locationProvider, Criteria locationCriteria) {
        this.locationManager = locationManager;
        this.locationProvider = locationProvider;
        this.locationCriteria = locationCriteria;

        /*try {

            locationManager.requestLocationUpdates(locationProvider, 100, 100, location -> {
                this.location.latitude = location.getLatitude();
                this.location.longitude = location.getLongitude();
            });
        } catch(SecurityException e) {
            throw e;
        }*/

    }

    private String getLocationProvider() {
        if (this.locationProvider != null) {
            return this.locationProvider;
        }

        return this.locationManager.getBestProvider(this.locationCriteria, true);
    }

    @Override
    public Location getCurrentLocation() {

        return null;
    }
}
