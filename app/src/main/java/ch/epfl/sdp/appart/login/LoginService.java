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
     * @return a completable future containing the User if the request is successful
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
     * @return an empty completable future
     * @throws IllegalArgumentException if one of the arguments is null
     */
    CompletableFuture<Void> resetPasswordWithEmail(String email);

    /**
     * Creates a user with an email and a password
     *
     * @param email    the user's email
     * @param password the user's password
     * @return a completable future containing the User if the request is successful
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<User> createUser(String email, String password);

    /**
     * Updates the email address of a user
     *
     * @param email    the new email to set
     * @return an empty completable future
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<Void> updateEmailAddress(String email);

    /**
     * Updates the password of a user
     *
     * @param password the new password to set
     * @return an empty completable future
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<Void> updatePassword(String password);

    /**
     * Verifies an user's email
     *
     * @return an empty completable future
     * @throws IllegalArgumentException if callback is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<Void> sendEmailVerification();

    /**
     * Deletes a user
     *
     * @return an empty completable future
     * @throws IllegalArgumentException if callback is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<Void> deleteUser();

    /**
     * Re-authenticates a user. This is needed for things like password change or critical operations
     *
     * @param email    the user's email
     * @param password the user's password
     * @return an empty completable future
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<Void> reAuthenticateUser(String email, String password);
}
