package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The main UI class.
 */
public class MainActivity extends AppCompatActivity {

    private static boolean DEMO_MODE = false;

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
        if (extras != null && extras.containsKey("demo_mode")) {
            DEMO_MODE = extras.getBoolean("demo_mode");
        }
        startActivity(intent);
    }

    public static boolean isDemoMode() {
        return DEMO_MODE;
    }
}
