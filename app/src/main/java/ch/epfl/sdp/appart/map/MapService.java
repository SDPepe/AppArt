package ch.epfl.sdp.appart.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import ch.epfl.sdp.appart.location.Location;

/**
 * Service that all map services should implement
 */
public interface MapService {

    /**
     * Adds a marker at the given location on the map. The marker will carry
     * an object o, that can be null. If centerOnMarker is true the map will
     * center its view on the marker.
     *
     * @param location the location of the marker
     * @param tag the object the marker will carry as a tag
     * @param centerOnMarker boolean that indicates if the map needs to be centered on the marker
     * @param title the title of the marker
     */
    void addMarker(Location location, Object tag, boolean centerOnMarker,
                   String title);

    /**
     * Sets the support MapFragment for the map
     * @param fragment the map fragment
     */
    void setMapFragment(SupportMapFragment fragment);

    /**
     * Sets up the info window that will displayed on marker click
     * @param infoWindow
     */
    void setInfoWindow(GoogleMap.InfoWindowAdapter infoWindow);
}
