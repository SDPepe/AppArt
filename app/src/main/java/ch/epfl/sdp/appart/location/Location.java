package ch.epfl.sdp.appart.location;


/**
 * Simple data class that represents a location with a longitude and a location
 */
public class Location {
    public double longitude;
    public double latitude;
    public Location() {}
    public Location(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

}
