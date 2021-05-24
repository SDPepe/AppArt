package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;

import javax.inject.Inject;

import ch.epfl.sdp.appart.configuration.ApplicationConfiguration;
import dagger.hilt.android.AndroidEntryPoint;

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
        Intent intent = new Intent(this, PlaceActivity.class);
        if(extras != null && extras.containsKey(ActivityCommunicationLayout.PROVIDING_EMAIL)  && extras.containsKey(ActivityCommunicationLayout.PROVIDING_PASSWORD)){
            intent.putExtra(ActivityCommunicationLayout.PROVIDING_EMAIL, extras.getString(ActivityCommunicationLayout.PROVIDING_EMAIL));
            intent.putExtra(ActivityCommunicationLayout.PROVIDING_PASSWORD, extras.getString(ActivityCommunicationLayout.PROVIDING_PASSWORD));
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
