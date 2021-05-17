package ch.epfl.sdp.appart.place.helper;

import android.content.Context;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.address.Address;

/**
 * Mock of the google place helper. Allows to return the same dummy json string while testing.
 */
public class MockGooglePlaceServiceHelper implements GooglePlaceHelper {

    private final String dummyOutput;

    public MockGooglePlaceServiceHelper(Context context) {
        this.dummyOutput = context.getResources().getString(R.string.mock_google_place_output);
    }

    @Override
    public CompletableFuture<String> query(Location location, int radius, String type) {
        return CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return dummyOutput;
            }
        });
    }
}
