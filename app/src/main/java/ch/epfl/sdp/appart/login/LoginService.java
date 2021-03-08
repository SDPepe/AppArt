package ch.epfl.sdp.appart.login;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;

import ch.epfl.sdp.appart.user.User;

/**
 * Interface that any Login service should implement
 */
public interface LoginService {

    /**
     * Performs a user login with email and password.
     *
     * @param email    the user's email
     * @param password the user's password
     * @param callback what should be done on login success or failure
     */
    void loginWithEmail(String email, String password, OnCompleteListener<AuthResult> callback);

    /**
     * Retrieves the current user if there is one, null otherwise.
     *
     * @return the current user if logged-in, null otherwise
     */
    User getCurrentUser();

    /**
     * Resets the password associated to an email if said email is linked with an account
     * We do not indicate if the email was found or not to prevent malicious users to know if someone is registered or not
     *
     * @param email    the user's email
     * @param callback what should be done after success or failure
     */
    void resetPasswordWithEmail(String email, OnCompleteListener<Void> callback);

    /**
     * Creates a user with an email and a password
     *
     * @param email    the user's email
     * @param password the user's password
     * @param callback what should be done after success or failure
     */
    void createUser(String email, String password, OnCompleteListener<AuthResult> callback);
}
