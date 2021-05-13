package ch.epfl.sdp.appart.place;

import android.app.Activity;
import android.content.Context;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;

import ch.epfl.sdp.appart.R;
import dagger.hilt.android.scopes.ActivityScoped;

@ActivityScoped
public class GooglePlaceService {

    private final static String googlePlaceBaseUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/";
    private String apiKey;
    private PlacesClient client;

    @Inject
    public GooglePlaceService() {}

    public void initialize(Activity activity) {
        apiKey = activity.getResources().getString(R.string.maps_api_key);
        Places.initialize(activity.getApplicationContext(), apiKey);
        client = Places.createClient(activity.getApplicationContext());
        CompletableFuture<List<PlaceOfInterest>> a = getPlacesOfInterests(new Address("Rue de Neuchâtel 3"));
        a.thenAccept(aa -> {
           int i = 0;
        });
        a.exceptionally(e -> {
            return null;
        });

    }

    public CompletableFuture<List<PlaceOfInterest>> getPlacesOfInterests(Address address) {
        CompletableFuture<List<PlaceOfInterest>> result = new CompletableFuture<>();

        CompletableFuture<String> rawResult = getRawPlaceSearch(address.getAddress());
        CompletableFuture<JSONArray> extract = extractCandidates(rawResult);
        CompletableFuture<List<String>> placeIds = getPlacesIds(extract);

        placeIds.thenAccept(ids -> {

            CompletableFuture<Place>[] places = new CompletableFuture[ids.size()];

            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
            for (int i = 0; i < ids.size(); i++) {
                String id = ids.get(i);
                CompletableFuture<Place> placeFuture = new CompletableFuture<>();
                FetchPlaceRequest request = FetchPlaceRequest.newInstance(id, placeFields);
                client.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place place = response.getPlace();
                    placeFuture.complete(place);
                }).addOnFailureListener(e -> {
                    placeFuture.completeExceptionally(e);
                });
                places[i] = placeFuture;
            }

            CompletableFuture.allOf(places).thenAccept(ignoredVoid -> {
                List<PlaceOfInterest> placesOfInterest = new ArrayList<>();
                for (int i = 0; i < places.length; i++) {

                }
                result.complete(placesOfInterest);
            }).exceptionally(e -> {
                result.completeExceptionally(e);
                return null;
            });


        });

        placeIds.exceptionally(e -> {
            result.completeExceptionally(e);
           return null;
        });

        return result;
    }


    /**
     * Extract the place ids form the provided <type>JSONArray</type>.
     * @param candidates a  <type>CompletableFuture<JSONArray></></type>
     * @return <type>CompletableFuture<List<String>></type>
     */
    private CompletableFuture<List<String>> getPlacesIds(CompletableFuture<JSONArray> candidates) {
        CompletableFuture<List<String>> result = new CompletableFuture<>();

        candidates.thenAccept(candidatesJson -> {
            try {
                List<String> intermediate = new ArrayList<>();
                for (int i = 0; i < candidatesJson.length(); i++) {
                    JSONObject element = (JSONObject) candidatesJson.get(i);
                    String id = element.getString("place_id");
                    intermediate.add(id);
                }
                result.complete(intermediate);
            } catch (JSONException e) {
                result.completeExceptionally(e);
            }
        });

        candidates.exceptionally(e -> {
            result.completeExceptionally(e);
            return null;
        });

        return result;
    }

    /**
     * Based on the sdp project. Make a query to google place to retrieve the place.
     * @param address
     * @return
     * @throws IOException
     */
    private CompletableFuture<String> getRawPlaceSearch(String address) {

        URL url = new GooglePlacesURLBuilder(apiKey)
                    .withAddress(address)
                    .withFieldPlaceId()
                    .build();

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
     * Convert the raw json string to a json array.
     * @param rawSearch
     * @return
     */
    private CompletableFuture<JSONArray> extractCandidates(CompletableFuture<String> rawSearch) {

        CompletableFuture<JSONArray> result = new CompletableFuture<>();

        rawSearch.thenAccept(raw -> {
            JSONObject json = null;

            try {
                json = (JSONObject) new JSONTokener(raw).nextValue();
                String status = (String) json.get("status");
                if (!status.equals("OK")) {
                    result.completeExceptionally(new IllegalStateException("failed to get "));
                }
                JSONArray candidatesJson = json.getJSONArray("candidates");

                if (candidatesJson == null) {
                    result.completeExceptionally(new IllegalStateException("failed to convert candidates to json object"));
                } else {
                    result.complete(candidatesJson);
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

    private static class GooglePlacesURLBuilder {

        private static final String OUTPUT_FIELD = "json";
        private static final String INPUT_FIELD = "input";
        private static final String FIELDS_FIELD = "fields";
        private static final String KEY_FIELD = "key";
        private static final String INPUT_TYPE = "inputtype=textquery";
        private static final List<String> PARAMETERS_FIELDS = Arrays.asList("place_id", "formatted_address");
        private static final String SEPARATOR = "&";
        private static final String START_QUERY = "?";
        private static final String SPACE_SEPARATOR = "%20";
        private static final String START_FIELD = "=";
        private static final String FIELD_SEPARATOR = ",";

        protected enum QueryField { PLACE_ID, FORMATTED_ADDRESS }

        private final ArrayList<String> targetedFields = new ArrayList<>();
        private final String apiKey;
        private String address;

        public GooglePlacesURLBuilder(String apiKey) {
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("api key cannot be null or empty !");
            }
            this.apiKey = apiKey;
        }

        public GooglePlacesURLBuilder withAddress(String address) {
            this.address = address;
            return this;
        }

        public GooglePlacesURLBuilder withField(QueryField field) {
            targetedFields.add(PARAMETERS_FIELDS.get(field.ordinal()));
            return this;
        }

        public GooglePlacesURLBuilder withFields(List<QueryField> fields) {
            for (QueryField field : fields) {
                withField(field);
            }
            return this;
        }

        public GooglePlacesURLBuilder withFieldPlaceId() {
            return withField(QueryField.PLACE_ID);
        }

        public GooglePlacesURLBuilder withFieldFormattedAddress() {
            return withField(QueryField.FORMATTED_ADDRESS);
        }

        public URL build() {

            if (targetedFields.size() == 0) {
                throw new IllegalStateException("you must specify target fields in the URL");
            }

            StringBuilder sb = new StringBuilder();
            //append the base query url
            sb.append(googlePlaceBaseUrl);
            //append the type of output we want, no separator required for the first
            sb.append(OUTPUT_FIELD).append(START_QUERY);

            //format input
            String[] separatedBySpaces = address.split(" ");
            StringBuilder f = new StringBuilder();

            for (int i = 0; i < separatedBySpaces.length; i++) {
                f.append(separatedBySpaces[i]);
                if (i < separatedBySpaces.length - 1) {
                    f.append(SPACE_SEPARATOR);
                }
            }

            String formatted = f.toString();

            //append the input
            sb.append(INPUT_FIELD).append(START_FIELD).append(formatted).append(SEPARATOR);

            //append input type
            sb.append(INPUT_TYPE).append(SEPARATOR);

            StringBuilder p = new StringBuilder();

            for (int i = 0; i < targetedFields.size(); i++) {
                p.append(targetedFields.get(i));
                if (i < targetedFields.size() - 1) {
                    p.append(FIELD_SEPARATOR);
                }
            }

            String fieldsFormatted = p.toString();

            //append targeted fields
            sb.append(FIELDS_FIELD).append(START_FIELD).append(fieldsFormatted).append(SEPARATOR);

            //append the key at the end
            sb.append(KEY_FIELD).append(START_FIELD).append(apiKey);

            URL url = null;
            try {
                url = new URL(sb.toString());
            } catch (MalformedURLException e) {
                throw new IllegalStateException("the built url is malformed !");
            }
            return url;
        }

    }

}
