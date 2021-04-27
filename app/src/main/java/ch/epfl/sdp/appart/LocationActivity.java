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
 * This activity only serves to test the permission request, specifically on cirrus ci.
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
                findViewById(R.id.longitude_Location_textView);
        TextView latitudeTextView =
                findViewById(R.id.latitude_Location_textView);
        TextView permissionTextView =
                findViewById(R.id.permission_Location_textView);
        TextView callbackTextView =
                findViewById(R.id.callback_Location_textView);

        PermissionRequest.askForLocationPermission(this,
                () -> permissionTextView.setText(R.string.permissionGranted),
                () -> permissionTextView.setText(R.string.permissionRefused),
                () -> permissionTextView.setText(R.string.educationalPopup));

        locationService.getCurrentLocation().thenAccept(location -> {
            longitudeTextView.setText("" + location.longitude);
            latitudeTextView.setText("" + location.latitude);
        });

        locationService.setupLocationUpdate(locations -> {
            if(locations != null) {
                callbackTextView.setText(R.string.setCallbackTextView);
            }
        });
    }
}