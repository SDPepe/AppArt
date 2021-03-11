package ch.epfl.sdp.appart.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import ch.epfl.sdp.appart.R;

public class ResetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
    }

    /**
     * Method called when the reset password is pushed
     * Makes the text appear and sends a mail to the user, if the given address exists in the FireBase
     * @param view
     */
    public void resetPassword(View view) {
        EditText emailView = (EditText) findViewById(R.id.reset_email);
        String email = emailView.getText().toString();
    }

    /**
     * Method called when the Log In button is pushed
     * Takes the user back to the login screen
     * @param view
     */
    public void backToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}