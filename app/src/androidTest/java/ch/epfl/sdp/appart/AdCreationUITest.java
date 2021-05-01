package ch.epfl.sdp.appart;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.GrantPermissionRule;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@UninstallModules({DatabaseModule.class, LoginModule.class})
@HiltAndroidTest
public class AdCreationUITest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<AdCreationActivity> adCreationActivityRule = new ActivityScenarioRule<>(AdCreationActivity.class);

    /* Used to grant camera permission always */
    @Rule(order = 2)
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @BindValue
    DatabaseService database = new MockDatabaseService();
    @BindValue
    LoginService login = new MockLoginService();

    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
        login.loginWithEmail("lorenzo@epfl.ch", "2222");
    }

    @Test
    public void viewsAreDisplayedTest() {
        //edit text
        onView(withId(R.id.title_AdCreation_editText)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.number_AdCreation_ediText)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.street_AdCreation_editText)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.city_AdCreation_editText)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.npa_AdCreation_editText)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.price_AdCreation_editText)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.description_AdCreation_editText)).perform(scrollTo()).check(matches(isDisplayed()));

        //text view
        onView(withId(R.id.street_AdCreation_textView)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.number_AdCreation_textView)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.street_AdCreation_textView)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.city_AdCreation_textView)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.npa_AdCreation_textView)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.price_AdCreation_textView)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.description_AdCreation_textView)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.francs_AdCreation_textView)).perform(scrollTo()).check(matches(isDisplayed()));

        //buttons
        onView(withId(R.id.addPhoto_AdCreation_button)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.confirm_AdCreation_button)).perform(scrollTo()).check(matches(isDisplayed()));

        //sbinner
        onView(withId(R.id.period_AdCreation_spinner)).perform(scrollTo()).check(matches(isDisplayed()));
    }

    @Test
    public void photoButtonStartsCameraActivityTest() {
        onView(withId(R.id.addPhoto_AdCreation_button)).perform(scrollTo(), click());
        intended(hasComponent(CameraActivity.class.getName()));
    }

    @Test
    public void cameraActivityWorksAndRespondsCorrectly() throws InterruptedException{
        onView(withId(R.id.addPhoto_AdCreation_button)).perform(scrollTo(), click());
        /* =================================================================================================== */
        /*                         HOW TO CALL THE CAMERA AND RECEIVE A MOCK IMAGE BACK                        */
        /* =================================================================================================== */

        // Create a bitmap we can use for our simulated camera image
        Bitmap icon = BitmapFactory.decodeResource(
                ApplicationProvider.getApplicationContext().getResources(),
                R.mipmap.ic_launcher);


        // Build a result to return from the Camera app
        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub out the Camera. When an intent is sent to the Camera, this tells Espresso to respond
        // with the ActivityResult we just created
        intending(toPackage("com.android.camera2")).respondWith(result);

        // Now that we have the stub in place, click on the button in our app that launches into the Camera
        onView(withId(R.id.camera_Camera_button)).perform(click());

        // We can also validate that an intent resolving to the "camera" activity has been sent out by our app
        intended(toPackage("com.android.camera2"));

        onView(withId(R.id.confirm_Camera_button)).perform(click());

        Thread.sleep(1000);

        ViewInteraction horizontalScrollView = onView(
                allOf(withId(R.id.picturesScroll_AdCreation_ScrollView),
                        withParent(allOf(withId(R.id.vertical_AdCreation_linearLayout),
                                withParent(withId(R.id.horizontal_AdCreation_scrollView)))),
                        isDisplayed()));
        horizontalScrollView.check(matches(isDisplayed()));

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.pictures_AdCreation_linearLayout),
                        withParent(allOf(withId(R.id.picturesScroll_AdCreation_ScrollView),
                                withParent(withId(R.id.vertical_AdCreation_linearLayout)))),
                        isDisplayed()));
        linearLayout.check(matches(isDisplayed()));
    }

    @Test
    public void successfulPostAdButtonOpensScrollingActivityTest() {
        //populate ad info
        onView(withId(R.id.title_AdCreation_editText)).perform(scrollTo(), typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.street_AdCreation_editText)).perform(scrollTo(), typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.city_AdCreation_editText)).perform(scrollTo(), typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.description_AdCreation_editText)).perform(scrollTo(), typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.number_AdCreation_ediText)).perform(scrollTo(), typeText("0"));
        closeSoftKeyboard();
        onView(withId(R.id.npa_AdCreation_editText)).perform(scrollTo(), typeText("0"));
        closeSoftKeyboard();
        onView(withId(R.id.price_AdCreation_editText)).perform(scrollTo(), typeText("0"));
        closeSoftKeyboard();

        //create ad
        onView(withId(R.id.confirm_AdCreation_button)).perform(scrollTo(), click());
        // TODO go back to adactivity when user is synced with firestore
        //intended(hasComponent(AdActivity.class.getName()));
        intended(hasComponent(ScrollingActivity.class.getName()));
    }

    @Test
    public void failedPostAdButtonShowsSnackbarTest() {
        //populate ad info
        onView(withId(R.id.title_AdCreation_editText)).perform(scrollTo(), typeText("failing"));
        closeSoftKeyboard();
        onView(withId(R.id.street_AdCreation_editText)).perform(scrollTo(), typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.city_AdCreation_editText)).perform(scrollTo(), typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.description_AdCreation_editText)).perform(scrollTo(), typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.number_AdCreation_ediText)).perform(scrollTo(), typeText("0"));
        closeSoftKeyboard();
        onView(withId(R.id.npa_AdCreation_editText)).perform(scrollTo(), typeText("0"));
        closeSoftKeyboard();
        onView(withId(R.id.price_AdCreation_editText)).perform(scrollTo(), typeText("0"));
        closeSoftKeyboard();

        //create ad
        onView(withId(R.id.confirm_AdCreation_button)).perform(scrollTo(), click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.snackbarFailed_AdCreation)));
    }

    @Test
    public void emptyFieldsShowSnackbarTest() {
        //populate ad info
        onView(withId(R.id.title_AdCreation_editText)).perform(scrollTo(), typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.street_AdCreation_editText)).perform(scrollTo(), typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.city_AdCreation_editText)).perform(scrollTo(), typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.number_AdCreation_ediText)).perform(scrollTo(), typeText("0"));
        closeSoftKeyboard();
        onView(withId(R.id.npa_AdCreation_editText)).perform(scrollTo(), typeText("0"));
        closeSoftKeyboard();
        onView(withId(R.id.price_AdCreation_editText)).perform(scrollTo(), typeText("0"));
        closeSoftKeyboard();

        // description field not filled
        onView(withId(R.id.confirm_AdCreation_button)).perform(scrollTo(), click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.snackbarNotFilled_AdCreation)));
    }

    @After
    public void release() {
        Intents.release();
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

}
