package ch.epfl.sdp.appart.login;

import java.util.concurrent.CompletableFuture;

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
     * @throws IllegalArgumentException if one the arguments is null
     */
    CompletableFuture<User> loginWithEmail(String email, String password);

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
     * @throws IllegalArgumentException if one of the arguments is null
     */
    CompletableFuture<Void> resetPasswordWithEmail(String email);

    /**
     * Creates a user with an email and a password
     *
     * @param email    the user's email
     * @param password the user's password
     * @param callback what should be done after success or failure
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<Void> createUser(String email, String password);

    /**
     * Updates the email address of a user
     *
     * @param email    the new email to set
     * @param callback what is performed on update
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<Void> updateEmailAddress(String email);

    /**
     * Updates the password of a user
     *
     * @param password the new password to set
     * @param callback what is performed on password change if it succeeds or on failure
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<Void> updatePassword(String password);

    /**
     * Verifies an user's email
     *
     * @param callback what is performed on send success or failure
     * @throws IllegalArgumentException if callback is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<Void> sendEmailVerification();

    /**
     * Deletes a user
     *
     * @param callback what is performed on deletion success or failure
     * @throws IllegalArgumentException if callback is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<Void> deleteUser();

    /**
     * Re-authenticates a user. This is needed for things like password change or critical operations
     *
     * @param email    the user's email
     * @param password the user's password
     * @param callback what is performed on re-authentication success or failure
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<Void> reAuthenticateUser(String email, String password);
}
