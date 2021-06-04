package ch.epfl.sdp.appart;

import android.view.View;

import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
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

    @Test
    public void clickOnCameraBtn() {
        //onView(isRoot()).perform(waitId(R.id.gallery_Camera_button, 5000));
        //onView(withId(R.id.camera_Camera_button)).perform(closeSoftKeyboard()).perform(click());
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
    }

    
    @Test
    public void clickOnGalleryBtn() {
        //onView(isRoot()).perform(waitId(R.id.gallery_Camera_button, 5000));
        //onView(withId(R.id.gallery_Camera_button)).perform(closeSoftKeyboard()).perform(click());
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


    /**
     * From https://stackoverflow.com/questions/49796132/android-espresso-wait-for-text-to-appear
     * Perform action of waiting for a specific view id.
     * @param viewId The id of the view to wait for.
     * @param millis The timeout of until when to wait for.
     */
    public static ViewAction waitId(final int viewId, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                // timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }

}
