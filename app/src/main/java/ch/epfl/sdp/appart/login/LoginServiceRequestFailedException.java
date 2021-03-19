package ch.epfl.sdp.appart.login;

public class LoginServiceRequestFailedException extends Exception {
    public LoginServiceRequestFailedException(String errorMsg) {
        super(errorMsg);
    }
}
