package ch.epfl.sdp.appart.map;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.location.Location;

/**
 * Service that all map services should implement
 */
public interface MapService extends OnMapReadyCallback {

    /**
     * Adds a marker at the given location on the map. The marker will carry
     * an object o, that can be null. If centerOnMarker is true the map will
     * center its view on the marker.
     *
     * @param location       the location of the marker
     * @param tag            the object the marker will carry as a tag
     * @param centerOnMarker boolean that indicates if the map needs to be
     *                       centered on the marker
     * @param title          the title of the marker
     */
    void addMarker(Location location, Object tag, boolean centerOnMarker,
                   String title);

    /**
     * Sets the support MapFragment for the map
     *
     * @param fragment the map fragment
     */
    void setMapFragment(SupportMapFragment fragment);

    /**
     * Sets up the info window that will displayed on marker click
     *
     * @param infoWindow
     */
    void setInfoWindow(GoogleMap.InfoWindowAdapter infoWindow);

    /**
     * Sets the callback for when the function will be called.
     *
     * @param onReadyCallback the callback we want to set
     */
    void setOnReadyCallback(Runnable onReadyCallback);

    /**
     * Centers the map on a specific location. It either animates the map or
     * go directly on the location depending on the instant boolean parameter.
     *
     * @param location the location we want the map's camera to be centered at
     * @param instant indicates if we want an animated movement or not
     */
    void centerOnLocation(Location location, boolean instant);

    /**
     * Sets the activity for the whole service.
     * @param activity the activity we want to give to the map service
     */
    void setActivity(Activity activity);

    /**
     * Get the current map's camera's position.
     * @return a future that will contain the location when the operation completes.
     */
    CompletableFuture<Location> getCameraPosition();
}
