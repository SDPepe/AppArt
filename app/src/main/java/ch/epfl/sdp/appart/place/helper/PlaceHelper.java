package ch.epfl.sdp.appart.place.helper;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.location.Location;

public interface PlaceHelper {

    /**
     * Based on the sdp project. Make a query to the in place service to
     * retrieve the place.
     *
     * @return an Json String containing the data.
     * @throws IOException
     */
    CompletableFuture<String> query(Location location, int radius, String type);

    /**
     * Based on the sdp project. Make a query to the in place service to
     * retrieve the place.
     *
     * @return an Json String containing the data.
     * @throws IOException
     */
    CompletableFuture<String> query(Location location, String type);

    CompletableFuture<Bitmap> queryImage(String photoReference, int maxHeight
            , int maxWidth);

    CompletableFuture<String> queryPlaceDetails(String placeId);
}
