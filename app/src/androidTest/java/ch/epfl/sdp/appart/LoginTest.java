package ch.epfl.sdp.appart;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import ch.epfl.sdp.appart.login.FirebaseLoginService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.user.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

    @Test
    public void loginTest() throws InterruptedException {
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);


        CountDownLatch authCreate = new CountDownLatch(1);
        LoginService loginService = FirebaseLoginService.buildLoginService();

        String email = "test@testappart.com";
        String password = "password1234";

        loginService.createUser(email, password, hasSucceeded -> authCreate.countDown());
        authCreate.await();

        CountDownLatch authLogin = new CountDownLatch(1);

        loginService.loginWithEmail(email, password, hasSucceeded -> authLogin.countDown());

        authLogin.await();

        User user = loginService.getCurrentUser();

        assertNotNull(user);

        assertThat(user.getUserEmail(), is(email));

        CountDownLatch authUpdateEmail = new CountDownLatch(1);

        String newEmail = "test2@testappart.com";
        loginService.updateEmailAddress(newEmail, hasSucceeded -> authUpdateEmail.countDown());
        authUpdateEmail.await();

        user = loginService.getCurrentUser();
        assertNotNull(user);
        assertThat(user.getUserEmail(), is(newEmail));

        CountDownLatch authDelete = new CountDownLatch(1);

        loginService.deleteUser(hasSucceeded -> authDelete.countDown());
        authDelete.await();

        assertNull(loginService.getCurrentUser());

    }
}
