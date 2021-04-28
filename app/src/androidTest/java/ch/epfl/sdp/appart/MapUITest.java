package ch.epfl.sdp.appart;

import android.Manifest;
import android.location.Address;
import android.location.Geocoder;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.Until;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.MapModule;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.map.GoogleMapService;
import ch.epfl.sdp.appart.map.MapService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.is;

@UninstallModules(DatabaseModule.class)
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

    @Test
    public void markerTest() {

        UiDevice device =
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        boolean foundMap = device.wait(Until.hasObject(By.desc("MAP READY")), 10000);
        assertThat(foundMap, is(true));

        Geocoder geocoder = new Geocoder(InstrumentationRegistry.getInstrumentation().getTargetContext());

        Set<String> markerDescs = new HashSet<>();
        ArrayList<UiObject2> markers = new ArrayList<>();

        List<Card> cards = databaseService.getCards().join();
        for (Card card : cards) {
            if (!markerDescs.contains(card.getCity())) {
                try {
                    List<Address> addresses = geocoder.getFromLocationName(card.getCity(), 1);
                    Address addr = addresses.get(0);
                    Location loc = new Location();
                    loc.longitude = addr.getLongitude();
                    loc.latitude = addr.getLatitude();
                    GoogleMapService.centerOnLocation(loc, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                List<UiObject2> lists =
                        device.findObjects(By.descContains(card.getCity()));
                markers.addAll(lists);
                markerDescs.add(card.getCity());
            }
        }

        assertThat(markers.size(), is(cards.size()));
    }
}
