package ch.epfl.sdp.appart.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.ScrollingActivity;
import ch.epfl.sdp.appart.utils.UIUtils;
import dagger.hilt.android.AndroidEntryPoint;

@SuppressWarnings("JavaDoc")
@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    //@LoginModule.CloudLoginService
    @Inject
    LoginService loginService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * Method called when the login button is pushed
     * For now, just takes the user to the scrolling activity page
     *
     * @param view
     */
    public void logIn(View view) {
        EditText emailView = findViewById(R.id.email_login);
        EditText passwordView = findViewById(R.id.password);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();


        CompletableFuture<User> loginFuture = loginService.loginWithEmail(email, password);
        loginFuture.exceptionally(e -> {
            UIUtils.makeSnakeAndLogOnFail(view, R.string.login_failed_snack, e);
            return null;
        });
        loginFuture.thenAccept(user -> {
            Intent intent = new Intent(this, ScrollingActivity.class);
            startActivity(intent);
        });

    }

    public void createAccount(@SuppressWarnings("unused") View view) {
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }

    /**
     * Method called when the forgotten password button is pushed
     * Takes the user to the reset password page, where he can put his address mail and change his password
     *
     * @param view
     */
    public void resetPassword(@SuppressWarnings("unused") View view) {
        Intent intent = new Intent(this, ResetActivity.class);
        startActivity(intent);
    }
}