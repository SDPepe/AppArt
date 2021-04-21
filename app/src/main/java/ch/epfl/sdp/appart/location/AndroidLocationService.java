package ch.epfl.sdp.appart.location;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.hilt.LocationModule;

@Singleton
public final class AndroidLocationService implements LocationService {
   private final FusedLocationProviderClient locationProvider;

    private LocationCallback callback;

    @Inject
    AndroidLocationService(FusedLocationProviderClient locationProvider) {
        if(locationProvider == null) throw new IllegalArgumentException();
        this.locationProvider = locationProvider;

    }

    @Override
    public CompletableFuture<Location> getCurrentLocation() {
       CompletableFuture<Location> futureLocation = new CompletableFuture<>();

    }

    @Override
    public void setupLocationUpdate(Consumer<Location> callback) {
         try {
             LocationRequest request = LocationRequest.create();
             this.callback = new LocationCallback() {
                 @Override
                 public void onLocationResult(@NonNull LocationResult locationResult) {
                     android.location.Location loc = locationResult.getLastLocation();
                     
                     Location myLocation = new Location();
                     myLocation.latitude = loc.getLatitude();
                     myLocation.longitude = loc.getLongitude();
                     callback.accept(myLocation);
                 }
             };
            locationProvider.requestLocationUpdates(request, this.callback, Looper.getMainLooper());
        } catch(SecurityException e) {
            throw e;
        }
    }
}
