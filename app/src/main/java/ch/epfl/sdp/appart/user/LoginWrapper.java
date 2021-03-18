package ch.epfl.sdp.appart.user;

import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.login.LoginService;

public class LoginWrapper {
    private final LoginService loginService;

    public LoginWrapper(LoginService loginService) {
        if(loginService == null) throw new IllegalArgumentException();
        this.loginService = loginService;
    }

    public CompletableFuture<User> login(String email, String password) {

        return null;
    }
}
