package ch.epfl.sdp.appart;

import android.Manifest;
import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
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
public class LocationTest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<LocationActivity> locationActivity =
            new ActivityScenarioRule<>(LocationActivity.class);

    private final Context context =
            InstrumentationRegistry.getInstrumentation().getContext();

    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET);

    @Test
    public void locationTest() {
        onView(withId(R.id.longitude_Location_textView)).check(matches(not(withText(R.string.locationUnknown))));
        onView(withId(R.id.latitude_Location_textView)).check(matches(not(withText(R.string.locationUnknown))));

        onView(withId(R.id.longitude_Location_textView)).check(matches(not(withText(R.string.locationDefault))));
        onView(withId(R.id.latitude_Location_textView)).check(matches(not(withText(R.string.locationDefault))));

    }
}
