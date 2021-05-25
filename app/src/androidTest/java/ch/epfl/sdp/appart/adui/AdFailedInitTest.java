package ch.epfl.sdp.appart.adui;

import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.AdActivity;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.ad.AdViewModel;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.database.preferences.SharedPreferencesHelper;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Test class for AdActivity where init of the ViewModel fails.
 */
@UninstallModules({DatabaseModule.class, LoginModule.class})
@HiltAndroidTest
public class AdFailedInitTest {

    static final String testId = "unknown";
    static final Intent intent;

    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), AdActivity.class);
        intent.putExtra(ActivityCommunicationLayout.PROVIDING_AD_ID, testId);
        intent.putExtra(ActivityCommunicationLayout.PROVIDING_CARD_ID, testId);
    }

    @BindValue
    final DatabaseService database = new MockDatabaseService();
    @BindValue
    final LoginService login = new MockLoginService();
    @BindValue
    AdViewModel mViewModel = new MockInitAdViewModel(database);

    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule(order = 1)
    public ActivityScenarioRule<AdActivity> mScenarioRule = new ActivityScenarioRule<>(intent);

    private View decorView;

    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
        mScenarioRule.getScenario().onActivity(new ActivityScenario.ActivityAction<AdActivity>() {
            @Override
            public void perform(AdActivity ac) {
                decorView = ac.getWindow().getDecorView();
            }
        });
    }

    @After
    public void release() {
        Intents.release();
    }

}
