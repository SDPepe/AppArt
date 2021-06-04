package ch.epfl.sdp.appart;

import android.Manifest;
import android.content.Intent;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.GeocoderModule;
import ch.epfl.sdp.appart.hilt.LocationModule;
import ch.epfl.sdp.appart.hilt.MapModule;
import ch.epfl.sdp.appart.location.LocationService;
import ch.epfl.sdp.appart.location.MockLocationService;
import ch.epfl.sdp.appart.location.geocoding.GeocodingService;
import ch.epfl.sdp.appart.location.geocoding.GoogleGeocodingService;
import ch.epfl.sdp.appart.map.GoogleMapService;
import ch.epfl.sdp.appart.map.MapService;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

@UninstallModules({DatabaseModule.class, MapModule.class, GeocoderModule.class, LocationModule.class})
@HiltAndroidTest
public class MapUIWithAddressTest {

    static final String intentExtra = "Funny Street 1A, 1000 Lausanne";
    static final Intent intent;

    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(),
                MapActivity.class);
        intent.putExtra(InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.intentLocationForMap), intentExtra);
    }

    @Rule(order = 1)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 2)
    public ActivityScenarioRule<MapActivity> mapActivityRule =
            new ActivityScenarioRule<>(intent);

    @Rule(order = 0)
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET);

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @BindValue
    final
    DatabaseService databaseService = new MockDatabaseService();

    @BindValue
    final MapService mapService = new GoogleMapService();

    @BindValue
    final GeocodingService geocodingService =
            new GoogleGeocodingService(InstrumentationRegistry.getInstrumentation().getTargetContext());

    @BindValue
    final LocationService locationService = new MockLocationService();


    private static final double SCREEN_HEIGHT_INFO_WINDOW_FACTOR = 0.35;


    @Test
    public void performGoodLocationTest() throws InterruptedException {
        UiDevice device =
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        boolean foundMap = device.wait(Until.hasObject(By.desc("MAP READY")),
                10000);
        assertThat(foundMap, is(true));

        Thread.sleep(4000);

        boolean isMarkerPresent =
                device.hasObject(By.descContains("AddressMarker")) | device.wait(Until.hasObject(By.descContains("AddressMarker")), 10000);
        assertThat(isMarkerPresent, is(true));


        UiObject2 marker = device.findObject(By.descContains("AddressMarker"));
        assertNotNull(marker);
    }
}
