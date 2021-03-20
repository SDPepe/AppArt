package ch.epfl.sdp.appart;


import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.appart.login.MockLoginService;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MockLoginServiceTest {

    private MockLoginService loginService;
    private User userTest;
    private final AppUser antoine = new AppUser("1111", "antoine@epfl.ch");

    @Before
    public void init() {
        loginService = new MockLoginService();
        userTest = new AppUser("123456", "test@epfl.ch");
    }

    @Test
    public void TestLoginWithEmail() throws ExecutionException, InterruptedException {
        User user = loginService.loginWithEmail("antoine@epfl.ch", "1111").get();
        assertNotNull(user);
        assertEquals(user.getUserEmail(), antoine.getUserEmail());
        assertEquals(user.getUserId(), antoine.getUserId());
        assertEquals(user, loginService.getCurrentUser());
    }

    @Test
    public void TestDummyResetPasswordWithEmail() {
        loginService.resetPasswordWithEmail("antoine@epfl.ch");
    }

    @Test
    public void TestCreateUser() throws ExecutionException, InterruptedException {
        loginService.loginWithEmail("antoine@epfl.ch", "1111").get();
        User user = loginService.createUser("antoine2@epfl.ch", "1234567").get();
        assertEquals(user.getUserEmail(), "antoine2@epfl.ch");
    }

    @Test
    public void TestUpdateEmailAddressOnLoggedInUser() throws ExecutionException, InterruptedException {
        loginService.loginWithEmail("antoine@epfl.ch", "1111").get();
        assertEquals(loginService.getCurrentUser().getUserEmail(), "antoine@epfl.ch");
        loginService.updateEmailAddress("antoine2g@epfl.ch").get();
        assertEquals(loginService.getCurrentUser().getUserEmail(), "antoine2g@epfl.ch");
    }

    @Test
    public void TestUpdatePasswordOnLoggedInUser() throws ExecutionException, InterruptedException {
        loginService.loginWithEmail("antoine@epfl.ch", "1111").get();
        loginService.updatePassword("dummynewpassword").get(); //unfortunately not tested since not in api
        assertEquals(loginService.getCurrentUser().getUserEmail(), "antoine@epfl.ch");
    }

    @Test
    public void TestSendEmailVerificationToLoggedInUser() throws ExecutionException, InterruptedException {
        loginService.loginWithEmail("antoine@epfl.ch", "1111").get();
        loginService.sendEmailVerification();
    }

    @Test//(expected = MockLoginServiceException.class)
    public void TestDeletedUserShouldFailOnLogin() throws ExecutionException, InterruptedException {
        loginService.loginWithEmail("antoine@epfl.ch", "1111").get();
        loginService.deleteUser().get();
        CompletableFuture<User> result = loginService.loginWithEmail("antoine@epfl.ch", "1111");
        result.exceptionally(x -> {
            assertTrue(true);
            return null;
        });
        result.thenApply(user -> {
            assertTrue(false);
            return user;
        });
    }

    @Test
    public void TestReAuthenticateLoggedInUser() throws ExecutionException, InterruptedException {
        loginService.loginWithEmail("antoine@epfl.ch", "1111").get();
        assertEquals(loginService.getCurrentUser().getUserEmail(), "antoine@epfl.ch");
        assertNotNull(loginService.getCurrentUser());
        loginService.reAuthenticateUser("antoine@epfl.ch", "1111");
        assertEquals(loginService.getCurrentUser().getUserEmail(), "antoine@epfl.ch");
        assertNotNull(loginService.getCurrentUser());
    }

}
