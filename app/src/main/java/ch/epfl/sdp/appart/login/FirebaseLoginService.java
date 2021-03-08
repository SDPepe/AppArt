package ch.epfl.sdp.appart.login;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ch.epfl.sdp.appart.user.User;

public class FirebaseLoginService implements LoginService {
    private final FirebaseAuth mAuth;

    FirebaseLoginService() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean loginWithEmail(String email, String password) {
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
        //Waiting for the user classes
        return null;
    }

    public static LoginService buildAuthenticationService() {
        return new FirebaseLoginService();
    }
}
