package ch.epfl.sdp.appart.place;

import android.app.Activity;
import android.content.Context;
import android.location.Geocoder;
import android.net.Uri;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlusCode;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.location.GoogleGeocodingService;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.address.Address;
import dagger.hilt.android.scopes.ActivityScoped;

@ActivityScoped
public class GooglePlaceService {

    private String apiKey;
    private final GoogleGeocodingService geocoder;
    //private PlacesClient client;

    @Inject
    public GooglePlaceService(GoogleGeocodingService geocoder) {
        this.geocoder = geocoder;
    }

    public void initialize(Context context) {
        apiKey = context.getResources().getString(R.string.maps_api_key);
        Places.initialize(context.getApplicationContext(), apiKey);
        //client = Places.createClient(context.getApplicationContext());
    }

    /**
     *
     * @param address
     * @param radius
     * @param type
     * @param top
     * @return
     */
    public CompletableFuture<List<PlaceOfInterest>> getNearbyPlaces(Address address, int radius, String type, int top) {
        CompletableFuture<List<PlaceOfInterest>> result = new CompletableFuture<>();
        CompletableFuture<List<PlaceOfInterest>> places = getNearbyPlaces(address, radius, type);
        places.thenAccept(placesOfInterests -> {
           result.thenAccept(p -> p.subList(0, top));
        });
        places.exceptionally(throwable -> {
            result.completeExceptionally(throwable);
            return null;
        });
        return result;
    }

    /**
     *
     * @param address
     * @param radius
     * @param type
     * @return
     */
    public CompletableFuture<List<PlaceOfInterest>> getNearbyPlaces(Address address, int radius, String type) {

        CompletableFuture<Location> locationFuture = geocoder.getLocation(address);
        CompletableFuture<List<PlaceOfInterest>> result = new CompletableFuture<>();

        locationFuture.thenAccept(location -> {
            if (location == null) {
                result.completeExceptionally(new IllegalStateException("could not retrieve the location"));
                return;
            }
            CompletableFuture<List<PlaceOfInterest>> placesFuture = getNearbyPlaces(location, radius, type);
            placesFuture.thenAccept(result::complete);
            placesFuture.exceptionally(throwable -> {
                result.completeExceptionally(throwable);
                return null;
            });

        });

        locationFuture.exceptionally(throwable -> {
            result.completeExceptionally(throwable);
            return null;
        });

        return result;
    }

    /**
     * Retrieve the top nearby location within the radius range.
     * @param location The <type>Location</type> from which the search is made.
     * @param radius an <type>int</type> corresponding to the radius of search in meters.
     * @param type the <type>String</type> that represent the type to search for.
     * @param top if you want to get only a subset of results, an <type>int</type>
     * @return A CompletableFuture<List<PlaceOfInterest>> the places of interest in a future.
     */
    public CompletableFuture<List<PlaceOfInterest>> getNearbyPlaces(Location location, int radius, String type, int top) {
        CompletableFuture<List<PlaceOfInterest>> placesFuture = getNearbyPlaces(location, radius, type);
        CompletableFuture<List<PlaceOfInterest>> result = new CompletableFuture<>();
        placesFuture.thenAccept(places -> {
           result.complete(places.subList(0, top));
        });
        placesFuture.exceptionally(e -> {
            result.completeExceptionally(e);
            return null;
        });
        return result;
    }

    /**
     * Retrieve the nearby location within the radius range.
     * @param location The <type>Location</type> from which the search is made.
     * @param radius an <type>int</type> corresponding to the radius of search in meters.
     * @param type the <type>String</type> that represent the type to search for.
     * @return A CompletableFuture<List<PlaceOfInterest>> the places of interest in a future.
     */
    public CompletableFuture<List<PlaceOfInterest>> getNearbyPlaces(Location location, int radius, String type) {
        CompletableFuture<List<PlaceOfInterest>> result = new CompletableFuture<>();

        CompletableFuture<String> rawResult = getRawPlaceSearch(location, radius, type);
        CompletableFuture<JSONArray> queriesResults = parseNearbySearch(rawResult);

        queriesResults.thenAccept(queriesJson -> {

            List<PlaceOfInterest> places = new ArrayList<>();

            for (int i = 0; i < queriesJson.length(); i++) {
                try {
                    PlaceOfInterest place = new PlaceOfInterest();
                    JSONObject element = (JSONObject) queriesJson.get(i);
                    place.setId(element.optString("place_id"));
                    place.setName(element.optString("name"));
                    place.setAddress(element.optString("vicinity"));
                    place.setRating(element.optDouble("rating"));

                    JSONArray typesArray = element.getJSONArray("types");
                    Set<String> types = new HashSet<>();
                    for (int j = 0; j < typesArray.length(); j ++) {
                        types.add(typesArray.optString(i));
                    }

                    place.setTypes(types);

                    JSONObject geometryJson = element.getJSONObject("geometry");
                    JSONObject locationJson = geometryJson.getJSONObject("location");
                    place.setLocation(locationJson.optDouble("lng"), locationJson.optDouble("lat"));

                    places.add(place);

                } catch (JSONException e) {
                    result.completeExceptionally(e);
                }

            }
            result.complete(places);
        });
        queriesResults.exceptionally(e -> {
           result.completeExceptionally(e);
           return null;
        });

        return result;
    }

    /**
     * Based on the sdp project. Make a query to google place to retrieve the place.
     * @return
     * @throws IOException
     */
    private CompletableFuture<String> getRawPlaceSearch(Location location, int radius, String type) {

        URL url = new NearbySearchPlaceURLBuilder(apiKey, location, radius, type).getUrl();

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

    private CompletableFuture<JSONArray> parseNearbySearch(CompletableFuture<String> rawSearch) {
        CompletableFuture<JSONArray> result = new CompletableFuture<>();
        rawSearch.thenAccept(raw -> {
            JSONObject json = null;

            try {
                json = (JSONObject) new JSONTokener(raw).nextValue();
                String status = (String) json.get("status");
                if (!(status.equals("OK") || status.equals("ZERO_RESULTS"))) {
                    result.completeExceptionally(new IllegalStateException("failed to get the query"));
                }
                JSONArray resultsJson = json.getJSONArray("results");

                if (resultsJson == null) {
                    result.completeExceptionally(new IllegalStateException("failed to convert candidates to json object"));
                } else {
                    result.complete(resultsJson);
                }

            } catch (JSONException e) {
                result.completeExceptionally(e);
            }
        });
        rawSearch.exceptionally(e -> {
            result.completeExceptionally(e);
            return null;
        });
        return result;
    }

    private static class NearbySearchPlaceURLBuilder {

        private final static String TEXT_SEARCH_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
        private URL url = null;

        public NearbySearchPlaceURLBuilder(String apiKey, Location location, int radius, String type) {
            StringBuilder sb = new StringBuilder();
            sb.append(TEXT_SEARCH_BASE_URL).append("json?");
            //sb.append("query=").append(convertSpacesToPlus(text)).append("&");
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

        public URL getUrl() {
            return url;
        }

    }

}
