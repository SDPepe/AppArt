package ch.epfl.sdp.appart;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import ch.epfl.sdp.appart.user.LoginActivity;
import ch.epfl.sdp.appart.user.ResetActivity;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNull;

@UninstallModules(LoginModule.class)
@HiltAndroidTest
public class ResetPasswordTest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<ResetActivity> resetPasswordActivityRule = new ActivityScenarioRule<>(ResetActivity.class);

    @BindValue
    LoginService loginService = new MockLoginService();

    @Before
    public void init() {
        hiltRule.inject();
        Intents.init();
        //loginService.useEmulator("10.0.2.2", 9099);
    }

    @Test
    public void goBackToLoginTest() {
        onView(withId(R.id.button_log_in_from_reset)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void resetPasswordOnNonExistingUserTest() {
        String email = "fakeuser@testappart.ch";
        onView(withId(R.id.reset_email)).perform(typeText(email));
        onView(withId(R.id.button_reset_password)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.invalid_email_snack)));
    }

    @Test
    public void resetPasswordSuccessfulTest() throws ExecutionException, InterruptedException {
        String email = "test@testappart.ch";
        String password = "password";
        loginService.createUser(email, password).get();
        onView(withId(R.id.reset_email)).perform(typeText(email));
        onView(withId(R.id.button_reset_password)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
        loginService.deleteUser().get();
        assertNull(loginService.getCurrentUser());
    }

    @Test
    public void resetPasswordWithInvalidEmailTest() {
        String email = "invalidEmail";
        onView(withId(R.id.reset_email)).perform(typeText(email));
        onView(withId(R.id.button_reset_password)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.invalid_email_snack)));
    }

    @After
    public void release() {
        Intents.release();
    }
}
