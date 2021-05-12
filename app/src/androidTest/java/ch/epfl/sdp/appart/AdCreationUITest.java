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
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
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
    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);


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
    public void cameraActivityWorksAndRespondsCorrectly() throws UiObjectNotFoundException {
        onView(withId(R.id.addPhoto_AdCreation_button)).perform(scrollTo(), click());
        /* =================================================================================================== */
        /*                            CALL THE CAMERA AND RECEIVE A MOCK IMAGE BACK                            */
        /* =================================================================================================== */

        // Create a bitmap we can use for our simulated camera image


        Bitmap icon = BitmapFactory.decodeResource(
                ApplicationProvider.getApplicationContext().getResources(),
                R.mipmap.ic_launcher);


        // Build a result to return from the Camera app
        Intent resultIntent = new Intent();
        resultIntent.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(ActivityCommunicationLayout.RESULT_IS_FOR_TEST, resultIntent);
        //com.android.camera2
        // When an intent is sent to the Camera, this tells Espresso to respond with the ActivityResult we just created
        intending(toPackage("com.android.camera2")).respondWith(result);

        // Now that we have the stub in place, click on the button in our app that launches into the Camera
        onView(withId(R.id.camera_Camera_button)).perform(click());

        // validate that an intent resolving to the "camera" activity has been sent out by app
        intended(toPackage("com.android.camera2"));

        // Initialize UiDevice instance
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Search for correct button in the dialog.
        UiObject buttonAllow = uiDevice.findObject(new UiSelector().text("ALLOW"));

        if (buttonAllow.exists() && buttonAllow.isEnabled()) {
            buttonAllow.click();
            uiDevice.pressBack();
        }

        // Search for correct button in the dialog.
        UiObject buttonAllow2 = uiDevice.findObject(new UiSelector().text("Allow all the time"));

        if (buttonAllow2.exists() && buttonAllow2.isEnabled()) {
            buttonAllow2.click();
            uiDevice.pressBack();
        }

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.confirm_Camera_button), withText("Confirm"),
                        childAtPosition(
                                allOf(withId(R.id.camera_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatButton4.perform(click());

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
                .check(matches(withText(R.string.snackbarNoPhotos_AdCreation)));
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
