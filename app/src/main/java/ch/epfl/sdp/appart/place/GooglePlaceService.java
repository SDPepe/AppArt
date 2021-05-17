package ch.epfl.sdp.appart.place;

import android.content.Context;
import android.util.Pair;

import com.google.android.libraries.places.api.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.location.geocoding.GeocodingService;
import ch.epfl.sdp.appart.location.geocoding.GoogleGeocodingService;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.address.Address;
import ch.epfl.sdp.appart.place.helper.GooglePlaceHelper;
import dagger.hilt.android.scopes.ActivityScoped;
import kotlin.ranges.IntRange;

@ActivityScoped
public class GooglePlaceService {

    private final GeocodingService geocoder;
    private final GooglePlaceHelper helper;

    @Inject
    public GooglePlaceService(GooglePlaceHelper helper, GeocodingService geocoder) {
        this.helper = helper;
        this.geocoder = geocoder;
    }

    /**
     * Retrieve the top (at most 20) places of interests in addition to their respective distance
     * to the given address.
     * @param address The address from which the query originate
     * @param radius the radius in which we search for the place.
     * @param type the type of place we want to find.
     * @param top the quantity of places we want to retrieve (at most 20).
     * @return
     */
    public CompletableFuture<List<Pair<PlaceOfInterest, Float>>>
        getNearbyPlacesWithDistances(Address address, int radius, String type, int top) {

        return CompletableFuture.supplyAsync(() -> {
           try {
                return getNearbyPlacesWithDistances(geocoder.getLocation(address).join(), radius, type, top).join();
           } catch (Exception e) {
               throw new CompletionException(e);
           }
        });
    }

    /**
     * Retrieve the nearest places of interest with their respective distance to the Location
     * Given as argument.
     * @param location The location from which the request originate
     * @param radius The radius in which the query originate
     * @param type the type of object you want to query
     * @return CompletableFuture<List<Pair<PlaceOfInterest, Float>>> the places with the distances (at most 20).
     */
     public CompletableFuture<List<Pair<PlaceOfInterest, Float>>>
        getNearbyPlacesWithDistances(Location location, int radius, String type, int top) {

        CompletableFuture<List<Pair<PlaceOfInterest, Float>>> result = new CompletableFuture<>();
        CompletableFuture<List<PlaceOfInterest>> placesFuture = getNearbyPlaces(location, radius, type, top);

        placesFuture.thenAccept(placesOfInterests -> {

            List<PlaceOfInterest> placesWithLocation = placesOfInterests.stream()
                    .filter(PlaceOfInterest::hasLocation)
                    .collect(Collectors.toList());

            List<CompletableFuture<Float>> locationsFutures = placesWithLocation.stream()
                    .map(place -> {
                        return geocoder.getDistance(location, place.getLocation());
                    })
                    .collect(Collectors.toList());

            CompletableFuture.allOf(locationsFutures.toArray(new CompletableFuture[locationsFutures.size()])).thenAccept(aVoid -> {

                List<Pair<PlaceOfInterest, Float>> placesWithDistances =
                        IntStream.range(0, placesWithLocation.size()).mapToObj(value -> {
                    return new Pair<>(
                            placesWithLocation.get(value),
                            locationsFutures.get(value).join()
                    );
                }).collect(Collectors.toList());
                result.complete(placesWithDistances);

            });

        });

        placesFuture.exceptionally(throwable -> {
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
    private CompletableFuture<List<PlaceOfInterest>> getNearbyPlaces(Location location, int radius, String type, int top) {
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
    private CompletableFuture<List<PlaceOfInterest>> getNearbyPlaces(Location location, int radius, String type) {
        CompletableFuture<List<PlaceOfInterest>> result = new CompletableFuture<>();

        //retrieve the raw results from the query as a Json string
        CompletableFuture<String> rawResult = helper.query(location, radius, type);
        //parse the Json String to a JSONArray to work with
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
     * Parse the JSON string given in argument to a JSON Array
     * @param rawSearch the Json String
     * @return CompletableFuture<JSONArray> the parsed data
     */
    private CompletableFuture<JSONArray> parseNearbySearch(CompletableFuture<String> rawSearch) {
        CompletableFuture<JSONArray> result = new CompletableFuture<>();
        rawSearch.thenAccept(raw -> {
            JSONObject json = null;

            try {
                json = (JSONObject) new JSONTokener(raw).nextValue();
                String status = (String) json.get("status");
                if (!status.equals("OK") && !status.equals("ZERO_RESULTS")) {
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

}
