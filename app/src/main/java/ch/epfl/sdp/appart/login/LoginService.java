package ch.epfl.sdp.appart.login;

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
     * @return a boolean that indicates if the login was successful
     */
    boolean loginWithEmail(String email, String password);

    /**
     * Performs a user login with username and password.
     *
     * @param username the user's username
     * @param password the user's password
     * @return a boolean that indicates if the login was successful
     */
    boolean loginWithUsername(String username, String password);

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
     * @param email the user's email
     */
    void resetPasswordWithEmail(String email);

    /**
     * Creates a user with a username, an email and a password
     *
     * @param username the user's username
     * @param email    the user's email
     * @param password the user's password
     * @return the newly create user
     */
    User createUser(String username, String email, String password);
}
