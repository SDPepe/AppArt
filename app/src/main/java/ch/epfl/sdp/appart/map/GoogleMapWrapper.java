package ch.epfl.sdp.appart.map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ch.epfl.sdp.appart.location.Location;

public class GoogleMapWrapper implements OnMapReadyCallback {

    private GoogleMap map;

    private GoogleMap.InfoWindowAdapter infoWindowAdapter = null;
    private final SupportMapFragment mapFragment;
    private Runnable onReadyCallback = null;

    public GoogleMapWrapper(GoogleMap.InfoWindowAdapter infoWindowAdapter, SupportMapFragment mapFragment) {
        if(mapFragment == null) throw new IllegalArgumentException();


        this.infoWindowAdapter = infoWindowAdapter;
        this.mapFragment = mapFragment;
    }

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
        if(title != null) {
            cardMarker.setTitle(title);
        }
        cardMarker.setTag(tag);

        if (centerOnMarker) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(cardMarker.getPosition(), 12.0f));
        }
    }

    public void setOnReadyCallback(Runnable onReadyCallback) {
        //No need to check for null here, the check line 56 makes more sense
        this.onReadyCallback = onReadyCallback;
    }
}
