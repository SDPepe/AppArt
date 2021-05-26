package ch.epfl.sdp.appart.place.helper;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

    public HttpGooglePlaceHelper(Context context) {
        this.apiKey = context.getResources().getString(R.string.maps_api_key);
        Places.initialize(context.getApplicationContext(), apiKey);
        this.context = context;
    }

    @Override
    public CompletableFuture<String> query(Location location, int radius,
                                           String type) {
        CompletableFuture<String> result = new CompletableFuture<>();
        URL url =
                new HttpGooglePlaceHelper.NearbySearchPlaceURLBuilder(apiKey,
                        location, radius, type).getUrl();
        StringRequest nearbyPlacesRequest =
                new StringRequest(Request.Method.GET, url.toString(),
                        result::complete,
                        error -> result.completeExceptionally(error.getCause()));
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(nearbyPlacesRequest);
        return result;

    }

    /**
     * Builder that encapsulate the creation of the query URL
     */
    private static class NearbySearchPlaceURLBuilder {

        private final static String TEXT_SEARCH_BASE_URL = "https://maps" +
                ".googleapis.com/maps/api/place/nearbysearch/";
        private URL url = null;

        /**
         * Constructs the URL with a StringBuilder. The fields are hardcoded
         * because it would make
         * the code very unreadable with constants.
         *
         * @param apiKey
         * @param location
         * @param radius
         * @param type
         */
        public NearbySearchPlaceURLBuilder(String apiKey, Location location,
                                           int radius, String type) {
            StringBuilder sb = new StringBuilder();
            sb.append(TEXT_SEARCH_BASE_URL).append("json?");
            sb.append("location=").append(location.latitude).append(",").append(location.longitude).append("&");
            sb.append("radius=").append(radius).append("&");
            sb.append("name=").append(type.trim()).append("&");
            sb.append("key=").append(apiKey);
            try {
                url = new URL(sb.toString());
            } catch (MalformedURLException e) {
                throw new IllegalStateException("the built url is malformed !");
            }
        }

        /**
         * Get the URL of love.
         *
         * @return URL of love.
         */
        public URL getUrl() {
            return url;
        }

    }
}
