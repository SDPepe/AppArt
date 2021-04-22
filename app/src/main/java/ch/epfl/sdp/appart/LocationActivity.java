package ch.epfl.sdp.appart;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.LocationService;
import ch.epfl.sdp.appart.utils.PermissionRequest;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * This activity only serves to test the actual implementation of the Android
 * location service.
 * For now, we do not have any activity that actually uses the location.
 * Once the activity with the map is created this will probably disappear.
 */

@AndroidEntryPoint
public class LocationActivity extends AppCompatActivity {

    @Inject
    LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        TextView longitudeTextView =
                findViewById(R.id.longitude_location_textview);
        TextView latitudeTextView =
                findViewById(R.id.latitude_location_textview);
        TextView permissionTextView =
                findViewById(R.id.permission_location_textview);
        TextView callbackTextView =
                findViewById(R.id.callback_location_textview);

        PermissionRequest.askForLocationPermission(this, () -> permissionTextView.setText(R.string.permissionGranted), () -> permissionTextView.setText(R.string.permissionRefused), () -> permissionTextView.setText(R.string.educationalPopup));
    }
}