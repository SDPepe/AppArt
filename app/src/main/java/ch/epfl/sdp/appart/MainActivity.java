package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The main UI class.
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = this.getIntent().getExtras();
        Intent intent = new Intent(this, LoginActivity.class);
        if(extras != null && extras.containsKey("email")  && extras.containsKey("password")){
            intent.putExtra("email", extras.getString("email"));
            intent.putExtra("password", extras.getString("password"));
        }
        startActivity(intent);
    }
}
