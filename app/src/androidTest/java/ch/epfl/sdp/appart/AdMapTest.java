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
import java.util.concurrent.CompletableFuture;
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
import static org.hamcrest.Matchers.lessThanOrEqualTo;

@UninstallModules({DatabaseModule.class, MapModule.class})
@HiltAndroidTest
public class AdMapTest {

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ApplicationProvider.getApplicationContext().getResources().getString(R.string.intentLocationForMap), "Lausanne");
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
    public void markerTest() throws JSONException, UnsupportedEncodingException, InterruptedException {

        UiDevice device =
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        boolean foundMap = device.wait(Until.hasObject(By.desc("MAP READY")),
                10000);
        assertThat(foundMap, is(true));

        Location markerLocation = getLocationFromString("Lausanne");
        assertThat(markerLocation.longitude, greaterThan(6.0));
        assertThat(markerLocation.latitude, greaterThan(46.0));

        CompletableFuture<Location> futureCameraLoc = mapService.getCameraPosition();

        Location cameraLoc = futureCameraLoc.join();
        assertThat(cameraLoc.longitude, greaterThan(6.0));
        assertThat(cameraLoc.latitude, greaterThan(46.0));

        assertThat(Math.abs(cameraLoc.latitude - markerLocation.latitude), lessThanOrEqualTo(0.05));
        assertThat(Math.abs(cameraLoc.longitude - markerLocation.longitude), lessThanOrEqualTo(0.05));


    }
}
