package ch.epfl.sdp.appart;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.appart.hilt.FireBaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.FirebaseEmulatorLoginServiceWrapper;
import ch.epfl.sdp.appart.login.FirebaseLoginService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@UninstallModules(LoginModule.class)
@HiltAndroidTest
public class LoginTest {

    /**
     *              DISCLAIMER !!!!!!!!!!!!!!!!!!!!!!!!!
     *
     *  I know using CountDownLatch and directly testing Firebase is not good practice but it ensures a high
     *  enough total coverage. These tests will be removed when the code base is large enough to allow it.
     *
     *  Also, even though we are using CountDownLatch which is not good practice, the tests are run on a local
     *  emulator.
     *
     *  Finally, these tests allow us to see how practical the loginService is to use and how complete it must be.
     *
     *  @ADGLY
     *
     */

    /**
     * Update 20.03.21
     * This test is meant to use the emulator of firebase to add coverage to the project.
     * This test will fail if the emulator is not setup on local ip 10.0.2.2 with port 9099.
     * This should be the only test using the emulator.
     */

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @BindValue
    LoginService loginService = new FirebaseLoginService();//new FirebaseEmulatorLoginServiceWrapper(new FirebaseLoginService());

    @Test
    public void loginTest() throws InterruptedException, ExecutionException {

        String email = "test@testappart.ch";
        String password = "password1234";

        loginService.createUser(email, password).get();

        //loginService.loginWithEmail(email, password).get();

        User user = loginService.getCurrentUser();

        assertNotNull(user);

        assertThat(user.getUserEmail(), is(email));

        String newEmail = "test2@testappart.ch";
        loginService.updateEmailAddress(newEmail).get();

        user = loginService.getCurrentUser();
        assertNotNull(user);
        assertThat(user.getUserEmail(), is(newEmail));

        loginService.deleteUser().get();

        assertNull(loginService.getCurrentUser());

    }
}
