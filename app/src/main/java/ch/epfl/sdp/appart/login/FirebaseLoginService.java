package ch.epfl.sdp.appart.login;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
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
     * Builds a FirebaseLoginService from context
     *
     * @return a FirebaseLoginService
     */
    public static LoginService buildLoginService() {
        return new FirebaseLoginService();
    }

    @Override
    public void loginWithEmail(String email, String password, OnCompleteListener<AuthResult> callback) {
        if (email == null || password == null || callback == null)
            throw new IllegalArgumentException();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(callback);
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
                .addOnCompleteListener(callback);
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
