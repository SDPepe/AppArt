package ch.epfl.sdp.appart.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.ScrollingActivity;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    @Inject
    LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * Method called when the login button is pushed
     * For now, juste takes the user to the scrolling activity page
     *
     * @param view
     */
    public void logIn(View view) {
        EditText emailView = (EditText) findViewById(R.id.email_login);
        EditText passwordView = (EditText) findViewById(R.id.password);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        loginService.loginWithEmail(email, password, hasSucceeded -> {
            if(hasSucceeded) {
                Intent intent = new Intent(this, ScrollingActivity.class);
                startActivity(intent);
            }
            else {
                //Popup login failed
                Log.d("LOGIN", "Login failed !");
            }
        });

    }

    public void createAccount(View view) {
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }

    /**
     * Method called when the forgotten password button is pushed
     * Takes the user to the reset password page, where he can put his address mail and change his password
     *
     * @param view
     */
    public void resetPassword(View view) {
        Intent intent = new Intent(this, ResetActivity.class);
        startActivity(intent);
    }
}