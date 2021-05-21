package ch.epfl.sdp.appart.map;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.inject.Inject;

import ch.epfl.sdp.appart.location.Location;
import dagger.hilt.android.scopes.ActivityScoped;

/**
 * This is the google map service.
 */
@ActivityScoped
public class GoogleMapService implements MapService {

    private GoogleMap map = null;
    private Activity myActivity = null;

    private GoogleMap.InfoWindowAdapter infoWindowAdapter = null;
    private SupportMapFragment mapFragment = null;
    private Runnable onReadyCallback = null;
    private GoogleMap.OnInfoWindowClickListener infoWindowClickListener = null;

    @Inject
    public GoogleMapService() {
    }

    @Override
    public void setActivity(Activity activity) {
        this.myActivity = activity;
    }


    @Override
    public void setMapFragment(SupportMapFragment fragment) {
        this.mapFragment = fragment;
    }

    @Override
    public void setInfoWindow(GoogleMap.InfoWindowAdapter infoWindow) {
        this.infoWindowAdapter = infoWindow;
    }

    @Override
    public void setOnReadyCallback(Runnable onReadyCallback) {
        this.onReadyCallback = onReadyCallback;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (infoWindowAdapter != null) {
            map.setInfoWindowAdapter(infoWindowAdapter);
        }

        if (infoWindowClickListener != null) {
            map.setOnInfoWindowClickListener(infoWindowClickListener);
        }

        try {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e) {
            throw e;
        }

        if (onReadyCallback != null) {
            onReadyCallback.run();
        }

        this.mapFragment.getView().setContentDescription("MAP READY");
    }

    @Override
    public void addMarker(Location location, Object tag,
                          boolean centerOnMarker, String title) {
        if (location == null) {
            throw new IllegalArgumentException();
        }
        LatLng pos = new LatLng(location.latitude, location.longitude);
        MarkerOptions options = new MarkerOptions().position(pos);
        Marker cardMarker = map.addMarker(options);
        if (title != null) {
            cardMarker.setTitle(title);
        }
        cardMarker.setTag(tag);
        if (centerOnMarker) {
            this.centerOnLocation(location, true);
        }


    }


    @Override
    public void centerOnLocation(Location location,
                                 boolean instant) {
        myActivity.runOnUiThread(() -> {
            CameraUpdate update =
                    CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude, location.longitude), 2.0f);
            if (instant) {
                map.moveCamera(update);
            } else {
                map.animateCamera(update);
            }
        });
    }

    @Override
    public CompletableFuture<Location> getCameraPosition() {
        CompletableFuture<Location> futureLocation = new CompletableFuture<>();
        myActivity.runOnUiThread(() -> {
            CameraPosition pos = map.getCameraPosition();
            Location cameraLoc = new Location();
            cameraLoc.longitude = pos.target.longitude;
            cameraLoc.latitude = pos.target.latitude;
            futureLocation.complete(cameraLoc);
        });
        return futureLocation;
    }

    @Override
    public void setOnInfoWindowClickListener(Consumer<Marker> infoWindowClickListener) {
        this.infoWindowClickListener =
                infoWindowClickListener::accept;
    }


}
