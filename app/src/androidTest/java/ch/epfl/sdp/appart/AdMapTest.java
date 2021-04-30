package ch.epfl.sdp.appart;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LocationModule;
import ch.epfl.sdp.appart.hilt.MapModule;
import ch.epfl.sdp.appart.location.AndroidLocationService;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.LocationService;
import ch.epfl.sdp.appart.map.GoogleMapService;
import ch.epfl.sdp.appart.map.MapService;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

@UninstallModules({DatabaseModule.class, MapModule.class, LocationModule.class})
@HiltAndroidTest
public class AdMapTest {

    static Intent intent;

    static String testCity = "Lausanne";

    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(),
                MapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ApplicationProvider.getApplicationContext().getResources().getString(R.string.intentLocationForMap), testCity);
        intent.putExtras(bundle);
    }

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MapActivity> mapActivityRule =
            new ActivityScenarioRule<>(intent);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET);

    @BindValue
    final
    DatabaseService databaseService = new MockDatabaseService();

    @BindValue
    final MapService mapService = new GoogleMapService();

    @BindValue
    final LocationService locationService =
            new AndroidLocationService(InstrumentationRegistry.getInstrumentation().getTargetContext());


    @Test
    public void markerTest() {

        UiDevice device =
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        boolean foundMap = device.wait(Until.hasObject(By.desc("MAP READY")),
                10000);
        assertThat(foundMap, is(true));

        Location markerLocation = locationService.getLocationFromName(
                testCity).join();
        assertThat(markerLocation.longitude, greaterThan(6.0));
        assertThat(markerLocation.latitude, greaterThan(46.0));

        CompletableFuture<Location> futureCameraLoc =
                mapService.getCameraPosition();

        Location cameraLoc = futureCameraLoc.join();
        assertThat(cameraLoc.longitude, greaterThan(6.0));
        assertThat(cameraLoc.latitude, greaterThan(46.0));

        assertThat(Math.abs(cameraLoc.latitude - markerLocation.latitude),
                lessThanOrEqualTo(0.05));
        assertThat(Math.abs(cameraLoc.longitude - markerLocation.longitude),
                lessThanOrEqualTo(0.05));


    }
}
