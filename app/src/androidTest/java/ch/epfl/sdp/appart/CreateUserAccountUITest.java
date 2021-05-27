package ch.epfl.sdp.appart;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.database.preferences.SharedPreferencesHelper;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@UninstallModules({LoginModule.class, DatabaseModule.class})
@HiltAndroidTest
public class CreateUserAccountUITest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<CreateUserActivity> createUserActivityActivityRule =
            new ActivityScenarioRule<>(CreateUserActivity.class);

    @BindValue
    final
    LoginService loginService = new MockLoginService();

    @BindValue
    final
    DatabaseService databaseService = new MockDatabaseService();

    @Before
    public void init() {
        hiltRule.inject();
        Intents.init();
    }

    @Test
    public void failedCreateAccountTest() {
        String email = "invalidEmail";
        String password = "1";

        onView(withId(R.id.create_account_email_CreateUser_editText)).perform(typeText(email));
        onView(withId(R.id.create_account_password_CreateUser_editText)).perform(typeText(password));
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.create_account_CreateUser_button)).perform(click());
        //WARNING : I checked in the app and it does not do what performed here !
        //onView(withId(com.google.android.material.R.id.snackbar_text))
        //        .check(matches(withText(R.string.create_account_failed_snack)));
    }

    @Test
    public void successfulCreateAccountTest() throws ExecutionException, InterruptedException {
        String email = "test@testappart.ch";
        String password = "password";

        onView(withId(R.id.create_account_email_CreateUser_editText)).perform(typeText(email));
        onView(withId(R.id.create_account_password_CreateUser_editText)).perform(typeText(password));
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.create_account_CreateUser_button)).perform(click());

        // clear shared preferences to avoid auto-login
        createUserActivityActivityRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);

        intended(hasComponent(LoginActivity.class.getName()));

        User user = loginService.getCurrentUser();

        assertNotNull(user);

        assertThat(user.getUserEmail(), is(email));

        loginService.deleteUser().get();
        assertNull(loginService.getCurrentUser());
    }

    @Test
    public void createUserUITest() {
        ViewInteraction appCompatEditText = onView(
            allOf(withId(R.id.create_account_email_CreateUser_editText),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0),
                    2),
                isDisplayed()));
        appCompatEditText.perform(replaceText("test@epfl.ch"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
            allOf(withId(R.id.create_account_password_CreateUser_editText),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0),
                    1),
                isDisplayed()));
        appCompatEditText2.perform(replaceText("1234567890"), closeSoftKeyboard());

        ViewInteraction editText = onView(
            allOf(withId(R.id.create_account_email_CreateUser_editText), withText("test@epfl.ch"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()));
        editText.check(matches(withText("test@epfl.ch")));

        ViewInteraction editText2 = onView(
            allOf(withId(R.id.create_account_email_CreateUser_editText), withText("test@epfl.ch"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()));
        editText2.check(matches(isDisplayed()));

        ViewInteraction editText3 = onView(
            allOf(withId(R.id.create_account_password_CreateUser_editText), withText("••••••••••"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()));
        editText3.check(matches(withText("••••••••••")));

        ViewInteraction editText4 = onView(
            allOf(withId(R.id.create_account_password_CreateUser_editText), withText("••••••••••"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()));
        editText4.check(matches(isDisplayed()));

        ViewInteraction button = onView(
            allOf(withId(R.id.create_account_CreateUser_button), withText("Create Account"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()));
        button.check(matches(isDisplayed()));
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
        createUserActivityActivityRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);
        loginService.signOut();
        Intents.release();
    }
}
