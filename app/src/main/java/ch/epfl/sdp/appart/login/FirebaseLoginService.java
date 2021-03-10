package ch.epfl.sdp.appart.login;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.sdp.appart.user.User;

public class FirebaseLoginService implements LoginService {
    private final FirebaseAuth mAuth;

    FirebaseLoginService() {
        this.mAuth = FirebaseAuth.getInstance();
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
    public void loginWithEmail(String email, String password, LoginCallback callback) {
        if (email == null || password == null || callback == null)
            throw new IllegalArgumentException();
        addCallbackToTask(mAuth.signInWithEmailAndPassword(email, password), callback);
    }

    @Override
    public User getCurrentUser() {
        //Waiting for Adapter class
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetPasswordWithEmail(String email, LoginCallback callback) {
        if (email == null || callback == null) throw new IllegalArgumentException();
        addCallbackToTask(mAuth.sendPasswordResetEmail(email), callback);
    }

    @Override
    public void createUser(String email, String password, LoginCallback callback) {
        if (email == null || password == null || callback == null)
            throw new IllegalArgumentException();
        addCallbackToTask(mAuth.createUserWithEmailAndPassword(email, password), callback);
    }

    @Override
    public void updateEmailAddress(String email, LoginCallback callback) {
        if (callback == null) throw new IllegalArgumentException();
        FirebaseUser user = getCurrentFirebaseUser();
        addCallbackToTask(user.updateEmail(email), callback);
    }

    @Override
    public void updatePassword(String password, LoginCallback callback) {
        if (callback == null) throw new IllegalArgumentException();
        FirebaseUser user = getCurrentFirebaseUser();
        addCallbackToTask(user.updatePassword(password), callback);
    }

    @Override
    public void sendEmailVerification(LoginCallback callback) {
        if (callback == null) throw new IllegalArgumentException();
        FirebaseUser user = getCurrentFirebaseUser();
        addCallbackToTask(user.sendEmailVerification(), callback);
    }

    @Override
    public void deleteUser(LoginCallback callback) {
        if (callback == null) throw new IllegalArgumentException();
        FirebaseUser user = getCurrentFirebaseUser();
        addCallbackToTask(user.delete(), callback);
    }

    @Override
    public void reAuthenticateUser(String email, String password, LoginCallback callback) {
        if (callback == null) throw new IllegalArgumentException();
        FirebaseUser user = getCurrentFirebaseUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        addCallbackToTask(user.reauthenticate(credential), callback);
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


    private <T> void addCallbackToTask(Task<T> task, LoginCallback callback) {
        task.addOnCompleteListener(new LoginOnCompleteListener<>(callback));
    }

    private static class LoginOnCompleteListener<T> implements OnCompleteListener<T> {
        private final LoginCallback callback;

        LoginOnCompleteListener(LoginCallback callback) {
            this.callback = callback;
        }


        @Override
        public void onComplete(@NonNull Task<T> task) {
            callback.onRequestCompletion(task.isSuccessful());
        }
    }
}
