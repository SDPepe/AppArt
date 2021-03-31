package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.UIUtils;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * This class manages the UI for create user.
 */
@AndroidEntryPoint
public class CreateUserActivity extends AppCompatActivity {

    //@LoginModule.CloudLoginService
    @Inject
    public LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
    }

    /**
     * Method called when you want create an account.
     *
     * @param view
     */
    public void createAccount(View view) {
        EditText emailView = (EditText) findViewById(R.id.create_account_email_CreateUser_EditText);
        EditText passwordView = (EditText) findViewById(R.id.create_account_password_CreateUser_EditText);

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
     *
     * @param view
     */
    public void backToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}