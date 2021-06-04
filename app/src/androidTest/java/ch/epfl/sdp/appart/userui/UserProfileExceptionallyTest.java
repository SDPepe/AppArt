package ch.epfl.sdp.appart.userui;

import android.Manifest;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.MainActivity;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.UserProfileActivity;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.database.local.MockLocalDatabase;
import ch.epfl.sdp.appart.database.preferences.SharedPreferencesHelper;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import ch.epfl.sdp.appart.user.UserViewModel;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4ClassRunner.class)
@UninstallModules({LoginModule.class, DatabaseModule.class})
@HiltAndroidTest
public class UserProfileExceptionallyTest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<UserProfileActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(UserProfileActivity.class);

    /* Used to grant camera permission always */
    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule.grant();

    @BindValue
    DatabaseService database = new MockDatabaseService();

    @BindValue
    LoginService login = new MockLoginService();

    @BindValue
    UserViewModel mViewModel = new MockUserViewModel(database, login, new MockLocalDatabase());

    @Before
    public void init() {
        hiltRule.inject();
    }

    @Test
    public void exceptionallyOnCreate() {
        mActivityScenarioRule.getScenario().onActivity(ac -> {
            SharedPreferencesHelper.clearSavedUserForAutoLogin(ac);
            ac.recreate();
        });
        onView(withId(R.id.editProfile_UserProfile_button)).check(matches(not(isDisplayed())));
    }
}

class MockUserViewModel extends UserViewModel {

    public MockUserViewModel(DatabaseService database, LoginService loginService,
                             LocalDatabaseService localdb) {
        super(database, loginService, localdb);
    }

    @Override
    public CompletableFuture<Void> getCurrentUser() {
        CompletableFuture<Void> result = new CompletableFuture<>();
        result.completeExceptionally(new DatabaseServiceException("fail"));
        return result;
    }

}