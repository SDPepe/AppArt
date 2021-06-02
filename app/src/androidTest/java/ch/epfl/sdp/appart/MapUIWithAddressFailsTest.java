package ch.epfl.sdp.appart;

import android.Manifest;
import android.content.Intent;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@UninstallModules({DatabaseModule.class, MapModule.class,
        GeocoderModule.class, LocationModule.class})
@HiltAndroidTest
public class MapUIWithAddressFailsTest {

    static final String intentExtra = "";
    static final Intent intent;

    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(),
                MapActivity.class);
        intent.putExtra(InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.intentLocationForMap), intentExtra);
    }

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MapActivity> mapActivityRule =
            new ActivityScenarioRule<>(intent);

    @Rule(order = 2)
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


    @Test
    public void performIncorrectLocationTest() {
        onView(withText(InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getString(R.string.alertDialogMessage))).check(matches(isDisplayed()));
    }
}
