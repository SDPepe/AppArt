package ch.epfl.sdp.appart.login;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    public boolean loginWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this.executor, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Success

                        } else {
                            //Failure
                        }
                    }
                });
        //TODO: Change this
        return false;
    }

    @Override
    public boolean loginWithUsername(String username, String password) {
        return false;
    }

    @Override
    public User getCurrentUser() {
        return null;
    }

    @Override
    public void resetPasswordWithEmail(String email) {

    }

    @Override
    public User createUser(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this.executor, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Do something on success
                        } else {
                            //Do something on failed
                        }

                        // ...
                    }
                });
        return null;
    }

    /**
     * Gives an executor from the context to the FirebaseLoginService
     *
     * @param context
     * @return an executor
     */
    private static Executor buildExecutorFromContext(Context context) {
        return ContextCompat.getMainExecutor(context);
    }

    /**
     * Builds a FirebaseLoginService from context
     * @param context
     * @return a FirebaseLginService
     */
    public static LoginService buildfromContext(Context context) {
        return new FirebaseLoginService(buildExecutorFromContext(context));
    }
}
