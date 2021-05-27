package ch.epfl.sdp.appart;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import ch.epfl.sdp.appart.MainActivity;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.database.local.MockLocalDatabase;
import ch.epfl.sdp.appart.database.preferences.SharedPreferencesHelper;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LocalDatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
@UninstallModules({LoginModule.class, DatabaseModule.class, LocalDatabaseModule.class})
@HiltAndroidTest
public class FavoriteUITest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainActivity> mActivityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    @BindValue
    LoginService login = new MockLoginService();
    @BindValue
    DatabaseService database = new MockDatabaseService();
    @BindValue
    LocalDatabaseService localdb = new MockLocalDatabase();


    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
        // clear shared preferences to avoid auto-login
        mActivityTestRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);
    }
    
    /**
     * taken from :
     * https://stackoverflow.com/questions/29378552/in-espresso-how-to-avoid-ambiguousviewmatcherexception-when-multiple-views-matc
     * Allows to select the index th view.
     *
     * @param matcher the matcher on the view
     * @param index   the index of the view we want to match
     * @return a Matcher on the View
     */
    private static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }

    @Test
    public void favoriteUITest() {
        // clear shared preferences to avoid auto-login
        mActivityTestRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);


        ViewInteraction appCompatEditText = onView(
                Matchers.allOf(ViewMatchers.withId(R.id.email_Login_editText),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("emilien@epfl.ch"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.password_Login_editText),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("5555"), closeSoftKeyboard());

        onView(withId(R.id.login_Login_button)).perform(click());

        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_Scrolling_toolbar),
                                        1),
                                0),
                        isDisplayed()));
        overflowMenuButton.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Favorites"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recycler_favorites),
                        withParent(allOf(withId(R.id.columnLayout_Scrolling_LinearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        recyclerView.check(matches(isDisplayed()));

        pressBack();

        ViewInteraction appCompatImageView = onView(withIndex(withId(R.id.image_CardLayout_imageView), 0));
        appCompatImageView.perform(forceClick());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_add_favorite), withContentDescription("Add to Favorites"),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction overflowMenuButton3 = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.account_Ad_toolbar),
                                        1),
                                1),
                        isDisplayed()));
        overflowMenuButton3.perform(click());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.title), withText("Favorites"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView2.perform(click());

        onView(allOf(withIndex(withId(R.id.image_CardLayout_imageView), 0),isDisplayed()));


        onView(allOf(withIndex(withId(R.id.image_CardLayout_imageView), 1),isDisplayed()));


        pressBack();

        pressBack();

        ViewInteraction appCompatImageView3 = onView(withIndex(withId(R.id.image_CardLayout_imageView), 0));
        appCompatImageView3.perform(forceClick());

        ViewInteraction actionMenuItemView3 = onView(
                allOf(withId(R.id.action_add_favorite), withContentDescription("Add to Favorites"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.account_Ad_toolbar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView3.perform(click());

        pressBack();

        ViewInteraction appCompatImageView4 = onView(withIndex(withId(R.id.image_CardLayout_imageView), 1));
        appCompatImageView4.perform(forceClick());

        ViewInteraction actionMenuItemView4 = onView(
                allOf(withId(R.id.action_add_favorite), withContentDescription("Add to Favorites"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.account_Ad_toolbar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView4.perform(click());

        ViewInteraction overflowMenuButton4 = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.account_Ad_toolbar),
                                        1),
                                1),
                        isDisplayed()));
        overflowMenuButton4.perform(click());

        ViewInteraction appCompatTextView3 = onView(
                allOf(withId(R.id.title), withText("Favorites"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView3.perform(click());

        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.recycler_favorites),
                        withParent(allOf(withId(R.id.columnLayout_Scrolling_LinearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        recyclerView3.check(matches(isDisplayed()));
    }

    private static ViewAction forceClick() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isClickable();
            }

            @Override
            public String getDescription() {
                return "force click";
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.performClick();
                uiController.loopMainThreadUntilIdle();
            }
        };
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
        mActivityTestRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);
        login.signOut();
    }
    
}
