package ch.epfl.sdp.appart;

import android.content.Context;
import android.util.Pair;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
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

import ch.epfl.sdp.appart.AdCreationActivity;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.address.Address;
import ch.epfl.sdp.appart.location.address.AddressFactory;
import ch.epfl.sdp.appart.location.geocoding.MockGeocodingService;
import ch.epfl.sdp.appart.place.GooglePlaceService;
import ch.epfl.sdp.appart.place.PlaceOfInterest;
import ch.epfl.sdp.appart.place.PlaceServiceException;
import ch.epfl.sdp.appart.place.helper.MockGooglePlaceServiceHelper;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@HiltAndroidTest
public class PlaceServiceTest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    private GooglePlaceService placeService;
    private MockGooglePlaceServiceHelper helper;
    private final Address a = AddressFactory.makeAddress("rue du chat 19, 1328 Renens");

    @Before
    public void init() {
        hiltRule.inject();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        helper = new MockGooglePlaceServiceHelper(context);
        placeService = new GooglePlaceService(helper, new MockGeocodingService());
    }

    @Test
    public void retrieveMockedPlaces() {
        helper.setMockMode(MockGooglePlaceServiceHelper.MockMode.VALID);
        try {

            List<Pair<PlaceOfInterest, Float>> places =
                    placeService.getNearbyPlacesWithDistances(a, 100, "restaurant", 20).get(5000, TimeUnit.MILLISECONDS);
            Address a = AddressFactory.makeAddress("Avenue Haldimand 5, Yverdon-les-Bains");
            List<Pair<PlaceOfInterest, Float>> placesFiltered = places.stream().filter(p -> p.first.getAddress() != null && p.first.getAddress().equals(a)).collect(Collectors.toList());
            assertTrue(placesFiltered.size() == 1);
            PlaceOfInterest p = placesFiltered.get(0).first;
            assertTrue(p.hasLocation());
            assertTrue(p.hasAddress());
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            assertFalse(true);
        }

    }


    @Test
    public void retrieveMockedPlacesFails() {
        helper.setMockMode(MockGooglePlaceServiceHelper.MockMode.INVALID);
        CompletableFuture<List<Pair<PlaceOfInterest, Float>>> future = null;
        try {
            future = placeService.getNearbyPlacesWithDistances(a, 100, "restaurant", 20);
            future.get(5000, TimeUnit.MILLISECONDS);
        } catch (PlaceServiceException | ExecutionException | InterruptedException | TimeoutException e) {
            //a bit shady....
            final Throwable cause = e.getCause();
            if (!(cause.getCause() instanceof PlaceServiceException)) {
                e.printStackTrace();
                fail();
            }

        } finally {
            future.cancel(false);
        }

    }

    @Test
    public void retrieveMockedPlacesEmpty() {
        helper.setMockMode(MockGooglePlaceServiceHelper.MockMode.EMPTY);
        boolean hasFailed = false;
        CompletableFuture<List<Pair<PlaceOfInterest, Float>>> future = null;
        List<Pair<PlaceOfInterest, Float>> result = null;
        try {
            future = placeService.getNearbyPlacesWithDistances(a, 100, "restaurant", 20);
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
