package ch.epfl.sdp.appart;

import android.Manifest;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.GeocoderModule;
import ch.epfl.sdp.appart.hilt.MapModule;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.geocoding.GeocodingService;
import ch.epfl.sdp.appart.location.geocoding.GoogleGeocodingService;
import ch.epfl.sdp.appart.location.place.locality.LocalityFactory;
import ch.epfl.sdp.appart.map.GoogleMapService;
import ch.epfl.sdp.appart.map.MapService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@UninstallModules({DatabaseModule.class, MapModule.class, GeocoderModule.class})
@HiltAndroidTest
public class MapUITest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MapActivity> mapActivityRule =
            new ActivityScenarioRule<>(MapActivity.class);

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


    private static final double SCREEN_HEIGHT_INFO_WINDOW_FACTOR = 0.35;

    @Test
    public void markerTest() throws InterruptedException {


        UiDevice device =
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        boolean foundMap = device.wait(Until.hasObject(By.desc("MAP READY")),
                10000);
        assertThat(foundMap, is(true));

        Thread.sleep(4000);

        Set<String> markerDescs = new HashSet<>();
        ArrayList<UiObject2> markers = new ArrayList<>();

        List<Card> cards = databaseService.getCards().join();
        for (Card card : cards) {
            if (!markerDescs.contains(card.getCity())) {
                Location loc =
                        geocodingService.getLocation(LocalityFactory.makeLocality(card.getCity())).join();
                assertNotNull(loc);
                assertThat(loc.latitude, greaterThan(0.0));
                assertThat(loc.longitude, greaterThan(0.0));
                mapService.centerOnLocation(loc, true);
                Thread.sleep(1000);


                List<UiObject2> lists =
                        device.findObjects(By.descContains(card.getCity()));
                assertThat(lists.size(), greaterThan(0));
                markers.addAll(lists);
                markerDescs.add(card.getCity());
            }
        }

        assertThat(markers.size(), is(cards.size()));

        Card card = cards.get(0);
        Location loc =
                geocodingService.getLocation(LocalityFactory.makeLocality(card.getCity())).join();
        mapService.centerOnLocation(loc, true);
        Thread.sleep(1000);


        boolean isMarkerPresent =
                device.hasObject(By.descContains(card.getCity())) | device.wait(Until.hasObject(By.descContains(card.getCity())), 10000);
        assertThat(isMarkerPresent, is(true));


        UiObject2 marker = device.findObject(By.descContains(card.getCity()));
        marker.click();

        boolean isMarkerClicked =
                device.wait(Until.hasObject(By.descContains(card.getCity() +
                        " CLICKED")), 10000) | device.hasObject(By.descContains(card.getCity() + "CLICKED"));
        assertThat(isMarkerClicked, is(true));
    }

    @Before
    public void init() {
        Intents.init();
    }

    @Test
    public void markerClickTest() throws InterruptedException {


        UiDevice device =
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        boolean foundMap = device.wait(Until.hasObject(By.desc("MAP READY")),
                10000);
        assertThat(foundMap, is(true));

        Thread.sleep(4000);

        List<Card> cards = databaseService.getCards().join();

        Card card = cards.get(0);

        Location loc =
                geocodingService.getLocation(LocalityFactory.makeLocality(card.getCity())).join();
        mapService.centerOnLocation(loc, true);
        Thread.sleep(1000);


        boolean isMarkerPresent =
                device.hasObject(By.descContains(card.getCity())) | device.wait(Until.hasObject(By.descContains(card.getCity())), 10000);
        assertThat(isMarkerPresent, is(true));


        UiObject2 marker = device.findObject(By.descContains(card.getCity()));
        marker.click();

        Thread.sleep(2000);

        //https://stackoverflow.com/questions/42505274/android-testing-google
        // -map-info-window-click
        WindowManager windowManager =
                (WindowManager) InstrumentationRegistry.getInstrumentation().getTargetContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        int x = screenWidth / 2;
        int y = (int) (screenHeight * SCREEN_HEIGHT_INFO_WINDOW_FACTOR);

        device.click(x, y);

        Thread.sleep(1000);

        intended(allOf(hasComponent(AdActivity.class.getName()), hasExtra(
                "adID", card.getAdId())));

    }

    @After
    public void release() {
        Intents.release();
    }

}
