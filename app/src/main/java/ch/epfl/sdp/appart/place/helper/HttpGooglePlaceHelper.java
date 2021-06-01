package ch.epfl.sdp.appart.place.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.api.Places;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.location.Location;

/**
 * Allows to make a query to Google API by using an http request and returns
 * a Json string.
 */
public class HttpGooglePlaceHelper implements PlaceHelper {

    private final String apiKey;
    private final Context context;
    private final RequestQueue queue;

    private static final String PLACE_API_BASE_URL = "https://maps" +
            ".googleapis.com/maps/api/place/";

    private final static String JSON_SEARCH_BASE_URL = PLACE_API_BASE_URL +
            "nearbysearch/";

    private static final String PHOTO_URL = PLACE_API_BASE_URL + "photo?";

    private static final String PLACE_DETAILS_URL = PLACE_API_BASE_URL +
            "details/";

    public HttpGooglePlaceHelper(Context context) {
        this.apiKey = context.getResources().getString(R.string.maps_api_key);
        Places.initialize(context.getApplicationContext(), apiKey);
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    @Override
    public CompletableFuture<String> query(Location location, int radius,
                                           String type) {
        URL url = makeNearbyPlaceByRadiusURL(this.apiKey, location, radius,
                type);
        return makeHttpRequest(url);
    }

    @Override
    public CompletableFuture<String> query(Location location, String type) {
        URL url = makeNearbyPlaceByDistanceURL(apiKey, location, type);
        return makeHttpRequest(url);
    }

    @Override
    public CompletableFuture<Bitmap> queryImage(String photoReference,
                                                int maxHeight, int maxWidth) {
        URL url = makeImageURL(photoReference, maxHeight, maxWidth);
        return makePhotoHttpRequest(url, maxHeight, maxWidth);
    }

    private CompletableFuture<Bitmap> makePhotoHttpRequest(URL url,
                                                           int maxHeight,
                                                           int maxWidth) {
        CompletableFuture<Bitmap> futureBitmap = new CompletableFuture<>();
        ImageRequest request = new ImageRequest(url.toString(),
                futureBitmap::complete, maxWidth, maxHeight,
                ImageView.ScaleType.CENTER_CROP, null,
                error -> futureBitmap.completeExceptionally(error.getCause()));
        queue.add(request);
        return futureBitmap;
    }

    @Override
    public CompletableFuture<String> queryPlaceDetails(String placeId) {
        URL url = makeDetailsURL(placeId);
        return makeHttpRequest(url);
    }

    private URL makeDetailsURL(String placeID) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(PLACE_DETAILS_URL)
                .append("json?")
                .append("placeid=")
                .append(placeID)
                .append("&key=")
                .append(this.apiKey);
        try {

            return new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("the built url is malformed !");
        }
    }

    private URL makeImageURL(String photoReference, int maxHeight,
                             int maxWidth) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(PHOTO_URL)
                .append("photoreference=")
                .append(photoReference)
                .append("&maxheight=")
                .append(maxHeight)
                .append("&maxwidth=")
                .append(maxWidth)
                .append("&key=")
                .append(this.apiKey);
        try {

            return new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("the built url is malformed !");
        }
    }

    /**
     * Makes the http request described by the URL.
     *
     * @param url
     */
    private CompletableFuture<String> makeHttpRequest(URL url) {
        CompletableFuture<String> result = new CompletableFuture<>();
        StringRequest nearbyPlacesRequest =
                new StringRequest(Request.Method.GET, url.toString(),
                        result::complete,
                        error -> result.completeExceptionally(error.getCause()));
        queue.add(nearbyPlacesRequest);
        return result;
    }


    /**
     * Builds a URL for a request of nearby places ranked by distance
     * from the location. Therefore, this doesn't require a radius.
     *
     * @param apiKey   the Google API key
     * @param location the location around which we want the nearby
     *                 locations
     * @param type     the type of location to search
     * @return the URL
     */
    public static URL makeNearbyPlaceByDistanceURL(String apiKey,
                                                   Location location,
                                                   String type) {
        StringBuilder sb = makeURLNearbyPlaceBase(apiKey, location, type);
        sb.append("rankby=distance");
        try {

            return new URL(sb.toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("the built url is malformed !");
        }


    }

    /**
     * Builds a URL for a request of nearby places within a circle
     * centered at location and with radius radius.
     *
     * @param apiKey   the Google API key
     * @param location the location around which we want the nearby
     *                 locations
     * @param radius   the radius
     * @param type     the type of location to search
     * @return the URL
     */
    public static URL makeNearbyPlaceByRadiusURL(String apiKey,
                                                 Location location,
                                                 int radius, String type) {


        StringBuilder sb = makeURLNearbyPlaceBase(apiKey, location, type);
        sb.append("radius=").append(radius);
        try {
            return new URL(sb.toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("the built url is malformed !");
        }
    }

    private static StringBuilder makeURLNearbyPlaceBase(String apiKey,
                                                        Location location
            , String type) {
        StringBuilder sb = new StringBuilder();
        sb.append(JSON_SEARCH_BASE_URL).append("json?");
        sb.append("location=").append(location.latitude).append(",").append(location.longitude).append("&");
        sb.append("type=").append(type.trim()).append("&");
        sb.append("key=").append(apiKey).append("&");
        return sb;
    }

}

