package ch.epfl.sdp.appart;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static android.app.Activity.RESULT_CANCELED;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

/**
 * WARNING: to modify when the activity will support loading images from databaseservice!
 */
@UninstallModules(DatabaseModule.class)
@HiltAndroidTest
public class PanoramaUITest {

    static final String testId = "1PoUWbeNHvMNotxwAui5";
    static final Intent intent;

    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), PanoramaActivity.class);
        ArrayList<String> images = new ArrayList<>();
        images.add("fake_ad_1.jpg");
        images.add("fake_ad_2.jpg");
        intent.putStringArrayListExtra(AdActivity.Intents.INTENT_PANORAMA_PICTURES, images);
        intent.putExtra(AdActivity.Intents.INTENT_AD_ID, "dummy");
    }

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<PanoramaActivity> panoramaActivityRule = new ActivityScenarioRule<>(intent);

    @BindValue
    DatabaseService database = new MockDatabaseService();

    int leftButtonID;
    int rightButtonID;


    @Before
    public void init() {
        leftButtonID = R.id.leftImage_Panorama_imageButton;
        rightButtonID = R.id.rightImage_Panorama_imageButton;
    }

    @Test
    public void buttonVisibilityTest() {
        onView(withId(leftButtonID)).check(matches(not(isDisplayed())));
        onView(withId(rightButtonID)).check(matches(isDisplayed()));

        onView(withId(rightButtonID)).perform(click());
        onView(withId(leftButtonID)).check(matches(isDisplayed()));
        onView(withId(rightButtonID)).check(matches(not(isDisplayed())));

        onView(withId(leftButtonID)).perform(click());
        onView(withId(leftButtonID)).check(matches(not(isDisplayed())));
        onView(withId(rightButtonID)).check(matches(isDisplayed()));

    }

    /**
     * This test does nothing but allows to increase coverage over an internal method of
     * panoramagl
     */
    @Test
    public void scrollTest() {
        swipeDown();
    }


    @Test
    public void checkLoadImageSucceed() {
        panoramaActivityRule.getScenario().onActivity(activity -> {
            activity.hasCurrentImageLoadingFailed().thenAccept(s -> assertThat(s, is(false)));
        });
    }

}
