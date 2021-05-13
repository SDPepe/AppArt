package ch.epfl.sdp.appart;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import android.widget.FrameLayout;
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

import ch.epfl.sdp.appart.configuration.ApplicationConfiguration;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.AppConfigurationModule;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


//@RunWith(AndroidJUnit4.class)
@UninstallModules({DatabaseModule.class, AppConfigurationModule.class})
@HiltAndroidTest
public class ScrollingUITest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<ScrollingActivity> scrollingActivityRule = new ActivityScenarioRule<>(ScrollingActivity.class);

    @BindValue
    DatabaseService database = new MockDatabaseService();

    //just to reset demo mode
    @BindValue
    ApplicationConfiguration configuration = new ApplicationConfiguration();

    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
    }

    @Test
    public void clickOnImageViewFromCardViewStartAnnounceActivity() throws InterruptedException {
        ViewInteraction appCompatImageView = onView(
            ViewUtils.withIndex(withId(R.id.image_CardLayout_imageView),
                0));
        appCompatImageView.perform(forceClick());
        intended(hasComponent(AdActivity.class.getName()));
    }
    public static ViewAction forceClick() {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() {
                return isClickable();
            }

            @Override public String getDescription() {
                return "force click";
            }

            @Override public void perform(UiController uiController, View view) {
                view.performClick(); // perform click without checking view coordinates.
                uiController.loopMainThreadUntilIdle();
            }
        };
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
                allOf(withId(R.id.login_Login_button), withText("Log In!"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
    }

    @Test
    public void searchBarTest(){
        ViewInteraction appCompatEditText = onView(
            allOf(withId(R.id.search_bar_Scrolling_editText),
                childAtPosition(
                    allOf(withId(R.id.columnLayout_Scrolling_LinearLayout),
                        childAtPosition(
                            withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                            0)),
                    0),
                isDisplayed()));
        appCompatEditText.perform(replaceText("1000"), closeSoftKeyboard());

        ViewInteraction editText = onView(allOf(withId(R.id.search_bar_Scrolling_editText),
            childAtPosition(
                allOf(withId(R.id.columnLayout_Scrolling_LinearLayout),
                    childAtPosition(
                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                        0)),
                0),
            isDisplayed()));
        editText.check(matches(isDisplayed()));

        ViewInteraction editText2 = onView(allOf(withId(R.id.search_bar_Scrolling_editText),
            childAtPosition(
                allOf(withId(R.id.columnLayout_Scrolling_LinearLayout),
                    childAtPosition(
                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                        0)),
                0),
            isDisplayed()));
        editText2.check(matches(withText("1000")));
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


    @After
    public void release() {
        Intents.release();
    }

}
