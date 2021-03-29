package ch.epfl.sdp.appart.login.exceptions;

public class LoginServiceRequestFailedException extends Exception {
    public LoginServiceRequestFailedException(String errorMsg) {
        super(errorMsg);
    }
}
