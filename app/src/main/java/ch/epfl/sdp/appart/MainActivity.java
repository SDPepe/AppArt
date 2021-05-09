package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import ch.epfl.sdp.appart.configuration.ApplicationConfiguration;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * To run tests :
 * firebase emulators:start
 * ./gradlew check connectedCheck --stacktrace
 * To insert in configuration :
 *  -e "email" "youremail" -e "password" "yourpsw" --ez "demo_mode" true
 */

/**
 * The main UI class.
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    //private static boolean DEMO_MODE = false;

    @Inject
    public ApplicationConfiguration configuration;

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
            configuration.setDemoMode(this, extras.getBoolean("demo_mode"));
        }
        startActivity(intent);
    }

    //public static boolean isDemoMode() {
    //    return DEMO_MODE;
    //}
}
