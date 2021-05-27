package ch.epfl.sdp.appart.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;


/**
 * This class represents the android location service. It uses a
 * FusedLocationProviderClient to get location information.
 * <p>
 * To get a location from name we bypass the Geocoder because it fails in
 * cirrus and sometimes on the local emulator. It seems very unstable.
 */
@Singleton
public final class AndroidLocationService implements LocationService {
    private final FusedLocationProviderClient locationProvider;

    private LocationCallback locationCallback;
    private final RequestQueue queue;
    private final String api_key;

    @Inject
    public AndroidLocationService(Context context) {
        if (context == null)
            throw new IllegalArgumentException();
        this.locationProvider =
                LocationServices.getFusedLocationProviderClient(context);

        this.queue = Volley.newRequestQueue(context);
        this.api_key = context.getResources().getString(R.string.maps_api_key);

    }

    @SuppressLint("MissingPermission")
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
                    futureLocation.complete(null);
                }
            });
        } catch (SecurityException e) {
            throw e;
        }
        return futureLocation;
    }

    @SuppressLint("MissingPermission")
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
}
