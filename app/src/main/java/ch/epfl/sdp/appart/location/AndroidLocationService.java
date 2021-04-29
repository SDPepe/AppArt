package ch.epfl.sdp.appart.location;

import android.content.Context;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.R;

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
    public CompletableFuture<Location> getLocationFromName(String address) {
        CompletableFuture<Location> futureLocation = new CompletableFuture<>();
        try {
            String url = "https://maps.googleapis" +
                    ".com/maps/api/geocode/json?address=" + URLEncoder.encode(address, "UTF-8") + "&key=" + api_key;
            JsonObjectRequest request = new JsonObjectRequest(url,
                    new JSONObject(), jsonObject -> {
                double lat = 0;
                double lng = 0;
                try {
                    lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");
                    lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");
                } catch (JSONException e) {
                    e.printStackTrace();
                    futureLocation.completeExceptionally(e);
                }

                Location loc = new Location();
                loc.latitude = lat;
                loc.longitude = lng;
                futureLocation.complete(loc);
            }, error -> {
                futureLocation.completeExceptionally(error.getCause());
            });
            queue.add(request);


        } catch(UnsupportedEncodingException e) {
            futureLocation.completeExceptionally(e);
        }

        return futureLocation;
    }
}
