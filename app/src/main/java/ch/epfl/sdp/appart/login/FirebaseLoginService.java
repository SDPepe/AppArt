package ch.epfl.sdp.appart.login;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
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
    FirebaseLoginService() {
        this.mAuth = FirebaseAuth.getInstance();
        mAuth.useEmulator("10.0.2.2", 9099);
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
        if(email == null || password == null) throw new IllegalArgumentException();
        return new FutureSetup<AuthResult, User>().setUpFuture(mAuth.signInWithEmailAndPassword(email, password),
                authResult -> getUserFromAuthResult(authResult));
    }

    @Override
    public User getCurrentUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return null;
        }
        String name = user.getDisplayName();
        String userId = user.getUid();
        String email = user.getEmail();
        String phoneNumber = user.getPhoneNumber();
        Uri photoUrl = user.getPhotoUrl();
        String profilePic = null;
        if (photoUrl != null) {
            profilePic = photoUrl.toString();
        }
        return new AppUser(userId, name, email, phoneNumber, profilePic);
    }

    @Override
    public CompletableFuture<Void> resetPasswordWithEmail(String email) {
        if (email == null) throw new IllegalArgumentException();
        return new FutureSetup<Void, Void>().setUpFuture(mAuth.sendPasswordResetEmail(email),
                result -> result);
    }

    @Override
    public CompletableFuture<User> createUser(String email, String password) {
        if(email == null || password == null) throw new IllegalArgumentException();
        return new FutureSetup<AuthResult, User>().setUpFuture(mAuth.createUserWithEmailAndPassword(email, password),
                result -> getUserFromAuthResult(result));
    }

    @Override
    public CompletableFuture<Void> updateEmailAddress(String email) {
        if (email == null) throw new IllegalArgumentException();
        return new FutureSetup<Void, Void>().setUpFuture(getCurrentFirebaseUser().updateEmail(email)
                , result -> result);
    }

    @Override
    public CompletableFuture<Void> updatePassword(String password) {
        if (password == null) throw new IllegalArgumentException();
        return new FutureSetup<Void, Void>().setUpFuture(getCurrentFirebaseUser().updatePassword(password),
                result -> result);
    }

    @Override
    public CompletableFuture<Void> sendEmailVerification() {
        return new FutureSetup<Void, Void>().setUpFuture(getCurrentFirebaseUser().sendEmailVerification(),
                result ->result);
    }

    @Override
    public CompletableFuture<Void> deleteUser() {
        return new FutureSetup<Void, Void>().setUpFuture(getCurrentFirebaseUser().delete(),
                result -> result);
    }

    @Override
    public CompletableFuture<Void> reAuthenticateUser(String email, String password) {
        if(email == null || password == null) throw new IllegalArgumentException();
        return new FutureSetup<Void, Void>().setUpFuture(getCurrentFirebaseUser().reauthenticate(
                EmailAuthProvider.getCredential(email, password)),
                result -> result);
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
        FirebaseUser user = result.getUser();
        Uri profilePicUrl = user.getPhotoUrl();
        String profilePicString = null;
        if (profilePicUrl != null) {
            profilePicString = profilePicUrl.toString();
        }
        return new AppUser(user.getUid(), null, user.getEmail(), user.getPhoneNumber(), profilePicString);
    }

    /**
     * Interface that handles the conversion of a task from one type to another
     *
     * @param <FROM> the type we want to convert
     * @param <TO> the type we want to get
     */
    private interface ConvertTask<FROM, TO> {
        TO convertTask(FROM arg);
    }

    /**
     * A private class that handles the creation of a future from a task
     */
    private class FutureSetup<FROM, TO> {
        /**
         *
         * @param task the task whose result will go into the returned future if successful
         * @param func the function used to convert the task from one type to another
         * @return a future that contains the task result
         */
        public CompletableFuture<TO> setUpFuture(Task<FROM> task, ConvertTask<FROM, TO> func) {
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
}
