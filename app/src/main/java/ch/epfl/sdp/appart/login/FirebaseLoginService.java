package ch.epfl.sdp.appart.login;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.login.exceptions.LoginServiceRequestFailedException;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;


@Singleton
public class FirebaseLoginService implements LoginService {
    private final FirebaseAuth mAuth;

    @Inject
    public FirebaseLoginService() {
        this.mAuth = FirebaseAuth.getInstance();
        this.mAuth.signOut(); //to unsure that no invalid user is cached
        System.out.println("User signed out");
    }

    /**
     * Builds a FirebaseLoginService
     *
     * @return a FirebaseLoginService
     */
    public static LoginService buildLoginService() {
        return new FirebaseLoginService();
    }

    private CompletableFuture<User> handleEmailAndPasswordMethod(String email, String password,
                                                                 Task<AuthResult> task) {
        argsChecker(email, password);
        //Handle loss of network with https://firebase.google
        // .com/docs/database/android/offline-capabilities#section-connection-state
        return setUpFuture(task,
                this::getUserFromAuthResult);
    }

    @Override
    public CompletableFuture<User> loginWithEmail(String email, String password) {
        userChecker(true, "when authenticating");
        try {
            argsChecker(email, password);
        } catch(Exception e) {
            CompletableFuture<User> failedLogin = new CompletableFuture<>();
            failedLogin.completeExceptionally(new LoginServiceRequestFailedException(e.getMessage()));
            return failedLogin;
        }
        return handleEmailAndPasswordMethod(email, password,
                mAuth.signInWithEmailAndPassword(email, password));
    }

    @Override
    public User getCurrentUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return null;
        }
        String userId = user.getUid();
        String email = user.getEmail();

        return new AppUser(userId, email);
    }

    @Override
    public CompletableFuture<Void> resetPasswordWithEmail(String email) {
        try {
            argsChecker(email);
        } catch (Exception e) {
            CompletableFuture<Void> failedLogin = new CompletableFuture<>();
            failedLogin.completeExceptionally(new LoginServiceRequestFailedException(e.getMessage()));
            return failedLogin;
        }
        return setUpFuture(mAuth.sendPasswordResetEmail(email),
                result -> result);
    }

    @Override
    public CompletableFuture<User> createUser(String email, String password) {
        userChecker(true, "when creating new user");
        try {
            argsChecker(email, password);
        } catch (Exception e) {
            CompletableFuture<User> failedLogin = new CompletableFuture<>();
            failedLogin.completeExceptionally(new LoginServiceRequestFailedException(e.getMessage()));
            return failedLogin;
        }
        return handleEmailAndPasswordMethod(email, password,
                mAuth.createUserWithEmailAndPassword(email, password));
    }

    @Override
    public CompletableFuture<Void> updateEmailAddress(String email) {
        try {
            fullChecker(email, "when updating the email");
        } catch (Exception e) {
            CompletableFuture<Void> failedLogin = new CompletableFuture<>();
            failedLogin.completeExceptionally(new LoginServiceRequestFailedException(e.getMessage()));
            return failedLogin;
        }


        return setUpFuture(getCurrentFirebaseUser().updateEmail(email), result -> result);
    }

    @Override
    public CompletableFuture<Void> updatePassword(String password) {
        String[] args = {password};
        try {
            fullChecker(password, "when updating the password");
        } catch (Exception e) {
            CompletableFuture<Void> failedLogin = new CompletableFuture<>();
            failedLogin.completeExceptionally(new LoginServiceRequestFailedException(e.getMessage()));
            return failedLogin;
        }



        return setUpFuture(getCurrentFirebaseUser().updatePassword(password), result -> result);
    }

    @Override
    public CompletableFuture<Void> sendEmailVerification() {
        try {
            userChecker(false, "when sending verification email");
        } catch (Exception e) {
            CompletableFuture<Void> failedLogin = new CompletableFuture<>();
            failedLogin.completeExceptionally(new LoginServiceRequestFailedException(e.getMessage()));
            return failedLogin;
        }
        return setUpFuture(getCurrentFirebaseUser().sendEmailVerification(),
                result -> result);
    }

    @Override
    public CompletableFuture<Void> deleteUser() {
        try {
            userChecker(false, "when deleting it");
        } catch (Exception e) {
            CompletableFuture<Void> failedLogin = new CompletableFuture<>();
            failedLogin.completeExceptionally(new LoginServiceRequestFailedException(e.getMessage()));
            return failedLogin;
        }

        return setUpFuture(getCurrentFirebaseUser().delete(),
                result -> result);
    }

    @Override
    public CompletableFuture<Void> reAuthenticateUser(String email, String password) {
        try {
            argsChecker(email, password);
            userChecker(false, "when reAuthentication");
        } catch (Exception e) {
            CompletableFuture<Void> failedLogin = new CompletableFuture<>();
            failedLogin.completeExceptionally(new LoginServiceRequestFailedException(e.getMessage()));
            return failedLogin;
        }



        return setUpFuture(getCurrentFirebaseUser().reauthenticate(
                EmailAuthProvider.getCredential(email, password)),
                result -> result);
    }

    @Override
    public void signOut() {
        mAuth.signOut();
        System.out.println("User signed out");
    }

    @Override
    public CompletableFuture<User> signInAnonymously() {
        return setUpFuture(mAuth.signInAnonymously(),
                authResult -> new AppUser("anonymousID", "anonymous@testappart.ch"));
    }

    /**
     * Checks if the internal state of the current user in Firebase match the expected state.
     * getCurrentUser() must return null
     * or a valid user depending on hasToBeNull.
     *
     * @param hasToBeNull boolean that decides if we need to check for null or not
     * @param excMessage  string that contains the exception message the caller wants to display
     * @throws IllegalStateException if the state of getCurrentUser() is not the one expected
     */
    private void userChecker(boolean hasToBeNull, String excMessage) {
        if (getCurrentUser() == null ^ hasToBeNull) {
            if (hasToBeNull) {
                throw new IllegalStateException("Current user must not already be set : " + excMessage);
            } else {
                throw new IllegalStateException("Current user must be set : " + excMessage);
            }
        }
    }

    /**
     * Verifies that all strings in the args array are not null. These strings usually correspond
     * to the list, or part of the list, of argument of a method.
     *
     * @param args the array containing the strings.
     * @throws IllegalArgumentException if one of the strings in args is null
     */
    private void argsChecker(String... args) {
        for (String s : args) {
            if (s == null || s.isEmpty()) {
                throw new IllegalArgumentException("String argument cannot be null");
            }
        }
    }

    /**
     * The fulChecker uses argsChecker and userChecker to verify that the arguments of a method
     * are valid, and that the sate of getCurrentUser is valid for this method.
     *
     * @param args       the list of strings we want to check
     * @param excMessage a string that contains the exception message the caller wants to display
     * @throws IllegalArgumentException if one of the arguments in args is null
     * @throws IllegalStateException    if the state of getCurrentUser is not valid for this method
     */
    private void fullChecker(String excMessage, String... args) {
        argsChecker(args);
        userChecker(false, excMessage);
    }

    /**
     * Sets up the use of an emulator for the Firebase authentication service.
     *
     * @param ip   the ip of the emulator
     * @param port the port that corresponds to the authentication service emulation
     */
    public void useEmulator(String ip, int port) {
        if (ip == null) throw new IllegalArgumentException();
        mAuth.useEmulator(ip, port);
    }

    /**
     * @return the current Firebase user
     * @throws IllegalStateException if no user is set
     */
    private FirebaseUser getCurrentFirebaseUser() {
        FirebaseUser user = this.mAuth.getCurrentUser();
        if (user == null) throw new IllegalStateException("No current user set !");
        return user;
    }

    /**
     * Converts an AuthResult to a User
     *
     * @param result the AuthResult coming from Firebase
     * @return a user
     */
    private User getUserFromAuthResult(AuthResult result) {
        FirebaseUser user = result.getUser();
        //No issue with user being null because this is only executed when the login future
        // succeeded
        return new AppUser(user.getUid(), user.getEmail());
    }

    /**
     * Completes the future depending on the task result
     *
     * @param task the task whose result will go into the returned future if successful
     * @param func the function used to convert the task from one type to another
     * @return a future that contains the task result
     */
    private <FROM, TO> CompletableFuture<TO> setUpFuture(Task<FROM> task,
                                                         ConvertTask<FROM, TO> func) {
        CompletableFuture<TO> future = new CompletableFuture<>();
        task.addOnCompleteListener(taskResult -> {
            if (taskResult.isSuccessful()) {
                future.complete(func.convertTask(taskResult.getResult()));
            } else {
                future.completeExceptionally(new LoginServiceRequestFailedException(taskResult.getException().getMessage()));
            }
        });
        return future;
    }

    /**
     * Interface that handles the conversion of a task from one type to another
     *
     * @param <FROM> the type we want to convert
     * @param <TO>   the type we want to get
     */
    private interface ConvertTask<FROM, TO> {
        TO convertTask(FROM arg);
    }
}
