package ch.epfl.sdp.appart;


import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.ScrollingActivity;
import ch.epfl.sdp.appart.user.CreateUserActivity;
import ch.epfl.sdp.appart.user.LoginActivity;
import ch.epfl.sdp.appart.user.ResetActivity;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

@HiltAndroidTest
public class LoginUITest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<LoginActivity> loginActivityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Inject
    LoginService loginService;

    @Before
    public void init() {
        hiltRule.inject();
        Intents.init();
        loginService.useEmulator("10.0.2.2", 9099);
    }

    @Test
    public void failedLoginTest() {
        String email = "test@testappart.com";
        String password = "password";

        onView(withId(R.id.email_login)).perform(typeText(email));
        onView(withId(R.id.password)).perform(typeText(password));
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.login_failed_snack)));
    }

    @Test
    public void goToCreateAccountTest() {
        onView(withId(R.id.button_create_account)).perform(click());
        intended(hasComponent(CreateUserActivity.class.getName()));
    }

    @Test
    public void goToPasswordResetTest() {
        onView(withId(R.id.reset_password_button)).perform(click());
        intended(hasComponent(ResetActivity.class.getName()));
    }

    @Test
    public void successfulLoginTest() throws ExecutionException, InterruptedException {
        String email = "test@testappart.com";
        String password = "password";
        loginService.createUser(email, password).get();

        onView(withId(R.id.email_login)).perform(typeText(email));
        onView(withId(R.id.password)).perform(typeText(password));
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        intended(hasComponent(ScrollingActivity.class.getName()));

        User user = loginService.getCurrentUser();
        assertNotNull(user);

        assertThat(user.getUserEmail(), is(email));

        loginService.deleteUser().get();
    }
}
