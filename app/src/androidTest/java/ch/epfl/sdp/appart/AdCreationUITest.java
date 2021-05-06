package ch.epfl.sdp.appart;

import android.app.Instrumentation;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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

import static android.app.Activity.RESULT_CANCELED;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UninstallModules({DatabaseModule.class, LoginModule.class})
@HiltAndroidTest
public class AdCreationUITest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<AdCreationActivity> adCreationActivityRule = new ActivityScenarioRule<>(AdCreationActivity.class);

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


        //buttons
        onView(withId(R.id.addPhoto_AdCreation_button)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.confirm_AdCreation_button)).check(matches(isDisplayed()));

        //spinner
        onView(withId(R.id.period_AdCreation_spinner)).perform(scrollTo()).check(matches(isDisplayed()));
    }

    @Test
    public void photoButtonStartsCameraActivityTest() {
        onView(withId(R.id.addPhoto_AdCreation_button)).perform(scrollTo(), click());
        intended(hasComponent(CameraActivity.class.getName()));
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

}
