package ch.epfl.sdp.appart.login;

import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.user.User;

/**
 * This class wraps the firebase login service.
 * This class should only be used when the firebase emulator
 * is setup on local ip 10.0.2.2 with port 9099.
 */
public class FirebaseEmulatorLoginServiceWrapper implements LoginService {

    private final FirebaseLoginService loginService;
    private static final String LOCALHOST = "10.0.2.2";
    private static final int AUTH_SERVICE_PORT = 9099;

    public FirebaseEmulatorLoginServiceWrapper(@NonNull FirebaseLoginService loginService) {
        if (loginService == null) throw new IllegalArgumentException("loginService cannot be null");
        loginService.useEmulator(LOCALHOST, AUTH_SERVICE_PORT);
        this.loginService = loginService;
    }

    @Override
    public CompletableFuture<User> loginWithEmail(String email, String password) {
        return loginService.loginWithEmail(email, password);
    }

    @Override
    public User getCurrentUser() {
        return loginService.getCurrentUser();
    }

    @Override
    public CompletableFuture<Void> resetPasswordWithEmail(String email) {
        return loginService.resetPasswordWithEmail(email);
    }

    @Override
    public CompletableFuture<User> createUser(String email, String password) {
        return loginService.createUser(email, password);
    }

    @Override
    public CompletableFuture<Void> updateEmailAddress(String email) {
        return loginService.updateEmailAddress(email);
    }

    @Override
    public CompletableFuture<Void> updatePassword(String password) {
        return loginService.updatePassword(password);
    }

    @Override
    public CompletableFuture<Void> sendEmailVerification() {
        return loginService.sendEmailVerification();
    }

    @Override
    public CompletableFuture<Void> deleteUser() {
        return loginService.deleteUser();
    }

    @Override
    public CompletableFuture<Void> reAuthenticateUser(String email, String password) {
        return loginService.reAuthenticateUser(email, password);
    }

    @Override
    public void signOut() {
        this.loginService.signOut();
    }

    @Override
    public CompletableFuture<User> signInAnonymously() {
        return loginService.signInAnonymously();
    }

}
