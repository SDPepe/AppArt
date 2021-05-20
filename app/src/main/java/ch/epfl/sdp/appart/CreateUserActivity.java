package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.UIUtils;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * This class manages the UI for creating a user.
 */
@AndroidEntryPoint
public class CreateUserActivity extends AppCompatActivity {

    @Inject
    public LoginService loginService;

    @Inject
    public DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
    }

    /**
     * Method called when the create an account button is pushed.
     * It takes the email and the password written in the corresponding fields, and tells the login service to create an account.
     * If it succeeds, it takes the user back to the login page.
     * If not, it shows a popup telling the user the app wasn't able to create the account
     *
     * @param view the view that was clicked (i.e., the account button)
     */
    public void createAccount(View view) {
        EditText emailView = findViewById(R.id.create_account_email_CreateUser_editText);
        EditText passwordView = findViewById(R.id.create_account_password_CreateUser_editText);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        CompletableFuture<User> futureUser = loginService.createUser(email, password);
        futureUser.exceptionally(e -> {
            UIUtils.makeSnakeAndLogOnFail(view, R.string.create_account_failed_snack, e);
            return null;
        });
        futureUser.thenAccept(user -> {
            databaseService.putUser(user);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}