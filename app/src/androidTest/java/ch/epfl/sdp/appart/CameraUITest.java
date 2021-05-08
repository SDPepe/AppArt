package ch.epfl.sdp.appart;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@HiltAndroidTest
public class CameraUITest {


    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<CameraActivity> intentsRule = new ActivityScenarioRule<>(CameraActivity.class);

    @Before
    public void init() {
        hiltRule.inject();
        Intents.init();
    }

    /*@Test
    public void clickOnCameraBtn() {
        onView(withId(R.id.camera_Camera_button)).perform(click());
        /*onView(withId(R.id.camera_Camera_button)).check(matches(allOf( isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return ViewMatchers.isEnabled(); // no constraints, they are checked above
                    }

                    @Override
                    public String getDescription() {
                        return "click camera button";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        view.performClick();
                    }
                }
        );*/
    //}

    @Test
    public void clickOnGalleryBtn() {
        onView(withId(R.id.gallery_Camera_button)).perform(click());
        /*onView(withId(R.id.camera_Camera_button)).check(matches(allOf( isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return ViewMatchers.isEnabled(); // no constraints, they are checked above
                    }

                    @Override
                    public String getDescription() {
                        return "click gallery button";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        view.performClick();
                    }
                }
        );*/
    }

    @After
    public void release() {
        Intents.release();
    }


}
