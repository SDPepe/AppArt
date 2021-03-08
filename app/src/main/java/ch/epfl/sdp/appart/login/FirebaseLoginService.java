package ch.epfl.sdp.appart.login;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

import ch.epfl.sdp.appart.user.User;

public class FirebaseLoginService implements LoginService {
    private final FirebaseAuth mAuth;
    private final Executor executor;

    FirebaseLoginService(Executor executor) {
        this.mAuth = FirebaseAuth.getInstance();
        this.executor = executor;
    }

    /**
     * Gives an executor from the context to the FirebaseLoginService
     *
     * @param context the context from which we get an executor
     * @return an executor
     */
    private static Executor buildExecutorFromContext(Context context) {
        return ContextCompat.getMainExecutor(context);
    }

    /**
     * Builds a FirebaseLoginService from context
     *
     * @param context the context needed to retrieve an executor
     * @return a FirebaseLoginService
     */
    public static LoginService buildFromContext(Context context) {
        if (context == null) throw new IllegalArgumentException();
        return new FirebaseLoginService(buildExecutorFromContext(context));
    }

    @Override
    public void loginWithEmail(String email, String password, OnCompleteListener<AuthResult> callback) {
        if (email == null || password == null || callback == null)
            throw new IllegalArgumentException();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this.executor, callback);
    }

    @Override
    public User getCurrentUser() {
        //Waiting for Adapter class
        return null;
    }

    @Override
    public void resetPasswordWithEmail(String email, OnCompleteListener<Void> callback) {
        if (email == null || callback == null) throw new IllegalArgumentException();
        this.mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(callback);
    }

    @Override
    public void createUser(String email, String password, OnCompleteListener<AuthResult> callback) {
        if (email == null || password == null || callback == null)
            throw new IllegalArgumentException();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this.executor, callback);
    }

    @Override
    public void updateEmailAddress(String email, OnCompleteListener<Void> callback) {
        if (callback == null) throw new IllegalArgumentException();
        FirebaseUser user = getCurrentFirebaseUser();
        user.updateEmail(email).addOnCompleteListener(callback);
    }

    @Override
    public void updatePassword(String password, OnCompleteListener<Void> callback) {
        if (callback == null) throw new IllegalArgumentException();
        FirebaseUser user = getCurrentFirebaseUser();
        user.updatePassword(password).addOnCompleteListener(callback);
    }

    @Override
    public void sendEmailVerification(OnCompleteListener<Void> callback) {
        if (callback == null) throw new IllegalArgumentException();
        FirebaseUser user = getCurrentFirebaseUser();
        user.sendEmailVerification().addOnCompleteListener(callback);
    }

    @Override
    public void deleteUser(OnCompleteListener<Void> callback) {
        if (callback == null) throw new IllegalArgumentException();
        FirebaseUser user = getCurrentFirebaseUser();
        user.delete().addOnCompleteListener(callback);
    }

    @Override
    public void reAuthenticateUser(String email, String password, OnCompleteListener<Void> callback) {
        if (callback == null) throw new IllegalArgumentException();
        FirebaseUser user = getCurrentFirebaseUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        user.reauthenticate(credential).addOnCompleteListener(callback);
    }

    /**
     * @return the current Firebase user, throws @{@link IllegalArgumentException} if none is set
     */
    private FirebaseUser getCurrentFirebaseUser() {
        FirebaseUser user = this.mAuth.getCurrentUser();
        if (user == null) throw new IllegalStateException("No current user set !");
        return user;
    }
}
