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
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@UninstallModules({DatabaseModule.class, MapModule.class})
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

    public Location getLocationFromString(String address)
            throws JSONException, UnsupportedEncodingException {
        RequestQueue queue =
                Volley.newRequestQueue(InstrumentationRegistry.getInstrumentation().getTargetContext());

        String api_key =
                InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().getString(R.string.maps_api_key);

        String url = "https://maps.googleapis" +
                ".com/maps/api/geocode/json?address=" + URLEncoder.encode(address, "UTF-8") + "&key=" + api_key;
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(url,
                new JSONObject(), future, future);
        queue.add(request);
        JSONObject jsonObject;
        try {
            jsonObject = future.get(30, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }

        double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lat");

        double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lng");
        Location loc = new Location();
        loc.latitude = lat;
        loc.longitude = lng;
        return loc;
    }


    @Test
    public void markerTest() throws JSONException {
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
                boolean succeeded = false;
                int tryCount = 0;
                Location loc = null;
                while (tryCount < 10 && !succeeded) {
                    try {
                        loc = getLocationFromString("Lausanne");
                        mapService.centerOnLocation(loc, true);
                        succeeded = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        tryCount++;
                    }
                }

                List<UiObject2> lists =
                        device.findObjects(By.descContains(card.getCity()));
                assertThat(lists.size(), greaterThan(0));
                markers.addAll(lists);
                markerDescs.add(card.getCity());

                mapService.centerOnLocation(loc, true);

                boolean isMarkerPresent = device.wait(Until.hasObject(By.desc(card.getCity())), 10000);
                assertThat(isMarkerPresent, is(true));


                UiObject2 marker = device.findObject(By.descContains(card.getCity()));
                marker.click();

                boolean isMarkerClicked =
                        device.wait(Until.hasObject(By.descContains(card.getCity() + " CLICKED")), 10000);
                assertThat(isMarkerClicked, is(true));
            }
        }

        assertThat(markers.size(), is(cards.size()));

        //markers.get(0).click();
    }
}
