package ch.epfl.sdp.appart.favoritesui;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.FavoriteActivity;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.database.preferences.SharedPreferencesHelper;
import ch.epfl.sdp.appart.favorites.FavoriteViewModel;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import ch.epfl.sdp.appart.favoritesui.MockFavoriteViewModel;
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
 * Test class for FavoriteActivtiy where the viewmodel returns exceptionally from init().
 */
@UninstallModules({LoginModule.class, DatabaseModule.class})
@HiltAndroidTest
public class FavoriteFailedInitTest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<FavoriteActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(FavoriteActivity.class);

    @BindValue
    LoginService login = new MockLoginService();

    @BindValue
    DatabaseService database = new MockDatabaseService();


    //Inject the mock viewmodel so that init always returns an exceptionally completed future
    @BindValue
    FavoriteViewModel mViewModel = new MockFavoriteViewModel(database, login);

    private View decorView;

    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
        mActivityScenarioRule.getScenario().onActivity(new ActivityScenario.ActivityAction<FavoriteActivity>() {
            @Override
            public void perform(FavoriteActivity ac) {
                decorView = ac.getWindow().getDecorView();
            }
        });
    }

    @Test
    public void exceptionallyInitShowsToast() {
        mActivityScenarioRule.getScenario().onActivity(ac -> {
            SharedPreferencesHelper.clearSavedUserForAutoLogin(ac);
            ac.recreate();
        });
        onView(withText("mock"))
                .inRoot(withDecorView(not(decorView)))// Here we use decorView
                .check(matches(isDisplayed()));

    }

    @After
    public void release() {
        Intents.release();
    }
}
