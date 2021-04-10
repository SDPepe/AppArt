package ch.epfl.sdp.appart;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
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

@UninstallModules(DatabaseModule.class)
@HiltAndroidTest
public class AdUITest {

    static final String cardID = "1PoUWbeNHvMNotxwAui5";
    static final Intent intent;

    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), AdActivity.class);
        intent.putExtra("cardID", cardID);
    }

    @BindValue
    final
    DatabaseService database = new MockDatabaseService();
    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule(order = 1)
    public ActivityScenarioRule<AdActivity> scrollingActivityRule = new ActivityScenarioRule<>(intent);

    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
    }

    @Test
    public void clickOnVTourOpensVTourActivity() {

        onView(withId(R.id.vtour_Ad_button)).perform(click());
        intended(hasComponent(PanoramaActivity.class.getName()));
    }

    @Test
    @Ignore("contact information will be shown as the User profile UI of the announcer, the contact info dialog has been replaced")
    public void contactDialogTests() {
        onView(withId(R.id.contact_info_Ad_button)).perform(scrollTo()).perform(click());
        onView(withText("Close"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.holder_ContactInfo_cardView)).check(doesNotExist());
    }

    @Test
    public void displayAdInfoTest() {
        Ad testAd = database.getAd(cardID).join();

        onView(withId(R.id.title_Ad_textView)).check(matches(withText(testAd.getTitle())));
        onView(withId(R.id.address_field_Ad_textView)).check(matches(withText(
                testAd.getStreet() + ", " + testAd.getCity())));
        onView(withId(R.id.price_field_Ad_textView)).check(matches(withText(
                testAd.getPrice() + " / " + testAd.getPricePeriod().toString())));
        onView(withId(R.id.description_field_Ad_textView)).check(matches(withText(testAd.getDescription())));
        onView(withId(R.id.user_field_Ad_textView)).check(matches(withText(testAd.getContactInfo().name)));
    }

    @Test
    @Ignore("contact information will be shown as the User profile UI of the announcer, the contact info dialog has been replaced")
    public void displayContactInfoTest() {
        Ad testAd = database.getAd(cardID).join();

        onView(withId(R.id.contact_info_Ad_button)).perform(scrollTo(), click());


        onView(withId(R.id.username_ContactInfo_textView)).check(matches(withText(testAd.getContactInfo().name)));
        onView(withId(R.id.email_ContactInfo_textView)).check(matches(withText(testAd.getContactInfo().userEmail)));
        onView(withId(R.id.phone_ContactInfo_textView)).check(matches(withText(testAd.getContactInfo().userPhoneNumber)));
    }

    @After
    public void release() {
        Intents.release();
    }
}
