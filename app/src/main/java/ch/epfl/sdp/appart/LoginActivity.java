package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.database.preferences.SharedPreferencesHelper;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
import ch.epfl.sdp.appart.utils.DatabaseSync;
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
    LocalDatabaseService localdb;
    @Inject
    DatabaseService database;

    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.progress_Login_ProgressBar);


        String email = SharedPreferencesHelper.getSavedEmail(this);
        if (!email.equals("")) {
            String password = SharedPreferencesHelper.getSavedPassword(this);
            CompletableFuture<User> loginRes = loginService.loginWithEmail(email, password);
            loginRes.exceptionally(e -> {
                Log.d("LOGIN", "Couldn't login to firebase");
                startScrollingActivity();
                return null;
            });
            loginRes.thenAccept(u -> {
                CompletableFuture<Void> saveRes = DatabaseSync.saveCurrentUserToLocalDB(this,
                        database, localdb, u.getUserId());
                saveRes.exceptionally(e -> {
                    Log.d("LOGIN", "Failed to save user locally");
                    startScrollingActivity();
                    return null;
                });
                saveRes.thenAccept(r -> startScrollingActivity());
            });
        } else {
            setBundleInfo(this.getIntent().getExtras());
        }
    }

    /**
     * Method called when the login button is pushed
     * Given the email and the password in the corresponding views, login with firebase, or show
     * a popup if it failed to connect
     *
     * @param view
     */
    public void logIn(View view) {
        EditText emailView = findViewById(R.id.email_Login_editText);
        EditText passwordView = findViewById(R.id.password_Login_editText);
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        showProgressBar();
        CompletableFuture<User> loginRes = loginService.loginWithEmail(email, password);
        loginRes.exceptionally(e -> {
            UIUtils.makeSnakeAndLogOnFail(view, R.string.login_failed_snack, e);
            hideProgressBar();
            return null;
        });
        loginRes.thenAccept(user -> {
            SharedPreferencesHelper.saveUserForAutoLogin(this, email, password);
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
     * Takes the user to the reset password page, where he can put his address mail and change
     * his password
     *
     * @param view
     */
    public void resetPassword(View view) {
        Intent intent = new Intent(this, ResetActivity.class);
        startActivity(intent);
    }

    private void startScrollingActivity() {
        Intent intent = new Intent(this, ScrollingActivity.class);
        startActivity(intent);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void setBundleInfo(Bundle extras) {
        if (extras != null && extras.containsKey(ActivityCommunicationLayout.PROVIDING_EMAIL) &&
                extras.containsKey(ActivityCommunicationLayout.PROVIDING_PASSWORD)) {
            ((EditText) findViewById(R.id.email_Login_editText))
                    .setText(extras.getString(ActivityCommunicationLayout.PROVIDING_EMAIL));
            ((EditText) findViewById(R.id.password_Login_editText))
                    .setText(extras.getString(ActivityCommunicationLayout.PROVIDING_PASSWORD));
        }
    }
}
