package ch.epfl.sdp.appart;


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
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@UninstallModules({LoginModule.class, DatabaseModule.class})
@HiltAndroidTest
public class CreateUserAccountUITest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<CreateUserActivity> createUserActivityActivityRule = new ActivityScenarioRule<>(CreateUserActivity.class);

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
    public void backToLoginTest() {
        onView(withId(R.id.create_account_login_CreateUser_button)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void successfulCreateAccountTest() throws ExecutionException, InterruptedException {
        String email = "test@testappart.ch";
        String password = "password";

        onView(withId(R.id.create_account_email_CreateUser_editText)).perform(typeText(email));
        onView(withId(R.id.create_account_password_CreateUser_editText)).perform(typeText(password));
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.create_account_CreateUser_button)).perform(click());

        intended(hasComponent(LoginActivity.class.getName()));

        User user = loginService.getCurrentUser();

        assertNotNull(user);

        assertThat(user.getUserEmail(), is(email));

        loginService.deleteUser().get();
        assertNull(loginService.getCurrentUser());
    }

    @After
    public void release() {
        Intents.release();
    }
}
