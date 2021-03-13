package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.sdp.appart.hilt.HiltTestActivity;
import ch.epfl.sdp.appart.scrolling.ScrollingActivity;
import ch.epfl.sdp.appart.user.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, HiltTestActivity.class);
        startActivity(intent);

    }

}