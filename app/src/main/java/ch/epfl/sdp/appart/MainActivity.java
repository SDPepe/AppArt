package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.user.LoginActivity;
import ch.epfl.sdp.appart.user.UserProfileActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);

    }

}