package ch.epfl.sdp.appart;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import ch.epfl.sdp.appart.location.LocationService;
import ch.epfl.sdp.appart.utils.PermissionRequest;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * The main UI class.
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    LocationService locationService;

    ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                if (isGranted.get(Manifest.permission.ACCESS_COARSE_LOCATION) && isGranted.get(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Continue app workfwlo
                } else {
                    //Tell the user the feature can't be used
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionRequest.askForLocationPermission(this, () -> {
            Log.d("PERMISSION_DEBUG_INFO", "Permission granted");
        }, () -> {
            Log.d("PERMISSION_DEBUG_INFO", "Permission refused");
        }, () -> {
            Log.d("PERMISSION_DEBUG_INFO", "Educational popup");
        });


        /*Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);*/

    }
}