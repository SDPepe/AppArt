package ch.epfl.sdp.appart.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.login.LoginService;
import dagger.hilt.android.AndroidEntryPoint;

@SuppressWarnings("JavaDoc")
@AndroidEntryPoint
public class ResetActivity extends AppCompatActivity {

    @Inject
    LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
    }

    /**
     * Method called when the reset password is pushed
     * Makes the text appear and sends a mail to the user, if the given address exists in the FireBase
     *
     * @param view
     */
    public void resetPassword(View view) {
        EditText emailView = findViewById(R.id.reset_email);
        String email = emailView.getText().toString();

        CompletableFuture<Void> resetFuture = this.loginService.resetPasswordWithEmail(email);
        resetFuture.exceptionally( e -> {
            Log.d("LOGIN", e.getMessage());
            Snackbar.make(view, R.string.invalid_email_snack, BaseTransientBottomBar.LENGTH_SHORT).show();
            return null;
        });
        resetFuture.thenAccept(arg -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Method called when the Log In button is pushed
     * Takes the user back to the login screen
     *
     * @param view
     */
    public void backToLogin(@SuppressWarnings("unused") View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}