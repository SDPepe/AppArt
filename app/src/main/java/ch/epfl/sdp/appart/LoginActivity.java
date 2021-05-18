package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.local.LocalDatabase;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
import ch.epfl.sdp.appart.utils.UIUtils;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * This class manages the UI for the login.
 */
@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    @Inject
    LoginService loginService;
    @Inject
    LocalDatabase localdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*
         * If a currentUser is stored locally, auto-login with it and then try to fetch
         * it from the server. If the fetch is successful, update local currentUser and
         * start the activity. Otherwise, act as the app is offline and start the activity
         * with the local currentUser.
         * If no currentUser is stored locally, ask for login credentials as we did before.
         */
        User user = localdb.getCurrentUser();

        if (user != null){
            startScrollingActivity();
        } else {
            Bundle extras = this.getIntent().getExtras();
            if(extras != null && extras.containsKey(ActivityCommunicationLayout.PROVIDING_EMAIL)  && extras.containsKey(ActivityCommunicationLayout.PROVIDING_PASSWORD)){
                ((EditText)findViewById(R.id.email_Login_editText)).setText(extras.getString(ActivityCommunicationLayout.PROVIDING_EMAIL));
                ((EditText)findViewById(R.id.password_Login_editText)).setText(extras.getString(ActivityCommunicationLayout.PROVIDING_PASSWORD));
            }
        }
    }

    /**
     * Method called when the login button is pushed
     * Given the email and the password in the corresponding views, login with firebase, or show a popup if it failed to connect
     *
     * @param view
     */
    public void logIn(View view) {
        EditText emailView = findViewById(R.id.email_Login_editText);
        EditText passwordView = findViewById(R.id.password_Login_editText);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        ProgressBar progressBar = findViewById(R.id.progress_Login_ProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        CompletableFuture<User> loginFuture = loginService.loginWithEmail(email, password);
        loginFuture.exceptionally(e -> {
            UIUtils.makeSnakeAndLogOnFail(view, R.string.login_failed_snack, e);
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


            return null;
        });
        loginFuture.thenAccept(user ->  {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            startScrollingActivity();
        });

    }

    /**
     * Method called when Create an account button is pushed.
     * Simply takes the user to the create user activity.
     *
     * @param view
     */
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

    private void startScrollingActivity(){
        Intent intent = new Intent(this, ScrollingActivity.class);
        startActivity(intent);
    }
}
