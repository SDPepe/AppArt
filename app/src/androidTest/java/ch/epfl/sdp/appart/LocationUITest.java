package ch.epfl.sdp.appart;

import android.Manifest;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@HiltAndroidTest
public class LocationUITest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<LocationActivity> locationActivityRule =
            new ActivityScenarioRule<>(LocationActivity.class);

    @Rule(order = 2)
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void permissionIsGranted() {
        onView(withId(R.id.permission_location_textview)).check(matches(withText(R.string.permissionGranted)));
    }

    @Test
    public void locationIsSet() {
        onView(withId(R.id.latitude_location_textview)).check(matches(not(withText(R.string.locationUnknown))));
        onView(withId(R.id.longitude_location_textview)).check(matches(not(withText(R.string.locationUnknown))));

        onView(withId(R.id.latitude_location_textview)).check(matches(not(withText(R.string.locationDefault))));
        onView(withId(R.id.longitude_location_textview)).check(matches(not(withText(R.string.locationDefault))));
    }

    @Test
    public void callbackWorks() {
        onView(withId(R.id.callback_location_textview)).check(matches(withText(R.string.setCallbackTextView)));
    }
}
