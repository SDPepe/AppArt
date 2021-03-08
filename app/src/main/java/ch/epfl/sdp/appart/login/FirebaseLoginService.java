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

    @Override
    public void loginWithEmail(String email, String password, OnCompleteListener<AuthResult> callback) {
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
        this.mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(callback);
    }

    @Override
    public void createUser(String email, String password, OnCompleteListener<AuthResult> callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this.executor, callback);
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
        return new FirebaseLoginService(buildExecutorFromContext(context));
    }
}
