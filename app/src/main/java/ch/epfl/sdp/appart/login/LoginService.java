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

    /**
     * Updates the email address of a user
     *
     * @param user     the user who's email address mus be updated
     * @param callback what is performed on update
     */
    void updateEmailAddress(User user, OnCompleteListener<Void> callback);

    /**
     * Updates the password of a user
     *
     * @param user     the user who's email address must be updated
     * @param callback what is performed on password change if it succeeds or on failure
     */
    void updatePassword(User user, OnCompleteListener<Void> callback);

    /**
     * Verifies an user's email
     *
     * @param user     the user who's email needs to be updated
     * @param callback what is performed on send success or failure
     */
    void sendEmailVerification(User user, OnCompleteListener<Void> callback);

    /**
     * Deletes a user
     *
     * @param user     the user we want to delete
     * @param callback what is performed on deletion success or failure
     */
    void deleteUser(User user, OnCompleteListener<Void> callback);

    /**
     * Re-authenticates a user. This is needed for things like password change or critical operations
     *
     * @param user     the user we want to re-authenticate
     * @param callback what is performed on re-authentication success or failure
     */
    void reAuthenticateUser(User user, OnCompleteListener<Void> callback);
}
