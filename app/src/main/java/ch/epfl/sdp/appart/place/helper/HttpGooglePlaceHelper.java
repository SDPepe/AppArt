package ch.epfl.sdp.appart.place.helper;

import android.content.Context;

import com.google.android.libraries.places.api.Places;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.location.Location;

/**
 * Allows to make a query to Google API by using an http request and returns a Json string.
 */
public class HttpGooglePlaceHelper implements GooglePlaceHelper {

    private final String apiKey;

    public HttpGooglePlaceHelper(Context context) {
        this.apiKey = context.getResources().getString(R.string.maps_api_key);
        Places.initialize(context.getApplicationContext(), apiKey);
    }

    @Override
    public CompletableFuture<String> query(Location location, int radius, String type) {

        URL url = new HttpGooglePlaceHelper.NearbySearchPlaceURLBuilder(apiKey, location, radius, type).getUrl();

        CompletableFuture<String> result = new CompletableFuture<>();
        CompletableFuture.supplyAsync(() -> {
            InputStream stream = null;
            HttpsURLConnection connection = null;
            String potentialResult = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");

                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                connection.setDoInput(true);
                // Open communications link (network traffic occurs here).
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }

                // Retrieve the response body as an InputStream.
                stream = connection.getInputStream();
                if (stream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                    potentialResult = reader.lines().collect(Collectors.joining("\n"));
                }
            } catch (IOException e) {
                result.completeExceptionally(e);
            } finally {
                // Close Stream and disconnect HTTPS connection.
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        result.completeExceptionally(e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }

            result.complete(potentialResult);
            return null;
        });
        return result;
    }

    /**
     * Builder that encapsulate the creation of the query URL
     */
    private static class NearbySearchPlaceURLBuilder {

        private final static String TEXT_SEARCH_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
        private URL url = null;

        /**
         * Constructs the URL with a StringBuilder. The fields are hardcoded because it would make
         * the code very unreadable with constants.
         * @param apiKey
         * @param location
         * @param radius
         * @param type
         */
        public NearbySearchPlaceURLBuilder(String apiKey, Location location, int radius, String type) {
            StringBuilder sb = new StringBuilder();
            sb.append(TEXT_SEARCH_BASE_URL).append("json?");
            sb.append("location=").append(location.latitude).append(",").append(location.longitude).append("&");
            sb.append("radius=").append(radius).append("&");
            sb.append("type=").append(type.trim()).append("&");
            sb.append("key=").append(apiKey);
            try {
                url = new URL(sb.toString());
            } catch (MalformedURLException e) {
                throw new IllegalStateException("the built url is malformed !");
            }
        }

        /**
         * Get the URL of love.
         * @return URL of love.
         */
        public URL getUrl() {
            return url;
        }

    }
}
