package ch.epfl.sdp.appart.place;

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
import java.util.stream.Collectors;

import ch.epfl.sdp.appart.AdCreationActivity;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.address.Address;
import ch.epfl.sdp.appart.location.address.AddressFactory;
import ch.epfl.sdp.appart.location.geocoding.MockGeocodingService;
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

//@UninstallModules({DatabaseModule.class, LoginModule.class})
@HiltAndroidTest
public class PlaceServiceTest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<DummyPlaceActivity> dummyPlaceActivityRule = new ActivityScenarioRule<>(DummyPlaceActivity.class);


    GooglePlaceService placeService;
    MockGooglePlaceServiceHelper helper;


    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        helper = new MockGooglePlaceServiceHelper(context);
        placeService = new GooglePlaceService(helper, new MockGeocodingService());
    }

    @Test
    public void retrieveMockedPlaces() {
        helper.setMockMode(MockGooglePlaceServiceHelper.MockMode.VALID);
        List<Pair<PlaceOfInterest, Float>> places = placeService.getNearbyPlacesWithDistances(new Location(123, 123), 100, "restaurant", 20).join();
        Address a = AddressFactory.makeAddress("Avenue Haldimand 5, Yverdon-les-Bains");
        List<Pair<PlaceOfInterest, Float>> placesFiltered = places.stream().filter(p -> p.first.getAddress() != null && p.first.getAddress().equals(a)).collect(Collectors.toList());
        assertTrue(placesFiltered.size() == 1);
        PlaceOfInterest p = placesFiltered.get(0).first;
        assertTrue(p.hasLocation());
        assertTrue(p.hasAddress());
    }


    @Test
    public void retrieveMockedPlacesFails() {
        helper.setMockMode(MockGooglePlaceServiceHelper.MockMode.INVALID);
        boolean hasFailed = false;
        try {
            placeService.getNearbyPlacesWithDistances(new Location(123, 123), 100, "restaurant", 20).join();
        } catch (CompletionException e) {
            hasFailed = true;
        } finally {
            assertTrue(hasFailed);
        }
    }

    @Test
    public void retrieveMockedPlacesEmpty() {
        helper.setMockMode(MockGooglePlaceServiceHelper.MockMode.EMPTY);
        boolean hasFailed = false;
        List<Pair<PlaceOfInterest, Float>> result = null;
        try {
            result = placeService.getNearbyPlacesWithDistances(new Location(123, 123), 100, "restaurant", 20).join();
        } catch (CompletionException e) {
            hasFailed = true;
        } finally {
            assertFalse(hasFailed);
            if (result == null) {
                assertFalse(true);
            }
            assertTrue(result.size() == 0);
        }
    }

}
