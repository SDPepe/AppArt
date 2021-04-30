package ch.epfl.sdp.appart;

import android.Manifest;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import ch.epfl.sdp.appart.scrolling.card.Card;
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
public class MapUITest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MapActivity> mapActivityRule =
            new ActivityScenarioRule<>(MapActivity.class);

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

        Set<String> markerDescs = new HashSet<>();
        ArrayList<UiObject2> markers = new ArrayList<>();

        List<Card> cards = databaseService.getCards().join();
        for (Card card : cards) {
            if (!markerDescs.contains(card.getCity())) {
                Location loc =
                        locationService.getLocationFromName("Lausanne").join();
                assertThat(Math.abs(loc.latitude - 46.5196535), lessThanOrEqualTo(0.05));
                assertThat(Math.abs(loc.longitude - 6.6322734), lessThanOrEqualTo(0.05));
                mapService.centerOnLocation(loc, true);
                List<UiObject2> lists =
                        device.findObjects(By.descContains(card.getCity()));
                assertThat(lists.size(), greaterThan(0));
                markers.addAll(lists);
                markerDescs.add(card.getCity());
            }
        }

        assertThat(markers.size(), is(cards.size()));
    }
}
