package ch.epfl.sdp.appart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabase;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.database.preferences.SharedPreferencesHelper;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoader;
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
            CompletableFuture<User> loginResult = loginService.loginWithEmail(email, password);
            loginResult.exceptionally(e -> {
                startScrollingActivity();
                return null;
            });
            loginResult.thenAccept(user -> {
                saveLoggedUser(user);
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                startScrollingActivity();
            });
        } else {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Bundle extras = this.getIntent().getExtras();
            if (extras != null && extras.containsKey(ActivityCommunicationLayout.PROVIDING_EMAIL) &&
                    extras.containsKey(ActivityCommunicationLayout.PROVIDING_PASSWORD)) {
                ((EditText) findViewById(R.id.email_Login_editText))
                        .setText(extras.getString(ActivityCommunicationLayout.PROVIDING_EMAIL));
                ((EditText) findViewById(R.id.password_Login_editText))
                        .setText(extras.getString(ActivityCommunicationLayout.PROVIDING_PASSWORD));
            }
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
            CompletableFuture<Void> saveRes = saveLoggedUser(user);
            saveRes.exceptionally(e -> {
                hideProgressBar();
                Toast.makeText(this, R.string.saveUserFail_Login,
                        Toast.LENGTH_SHORT).show();
                return null;
            });
            saveRes.thenAccept(res -> {
                startScrollingActivity();
            });
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

    private CompletableFuture<Void> saveLoggedUser(User user) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        CompletableFuture<Bitmap> pfpRes = new CompletableFuture<>();
        CompletableFuture<User> userRes = database.getUser(user.getUserId());
        userRes.exceptionally(e -> {
            result.completeExceptionally(e);
            return null;
        });
        userRes.thenAccept(u -> {
            database.accept(new GlideBitmapLoader(
                    this, pfpRes, u.getProfileImagePathAndName()));
            pfpRes.exceptionally(e -> {
                        result.completeExceptionally(e);
                        return null;
                    });
            pfpRes.thenAccept(bitmap -> {
                CompletableFuture<Void> setUserRes = localdb.setCurrentUser(u, bitmap);
                setUserRes.exceptionally(e -> {
                    result.completeExceptionally(e);
                    return null;
                });
                setUserRes.thenAccept(res -> {
                    result.complete(null);
                });
            });
        });
        return result;
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
}
