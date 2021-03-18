package ch.epfl.sdp.appart.user;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.sdp.appart.MainActivity;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.ScrollingActivity;
import ch.epfl.sdp.appart.utils.UIUtils;
import dagger.hilt.android.AndroidEntryPoint;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

@AndroidEntryPoint
public class CreateUserActivity extends AppCompatActivity {

    @Inject
    public LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
    }

    public void createAccount(View view) {
        EditText emailView = (EditText) findViewById(R.id.create_account_email);
        EditText passwordView = (EditText) findViewById(R.id.create_account_password);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        CompletableFuture<User> futureUser = loginService.createUser(email, password);
        futureUser.exceptionally(e -> {
            UIUtils.makeSnakeAndLogOnFail(view, R.string.create_account_failed_snack, e);
            return null;
        });
        futureUser.thenAccept(user -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
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