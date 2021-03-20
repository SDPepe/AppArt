package ch.epfl.sdp.appart.login;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

@Singleton
public class FirebaseLoginService implements LoginService {
    private final FirebaseAuth mAuth;

    @Inject
    public FirebaseLoginService() {
        this.mAuth = FirebaseAuth.getInstance();
        //mAuth.useEmulator("10.0.2.2", 9099);
    }

    private CompletableFuture<User> handleEmailAndPasswordMethod(String email, String password, Task<AuthResult> task) {
        if (email == null || password == null) throw new IllegalArgumentException();
        //Handle loss of network with https://firebase.google.com/docs/database/android/offline-capabilities#section-connection-state
        return setUpFuture(task,
                this::getUserFromAuthResult);
    }

    /**
     * Builds a FirebaseLoginService
     *
     * @return a FirebaseLoginService
     */
    public static LoginService buildLoginService() {
        return new FirebaseLoginService();
    }

    @Override
    public CompletableFuture<User> loginWithEmail(String email, String password) {
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
        if (email == null) throw new IllegalArgumentException();
        return setUpFuture(mAuth.sendPasswordResetEmail(email),
                result -> result);
    }

    @Override
    public CompletableFuture<User> createUser(String email, String password) {
        return handleEmailAndPasswordMethod(email, password,
                mAuth.createUserWithEmailAndPassword(email, password));
    }

    @Override
    public CompletableFuture<Void> updateEmailAddress(String email) {
        if (email == null) throw new IllegalArgumentException();
        return setUpFuture(getCurrentFirebaseUser().updateEmail(email)
                , result -> result);
    }

    @Override
    public CompletableFuture<Void> updatePassword(String password) {
        if (password == null) throw new IllegalArgumentException();
        return setUpFuture(getCurrentFirebaseUser().updatePassword(password),
                result -> result);
    }

    @Override
    public CompletableFuture<Void> sendEmailVerification() {
        return setUpFuture(getCurrentFirebaseUser().sendEmailVerification(),
                result -> result);
    }

    @Override
    public CompletableFuture<Void> deleteUser() {
        return setUpFuture(getCurrentFirebaseUser().delete(),
                result -> result);
    }

    @Override
    public CompletableFuture<Void> reAuthenticateUser(String email, String password) {
        if (email == null || password == null) throw new IllegalArgumentException();
        return setUpFuture(getCurrentFirebaseUser().reauthenticate(
                EmailAuthProvider.getCredential(email, password)),
                result -> result);
    }

    @Override
    public void useEmulator(String ip, int port) {
        if(ip == null) throw new IllegalArgumentException();
        mAuth.useEmulator(ip, port);
    }

    /**
     * @return the current Firebase user
     * @throws IllegalArgumentException if no user is set
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
        FirebaseUser user = getCurrentFirebaseUser();
        return new AppUser(user.getUid(), user.getEmail());
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

    /**
     * Completes the future depending on the task result
     *
     * @param task the task whose result will go into the returned future if successful
     * @param func the function used to convert the task from one type to another
     * @return a future that contains the task result
     */
    private <FROM, TO> CompletableFuture<TO> setUpFuture(Task<FROM> task, ConvertTask<FROM, TO> func) {
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
}
