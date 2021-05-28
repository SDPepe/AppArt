package ch.epfl.sdp.appart;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.database.preferences.SharedPreferencesHelper;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static org.junit.Assert.assertEquals;

@HiltAndroidTest
public class SharedPreferencesTest {

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule(order = 1)
    public ActivityScenarioRule<ScrollingActivity> mActivityRule = new ActivityScenarioRule<>(ScrollingActivity.class);

    @Before
    public void init() {
        hiltRule.inject();
        Intents.init();
        mActivityRule.getScenario().onActivity(SharedPreferencesHelper::clearSavedUserForAutoLogin);
    }

    @Test
    public void sharedPreferencesTest() {
        mActivityRule.getScenario().onActivity(ac -> {
            SharedPreferencesHelper.saveUserForAutoLogin(ac, "test@testappart.ch", "password");
        });
        mActivityRule.getScenario().onActivity(ac -> {
            String email = SharedPreferencesHelper.getSavedEmail(ac);
            String password = SharedPreferencesHelper.getSavedPassword(ac);
            assertEquals("test@testappart.ch", email);
            assertEquals("password", password);

            SharedPreferencesHelper.clearSavedUserForAutoLogin(ac);
            assertEquals("", SharedPreferencesHelper.getSavedEmail(ac));
            assertEquals("", SharedPreferencesHelper.getSavedPassword(ac));
        });
    }

    @After
    public void release() {
        Intents.release();
    }
}
