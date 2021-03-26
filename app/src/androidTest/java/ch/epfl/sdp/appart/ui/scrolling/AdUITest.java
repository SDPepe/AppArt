package ch.epfl.sdp.appart.ui.scrolling;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.database.Database;
import ch.epfl.sdp.appart.database.MockDataBase;
import ch.epfl.sdp.appart.hilt.FireBaseModule;
import ch.epfl.sdp.appart.scrolling.ad.Ad;
import ch.epfl.sdp.appart.scrolling.ad.AnnounceActivity;
import ch.epfl.sdp.appart.virtualtour.PanoramaGlActivity;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@UninstallModules(FireBaseModule.class)
@HiltAndroidTest
public class AdUITest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    static final String cardID = "1PoUWbeNHvMNotxwAui5";
    static final Intent intent;

    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), AnnounceActivity.class);
        intent.putExtra("cardID", cardID);
    }

    @Rule(order = 1)
    public ActivityScenarioRule<AnnounceActivity> scrollingActivityRule = new ActivityScenarioRule<>(intent);

    @BindValue
    final
    Database database = new MockDataBase();

    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
    }

    @Test
    public void clickOnVTourOpensVTourActivity() {

        onView(withId(R.id.vtourButton)).perform(click());
        intended(hasComponent(PanoramaGlActivity.class.getName()));
    }

    @Test
    public void contactDialogTests() {
        onView(withId(R.id.contactInfoButton)).perform(scrollTo()).perform(click());
        onView(withText("Close"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.dialogView)).check(doesNotExist());
    }

    @Test
    public void displayAdInfoTest() {
        Ad testAd = database.getAd(cardID).join();

        onView(withId(R.id.titleField)).check(matches(withText(testAd.getTitle())));
        onView(withId(R.id.addressField)).check(matches(withText(testAd.getAddress())));
        onView(withId(R.id.priceField)).check(matches(withText(testAd.getPrice())));
        onView(withId(R.id.descriptionField)).check(matches(withText(testAd.getDescription())));
        onView(withId(R.id.userField)).check(matches(withText(testAd.getContactInfo().name)));
    }

    @Test
    public void displayContactInfoTest() {
        Ad testAd = database.getAd(cardID).join();

        onView(withId(R.id.contactInfoButton)).perform(scrollTo(), click());


        onView(withId(R.id.usernameTextView)).check(matches(withText(testAd.getContactInfo().name)));
        onView(withId(R.id.emailField)).check(matches(withText(testAd.getContactInfo().userEmail)));
        onView(withId(R.id.phoneField)).check(matches(withText(testAd.getContactInfo().userPhoneNumber)));
    }

    @After
    public void release() {
        Intents.release();
    }
}
