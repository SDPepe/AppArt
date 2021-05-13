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
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;

import ch.epfl.sdp.appart.R;
import dagger.hilt.android.scopes.ActivityScoped;

@ActivityScoped
public class GooglePlaceService {

    private final static String googlePlaceBaseUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/";
    private String apiKey;
    private String result;

    @Inject
    public GooglePlaceService() {}

    public void initialize(Activity activity) {
        apiKey = activity.getResources().getString(R.string.maps_api_key);
        Places.initialize(activity.getApplicationContext(), apiKey);
        Place place = Place.builder().setAddress("Rue de Neuchâtel 3").build();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        try {
             result = getRawPlaceSearch("Rue de Neuchâtel 3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int i = 0;
    }

    /**
     * Base on the sdp project
     * @param address
     * @return
     * @throws IOException
     */
    public String getRawPlaceSearch(String address) throws IOException {

        URL url = new GooglePlacesURLBuilder(apiKey)
                    .withAddress(address)
                    .withFieldPlaceId()
                    .withFieldFormattedAddress()
                    .build();

        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
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
                result = reader.lines().collect(Collectors.joining("\n"));
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
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
