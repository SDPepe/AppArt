package ch.epfl.sdp.appart.login;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
    public void updateEmailAddress(User user, OnCompleteListener<Void> callback) {
        if (user == null || callback == null) throw new IllegalArgumentException();
        //We would need something like user.getLoginServiceUser
        //which would return the appropriate user type for the LoginService through which the user was logged
        //I have currently no idea on how to implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public void updatePassword(User user, OnCompleteListener<Void> callback) {
        if (user == null || callback == null) throw new IllegalArgumentException();
        //We would need something like user.getLoginServiceUser
        //which would return the appropriate user type for the LoginService through which the user was logged
        //I have currently no idea on how to implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendEmailVerification(User user, OnCompleteListener<Void> callback) {
        if (user == null || callback == null) throw new IllegalArgumentException();
        //We would need something like user.getLoginServiceUser
        //which would return the appropriate user type for the LoginService through which the user was logged
        //I have currently no idea on how to implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteUser(User user, OnCompleteListener<Void> callback) {
        if (user == null || callback == null) throw new IllegalArgumentException();
        //We would need something like user.getLoginServiceUser
        //which would return the appropriate user type for the LoginService through which the user was logged
        //I have currently no idea on how to implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public void reAuthenticateUser(User user, OnCompleteListener<Void> callback) {
        if (user == null || callback == null) throw new IllegalArgumentException();
        //We would need something like user.getLoginServiceUser
        //which would return the appropriate user type for the LoginService through which the user was logged
        //I have currently no idea on how to implement this
        throw new UnsupportedOperationException();
    }
}
