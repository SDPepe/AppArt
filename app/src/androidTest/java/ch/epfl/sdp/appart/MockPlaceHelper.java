package ch.epfl.sdp.appart;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.geocoding.MockGeocodingService;
import ch.epfl.sdp.appart.place.GooglePlaceService;
import ch.epfl.sdp.appart.place.helper.MockGooglePlaceServiceHelper;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

/*
@HiltAndroidTest
public class MockPlaceHelper {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    MockGooglePlaceServiceHelper helper;

    @Before
    public void init() {
        hiltRule.inject();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        helper = new MockGooglePlaceServiceHelper(context);
    }

    @Test
    public void mockPlaceHelperReturnsExpectedTrash() {
        MockGooglePlaceServiceHelper helper = new MockGooglePlaceServiceHelper();
        helper.query(new Location(), 2, "restaurent");
    }
}*/
