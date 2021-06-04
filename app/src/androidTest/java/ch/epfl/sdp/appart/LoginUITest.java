package ch.epfl.sdp.appart;


import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@UninstallModules({LoginModule.class, DatabaseModule.class})
@HiltAndroidTest
public class LoginUITest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<LoginActivity> loginActivityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @BindValue
    final
    LoginService loginService = new MockLoginService();
    @BindValue
    final DatabaseService databaseService = new MockDatabaseService();

    @Before
    public void init() {
        hiltRule.inject();
        // clear shared preferences to avoid auto-login
        loginActivityRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);
        loginService.signOut();
        Intents.init();
    }

    @Test
    public void failedLoginTest() {
        loginService.signOut();
        loginActivityRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);

        String email = "test@testappart.ch";
        String password = "wrongpassword";

        onView(withId(R.id.email_Login_editText)).perform(typeText(email));
        onView(withId(R.id.password_Login_editText)).perform(typeText(password));
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_Login_button)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.login_failed_snack)));
    }

    @Test
    public void goToCreateAccountTest() {
        // clear shared preferences to avoid auto-login
        loginActivityRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);
        loginService.signOut();
        onView(withId(R.id.create_account_Login_button)).perform(click());
        intended(hasComponent(CreateUserActivity.class.getName()));
    }

    @Test
    public void goToPasswordResetTest() {
        // clear shared preferences to avoid auto-login
        loginActivityRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);
        onView(withId(R.id.reset_password_Login_button)).perform(click());
        intended(hasComponent(ResetActivity.class.getName()));
    }

    @Test
    public void successfulLoginTest() throws ExecutionException, InterruptedException {
        // clear shared preferences to avoid auto-login
        loginActivityRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);
        loginService.signOut();

        String email = "test@testappart.ch";
        String password = "password";

        onView(withId(R.id.email_Login_editText)).perform(typeText(email));
        onView(withId(R.id.password_Login_editText)).perform(typeText(password));
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_Login_button)).perform(click());

        intended(hasComponent(ScrollingActivity.class.getName()));

        User user = loginService.getCurrentUser();
        assertNotNull(user);

        assertThat(user.getUserEmail(), is(email));

        loginService.deleteUser().get();

        assertNull(loginService.getCurrentUser());
        loginActivityRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);
    }

    @Test
    public void successfulAutoLoginTest() throws InterruptedException {
        String email = "test@testappart.ch";
        String password = "password";
        // set login info and recreate the app, so that onCreate is called
        loginActivityRule.getScenario().onActivity(ac -> {
            SharedPreferencesHelper.clearSavedUserForAutoLogin(ac);
            SharedPreferencesHelper.saveUserForAutoLogin(ac, email, password);
            ac.recreate();
        });
        Thread.sleep(2000);
        intended(hasComponent(ScrollingActivity.class.getName()));
        loginActivityRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);
    }

    @After
    public void release() {
        Intents.release();
        loginActivityRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);
        loginService.signOut();
    }
}
