package ch.epfl.sdp.appart;

import android.content.Context;
import android.util.Pair;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import ch.epfl.sdp.appart.location.geocoding.MockGeocodingService;
import ch.epfl.sdp.appart.location.place.address.Address;
import ch.epfl.sdp.appart.location.place.address.AddressFactory;
import ch.epfl.sdp.appart.place.PlaceOfInterest;
import ch.epfl.sdp.appart.place.PlaceService;
import ch.epfl.sdp.appart.place.PlaceServiceException;
import ch.epfl.sdp.appart.place.helper.MockPlaceServiceHelper;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@HiltAndroidTest
public class PlaceServiceTest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    private PlaceService placeService;
    private MockPlaceServiceHelper helper;
    private final Address a = AddressFactory.makeAddress("rue du chat 19, " +
            "1328 Renens");

    @Before
    public void init() {
        hiltRule.inject();
        Context context =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        helper = new MockPlaceServiceHelper(context);
        placeService = new PlaceService(helper, new MockGeocodingService());
    }

    @Test
    public void retrieveMockedPlaces() {
        helper.setMockMode(MockPlaceServiceHelper.MockMode.VALID);


        List<Pair<PlaceOfInterest, Float>> places =
                placeService.getNearbyPlacesWithDistances(a, 100, "restaurant"
                        , 20).join();
        Address a = AddressFactory.makeAddress("Avenue Haldimand 5, " +
                "Yverdon-les-Bains");
        List<Pair<PlaceOfInterest, Float>> placesFiltered =
                places.stream().filter(p -> p.first.getAddress() != null && p.first.getAddress().equals(a)).collect(Collectors.toList());
        assertTrue(placesFiltered.size() == 1);
        PlaceOfInterest p = placesFiltered.get(0).first;
        assertTrue(p.hasLocation());
        assertTrue(p.hasAddress());


    }


    @Test
    public void retrieveMockedPlacesFails() {
        helper.setMockMode(MockPlaceServiceHelper.MockMode.INVALID);
        CompletableFuture<List<Pair<PlaceOfInterest, Float>>> future = null;
        try {
            future = placeService.getNearbyPlacesWithDistances(a, 100,
                    "restaurant", 20);
            future.get(5000, TimeUnit.MILLISECONDS);
        } catch (PlaceServiceException | ExecutionException | InterruptedException | TimeoutException e) {
            //a bit shady....
            final Throwable cause = e.getCause();
            if (!(cause instanceof PlaceServiceException)) {
                e.printStackTrace();
                fail();
            }

        } finally {
            future.cancel(false);
        }

    }

    @Test
    public void retrieveMockedPlacesEmpty() {
        helper.setMockMode(MockPlaceServiceHelper.MockMode.EMPTY);
        boolean hasFailed = false;
        CompletableFuture<List<Pair<PlaceOfInterest, Float>>> future = null;
        List<Pair<PlaceOfInterest, Float>> result = null;
        try {
            future = placeService.getNearbyPlacesWithDistances(a, 100,
                    "restaurant", 20);
            result = future.get(5000, TimeUnit.MILLISECONDS);
        } catch (CompletionException | ExecutionException | InterruptedException | TimeoutException e) {
            fail();
            e.printStackTrace();
        } finally {
            assertFalse(hasFailed);
            if (result == null) {
                fail();
            }
            assertTrue(result.size() == 0);
            future.cancel(false);
        }
    }

}
