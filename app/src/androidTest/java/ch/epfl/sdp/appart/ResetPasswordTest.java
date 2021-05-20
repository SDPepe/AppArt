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

import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
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
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNull;

@UninstallModules(LoginModule.class)
@HiltAndroidTest
public class ResetPasswordTest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<ResetActivity> resetPasswordActivityRule = new ActivityScenarioRule<>(ResetActivity.class);

    @BindValue
    final
    LoginService loginService = new MockLoginService();

    @Before
    public void init() {
        hiltRule.inject();
        Intents.init();
        //loginService.useEmulator("10.0.2.2", 9099);
    }

    @Test
    public void resetPasswordOnNonExistingUserTest() {
        String email = "fakeuser@testappart.ch";
        onView(withId(R.id.email_Reset_editText)).perform(typeText(email));
        onView(withId(R.id.password_Reset_button)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.invalid_email_snack)));
    }

    @Test
    public void resetPasswordSuccessfulTest() throws ExecutionException, InterruptedException {
        String email = "test@testappart.ch";
        String password = "password";
        loginService.createUser(email, password).get();
        onView(withId(R.id.email_Reset_editText)).perform(typeText(email));
        onView(withId(R.id.password_Reset_button)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
        loginService.deleteUser().get();
        assertNull(loginService.getCurrentUser());
    }

    @Test
    public void resetPasswordWithInvalidEmailTest() {
        String email = "invalidEmail";
        onView(withId(R.id.email_Reset_editText)).perform(typeText(email));
        onView(withId(R.id.password_Reset_button)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.invalid_email_snack)));
    }
    @Test
    public void resetPasswordUiTest() {
        ViewInteraction appCompatEditText = onView(
            allOf(withId(R.id.email_Reset_editText),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0),
                    1),
                isDisplayed()));
        appCompatEditText.perform(replaceText("test@epfl.ch"), closeSoftKeyboard());

        ViewInteraction editText = onView(
            allOf(withId(R.id.email_Reset_editText), withText("test@epfl.ch"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()));
        editText.check(matches(withText("test@epfl.ch")));

        ViewInteraction editText2 = onView(
            allOf(withId(R.id.email_Reset_editText), withText("test@epfl.ch"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()));
        editText2.check(matches(isDisplayed()));

        ViewInteraction button = onView(
            allOf(withId(R.id.password_Reset_button), withText("Reset password"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
            allOf(withText("If this address is valid, you will receive a link to change your password"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()));
        textView.check(matches(
            withText("If this address is valid, you will receive a link to change your password")));

        ViewInteraction textView2 = onView(
            allOf(withText("If this address is valid, you will receive a link to change your password"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()));
        textView2.check(matches(isDisplayed()));
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
