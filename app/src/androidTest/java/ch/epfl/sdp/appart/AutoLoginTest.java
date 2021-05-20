package ch.epfl.sdp.appart;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.database.preferences.SharedPreferencesHelper;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@UninstallModules({LoginModule.class, DatabaseModule.class})
@HiltAndroidTest
public class AutoLoginTest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule(order = 1)
    public ActivityScenarioRule<LoginActivity> mActivityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @BindValue
    final
    LoginService loginService = new MockLoginService();
    @BindValue
    final DatabaseService databaseService = new MockDatabaseService();

    @Before
    public void init() {
        hiltRule.inject();
        loginService.signOut();
        mActivityRule.getScenario().onActivity(ac -> {
            SharedPreferencesHelper.clearSavedUserForAutoLogin(ac);
            SharedPreferencesHelper.saveUserForAutoLogin(ac, "test@testappart.ch", "password");
        });
        Intents.init();
    }

    @Test
    public void autoLoginTest() throws InterruptedException {
        //wait for autologin to happen
        Thread.sleep(2000);
        intended(hasComponent(ScrollingActivity.class.getName()));
    }

    @After
    public void release() {
        mActivityRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);
        loginService.signOut();
        Intents.release();
    }

}
