package ch.epfl.sdp.appart;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.uiautomator.UiDevice;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@UninstallModules({DatabaseModule.class, LoginModule.class})
@HiltAndroidTest
public class AdUITest {

    static final String testId = "unknown";
    static final Intent intent;

    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), AdActivity.class);
        intent.putExtra(ActivityCommunicationLayout.PROVIDING_AD_ID, testId);
        intent.putExtra(ActivityCommunicationLayout.PROVIDING_CARD_ID, testId);
    }

    @BindValue
    final
    DatabaseService database = new MockDatabaseService();
    @BindValue
    final
    LoginService login = new MockLoginService();
    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule(order = 1)
    public ActivityScenarioRule<AdActivity> adActivityRule = new ActivityScenarioRule<>(intent);

    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
    }

    @Test
    public void clickOnVTourOpensVTourActivity() {
        onView(withId(R.id.vtour_Ad_button)).perform(scrollTo(), click());
        intended(hasComponent(PanoramaActivity.class.getName()));
    }

    @Test
    public void clickOnContactInfoOpensUserPage() {
        onView(withId(R.id.contact_info_Ad_button)).perform(scrollTo(), click());
        intended(hasComponent(SimpleUserProfileActivity.class.getName()));
    }

    @Test
    public void clickOnImageOpensFullscreenPage() {
        onView(childAtPosition(withId(R.id.horizontal_children_Ad_linearLayout), 0)).perform(scrollTo(), click());
        intended(hasComponent(FullScreenImageActivity.class.getName()));
    }

    @Test
    public void clickOnFavoriteAddsToFavorites() {
        login.loginWithEmail("test@testappart.ch", "password").join();

        onView(withId(R.id.action_add_favorite)).perform(click());
        User currentUser = login.getCurrentUser();
        assertNotNull(currentUser);
        assertTrue(database.getUser(currentUser.getUserId()).join().getFavoritesIds().contains(testId));
        login.signOut();
    }

    @Test
    public void displayAdInfoTest() {
        Ad testAd = database.getAd(testId).join();

        onView(withId(R.id.title_Ad_textView)).check(matches(withText(testAd.getTitle())));
        onView(withId(R.id.address_field_Ad_textView)).check(matches(withText(
                testAd.getStreet() + ", " + testAd.getCity())));
        onView(withId(R.id.price_field_Ad_textView)).check(matches(withText(
                testAd.getPrice() + " / " + testAd.getPricePeriod().toString())));
        onView(withId(R.id.description_field_Ad_textView)).check(matches(withText(testAd.getDescription())));
    }

    @After
    public void release() {
        Intents.release();
        login.signOut();
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
