package ch.epfl.sdp.appart;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

@UninstallModules(LoginModule.class)
@HiltAndroidTest
public class LoginTest {

    /*
                   DISCLAIMER !!!!!!!!!!!!!!!!!!!!!!!!!

       I know using CountDownLatch and directly testing Firebase is not good practice but it ensures a high
       enough total coverage. These tests will be removed when the code base is large enough to allow it.

       Also, even though we are using CountDownLatch which is not good practice, the tests are run on a local
       emulator.

       Finally, these tests allow us to see how practical the loginService is to use and how complete it must be.

       @ADGLY

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
    final
    LoginService loginService = new FirebaseEmulatorLoginServiceWrapper(new FirebaseLoginService());

    /**
     * It is necessary to perform the emulator tests in one single test, otherwise we get initialization issues with useEmulator.
     * The other solution is to add a terminate function to both the wrapper and the service.
     */

    public void createUser(String email, String password) {
        loginService.createUser(email, password).join();

        User user = loginService.getCurrentUser();

        assertNotNull(user);

        assertThat(user.getUserEmail(), is(email));
    }

    public void updateMail(String newEmail) {
        loginService.updateEmailAddress(newEmail).join();

        User user = loginService.getCurrentUser();
        assertNotNull(user);
        assertThat(user.getUserEmail(), is(newEmail));
    }

    public void deleteUser() {
        loginService.deleteUser().join();
        assertNull(loginService.getCurrentUser());
    }

    public void signOut() {
        loginService.signOut();
        assertNull(loginService.getCurrentUser());
    }

    public void login(String email, String password) {
        loginService.loginWithEmail(email, password).join();

        User user = loginService.getCurrentUser();
        assertNotNull(user);
        assertThat(user.getUserEmail(), is(email));
    }

    public void updatePassword(String newPassword) {
        loginService.updatePassword(newPassword);
        signOut();
    }

    public void reAuthenticate(String email, String password) {
        loginService.reAuthenticateUser(email, password);
        User user = loginService.getCurrentUser();
        assertNotNull(user);

        assertThat(user.getUserEmail(), is(email));
    }

    public void loginFailInvalidArguments() {

        assertThrows(ExecutionException.class, () -> loginService.createUser("", "").get());
        assertThrows(ExecutionException.class, () -> loginService.createUser(null, "").get());

        assertThrows(ExecutionException.class, () -> loginService.createUser("invalidEmail", "password").get());

        assertThrows(ExecutionException.class, () -> loginService.loginWithEmail("", "").get());
        assertThrows(ExecutionException.class, () -> loginService.loginWithEmail(null, "").get());

        assertThrows(ExecutionException.class, () -> loginService.loginWithEmail("invalidEmail", "password").get());
        assertThrows(ExecutionException.class, () -> loginService.loginWithEmail("adg@testappart.ch", "wrongPassword").get());


        assertThrows(ExecutionException.class, () -> loginService.resetPasswordWithEmail(null).get());
        assertThrows(ExecutionException.class, () -> loginService.resetPasswordWithEmail("invalid").get());

        assertThrows(ExecutionException.class, () -> loginService.updateEmailAddress(null).get());
        assertThrows(ExecutionException.class, () -> loginService.updateEmailAddress("invalid").get());

        assertThrows(ExecutionException.class, () -> loginService.updatePassword(null).get());

        assertThrows(ExecutionException.class, () -> loginService.reAuthenticateUser("", "").get());
        assertThrows(ExecutionException.class, () -> loginService.reAuthenticateUser(null, "").get());

        assertThrows(ExecutionException.class, () -> loginService.reAuthenticateUser("invalidEmail", "password").get());
        assertThrows(ExecutionException.class, () -> loginService.reAuthenticateUser("adg@testappart.ch", "wrongPassword").get());
    }

    @Test
    public void loginTest() {

        String email = "test@testappart.ch";
        String password = "password1234";

        createUser(email, password);

        String newEmail = "test2@testappart.ch";
        updateMail(newEmail);

        deleteUser();

        createUser(email, password);
        signOut();
        login(email, password);
        String newPassword = "password12345";
        updatePassword(newPassword);

        login(email, newPassword);
        reAuthenticate(email, newPassword);

        deleteUser();

        loginFailInvalidArguments();

    }
}
