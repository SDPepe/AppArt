package ch.epfl.sdp.appart.login;

import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.user.User;

public class FirebaseEmulatorLoginServiceWrapper implements LoginService {

    private final FirebaseLoginService loginService;

    public FirebaseEmulatorLoginServiceWrapper(@NonNull FirebaseLoginService loginService, @NonNull String ip, int port) {
        if (loginService == null || ip == null) throw new IllegalArgumentException("arguments cannot be null");
        loginService.useEmulator(ip, port);
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
    public void useEmulator(String ip, int port) {
        throw new UnsupportedOperationException("emulator already in use");
    }
}
