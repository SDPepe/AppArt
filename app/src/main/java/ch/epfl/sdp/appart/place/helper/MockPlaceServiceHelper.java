package ch.epfl.sdp.appart.place.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.location.Location;

/**
 * Mock of the google place helper. Allows to return the same dummy json
 * string while testing.
 */
public class MockPlaceServiceHelper implements PlaceHelper {

    private final String dummyValidOutput;
    private final String dummyInvalidOutput;
    private final String dummyEmptyOutput;
    private String resultPointer = null;

    public enum MockMode {VALID, EMPTY, INVALID}

    public MockPlaceServiceHelper(Context context) {
        this.dummyValidOutput = loadRawJson(context,
                R.raw.mocked_google_place_output);
        this.dummyInvalidOutput = loadRawJson(context,
                R.raw.mocked_google_place_output_failure);
        this.dummyEmptyOutput = loadRawJson(context,
                R.raw.mocked_google_place_output_no_results);
        resultPointer = this.dummyValidOutput;
    }

    //from https://stackoverflow
    // .com/questions/6349759/using-json-file-in-android-app-resources
    private String loadRawJson(Context context, int id) {
        InputStream is = context.getResources().openRawResource(id);
        Writer writer = new StringWriter();
        char[] buffer = new char[50000];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF" +
                    "-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return writer.toString();
    }

    /**
     * Allows to select which type of json output will be sent on any
     * subsequent request.
     *
     * @param mode
     */
    public void setMockMode(MockMode mode) {
        if (mode == MockMode.VALID) {
            resultPointer = dummyValidOutput;
        } else if (mode == MockMode.EMPTY) {
            resultPointer = dummyEmptyOutput;
        } else if (mode == MockMode.INVALID) {
            resultPointer = dummyInvalidOutput;
        }
    }

    @Override
    public CompletableFuture<String> query(Location location, int radius,
                                           String type) {
        return CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return resultPointer;
            }
        });
    }

    @Override
    public CompletableFuture<String> query(Location location, String type) {
        return CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return resultPointer;
            }
        });
    }

    @Override
    public CompletableFuture<Bitmap> queryImage(String photoReference,
                                                int maxHeight, int maxWidth) {
        Bitmap fakeBitmap = Bitmap.createBitmap(100, 100,
                Bitmap.Config.ARGB_8888);
        fakeBitmap.eraseColor(Color.rgb(255, 0, 0));
        return CompletableFuture.completedFuture(fakeBitmap);
    }

    @Override
    public CompletableFuture<String> queryPlaceDetails(String placeId) {
        return CompletableFuture.completedFuture("");
    }
}
