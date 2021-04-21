package ch.epfl.sdp.appart;

import android.widget.ImageButton;

import androidx.annotation.ContentView;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

/**
 * WARNING: to modify when the activity will support loading images from databaseservice!
 */
@HiltAndroidTest
public class PanoramaUITest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<PanoramaActivity> panoramaActivityRule = new ActivityScenarioRule<>(PanoramaActivity.class);

    int leftButtonID;
    int rightButtonID;

    @Before
    public void init() {
        leftButtonID = R.id.leftImage_Panorama_imageButton;
        rightButtonID = R.id.rightImage_Panorama_imageButton;
    }

    @Test
    public void buttonVisibilityOnFirstImageTest() {
        onView(withId(leftButtonID)).check(matches(not(isDisplayed())));
        onView(withId(rightButtonID)).check(matches(isDisplayed()));
    }

    @Test
    public void buttonVisibilityOnSecondImageTest() {
        onView(withId(rightButtonID)).perform(click());

        onView(withId(leftButtonID)).check(matches(isDisplayed()));
        onView(withId(rightButtonID)).check(matches(isDisplayed()));
    }

    @Test
    public void buttonVisibilityOnLastImageTest() {
        onView(withId(rightButtonID)).perform(click());
        onView(withId(rightButtonID)).perform(click());
        onView(withId(rightButtonID)).perform(click());

        onView(withId(leftButtonID)).check(matches(isDisplayed()));
        onView(withId(rightButtonID)).check(matches(not(isDisplayed())));
    }

}
