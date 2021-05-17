package ch.epfl.sdp.appart.location;


import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.longitude, longitude) == 0 &&
                Double.compare(location.latitude, latitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude);
    }
}
