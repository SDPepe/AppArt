package ch.epfl.sdp.appart.login;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import ch.epfl.sdp.appart.user.User;

/**
 * Interface that any Login service should implement
 */
public interface LoginService {

    /**
     * Performs a user login with email and password. The current user is
     * required to not be already set.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return a completable future containing the User if the request is successful
     * @throws IllegalArgumentException if one the arguments is null
     * @throws IllegalStateException    if the current user is already set (logged in)
     */
    CompletableFuture<User> loginWithEmail(String email, String password);

    /**
     * Retrieves the current user if there is one, null otherwise.
     *
     * @return the current user if logged-in, null otherwise
     */
    @Nullable
    User getCurrentUser();

    /**
     * Resets the password associated to an email if said email is linked with an account.
     * We do not indicate if the email was found or not to prevent malicious users to know if
     * someone is registered or not
     * The current user is not required to be set.
     *
     * @param email the user's email
     * @return an empty completable future
     * @throws IllegalArgumentException if one of the arguments is null
     */
    CompletableFuture<Void> resetPasswordWithEmail(String email);

    /**
     * Creates a user with an email and a password
     * If the account creation succeed, the newly created
     * user is signed in. The current user must not already
     * be set.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return a completable future containing the User if the request is successful
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalStateException    if the current user is set
     */
    CompletableFuture<User> createUser(String email, String password);

    /**
     * Updates the email address of a user. The user is required to be set.
     *
     * @param email the new email to set
     * @return an empty completable future
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<Void> updateEmailAddress(String email);

    /**
     * Updates the password of a user. The current user is required to be set.
     *
     * @param password the new password to set
     * @return an empty completable future
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<Void> updatePassword(String password);

    /**
     * Verifies an user's email. The current user is required to be set.
     *
     * @return an empty completable future
     * @throws IllegalStateException if no current user is set
     */
    CompletableFuture<Void> sendEmailVerification();

    /**
     * Deletes a user. The current user is required to be set.
     *
     * @return an empty completable future
     * @throws IllegalStateException if no current user is set
     */
    CompletableFuture<Void> deleteUser();

    /**
     * Re-authenticates a user. This is needed for things like password change or critical
     * operations
     * The current user is required to be set.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return an empty completable future
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalStateException    if no current user is set
     */
    CompletableFuture<Void> reAuthenticateUser(String email, String password);

    /**
     * Signs out the currently logged in user if there is one.
     *
     */
    void signOut();

    /**
     * Allow an anonymous login, which might be useful in some cases
     *
     * @return an empty completable future which will be complete when anonymously logged in.
     */
    CompletableFuture<User> signInAnonymously();


}
