package ch.epfl.sdp.appart;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


//@RunWith(AndroidJUnit4.class)
@UninstallModules(DatabaseModule.class)
@HiltAndroidTest
public class ScrollingUITest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<ScrollingActivity> scrollingActivityRule = new ActivityScenarioRule<>(ScrollingActivity.class);

    @BindValue
    DatabaseService database = new MockDatabaseService();


    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
    }

    @Test
    public void clickOnImageViewFromCardViewStartAnnounceActivity() {

        ViewInteraction card = onView(ViewUtils.withIndex(withId(R.id.image_CardLayout_imageView), 0));
        card.perform(click());
        intended(hasComponent(AdActivity.class.getName()));

    }

    @Test
    public void clickOnFABStartsCreationActivity() {
        onView(withId(R.id.newAd_Scrolling_floatingActionButton)).perform(click());
        intended(hasComponent(AdCreationActivity.class.getName()));

    }

    @Test
    public void toolbarTest() {
        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("More options"),
                        ViewUtils.childAtPosition(
                                ViewUtils.childAtPosition(
                                        withId(R.id.login_Scrolling_toolbar),
                                        1),
                                0),
                        isDisplayed()));
        overflowMenuButton.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Settings"),
                        ViewUtils.childAtPosition(
                                ViewUtils.childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction overflowMenuButton3 = onView(
                allOf(withContentDescription("More options"),
                        ViewUtils.childAtPosition(
                                ViewUtils.childAtPosition(
                                        withId(R.id.login_Scrolling_toolbar),
                                        1),
                                0),
                        isDisplayed()));
        overflowMenuButton3.perform(click());

        ViewInteraction appCompatTextView3 = onView(
                allOf(withId(R.id.title), withText("Log Out"),
                        ViewUtils.childAtPosition(
                                ViewUtils.childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView3.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.login_Login_button), withText("LOGIN"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
    }

    @After
    public void release() {
        Intents.release();
    }

}
