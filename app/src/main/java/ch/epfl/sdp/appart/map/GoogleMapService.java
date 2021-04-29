package ch.epfl.sdp.appart.map;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.location.Location;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.ActivityScoped;

@ActivityScoped
public class GoogleMapService implements MapService {

    private GoogleMap map = null;
    private Activity myActivity = null;

    private GoogleMap.InfoWindowAdapter infoWindowAdapter = null;
    private SupportMapFragment mapFragment = null;
    private Runnable onReadyCallback = null;

    @Inject
    public GoogleMapService() {}

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (infoWindowAdapter != null) {
            map.setInfoWindowAdapter(infoWindowAdapter);
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

    public void addMarker(Location location, Object tag,
                          boolean centerOnMarker, String title) {
        if (location == null) {
            throw new IllegalArgumentException();
        }
        Marker cardMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(location.latitude, location
                        .longitude)));
        if (title != null) {
            cardMarker.setTitle(title);
        }
        cardMarker.setTag(tag);

        if (centerOnMarker) {
            this.centerOnLocation(location, true);
        }
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
    public /*static*/ void centerOnLocation(Location location,
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
    public void setActivity(Activity activity) {
        this.myActivity = activity;
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
    public void setOnReadyCallback(Runnable onReadyCallback) {
        //No need to check for null here, the check line 56 makes more sense
        this.onReadyCallback = onReadyCallback;
    }
}
