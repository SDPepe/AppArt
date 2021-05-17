package ch.epfl.sdp.appart.place.helper;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.location.Location;

public interface GooglePlaceHelper {

    /**
     * Based on the sdp project. Make a query to google place service to retrieve the place.
     * @return an Json String containing the data.
     * @throws IOException
     */
    CompletableFuture<String> query(Location location, int radius, String type);
}
