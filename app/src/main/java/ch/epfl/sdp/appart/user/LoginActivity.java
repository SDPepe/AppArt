package ch.epfl.sdp.appart.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.scrolling.ScrollingActivity;

public class LoginActivity extends AppCompatActivity {

    public static User sessionUser;

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
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);

        EditText emailView = (EditText) findViewById(R.id.email_login);
        String email = emailView.getText().toString();
        this.sessionUser = new AppUser("1", email);

        /*
        EditText passwordView = (EditText) findViewById(R.id.password);
        String password = passwordView.getText().toString();
        //Need to implement call to login service */

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