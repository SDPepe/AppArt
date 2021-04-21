package ch.epfl.sdp.appart;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import ch.epfl.sdp.appart.location.Location;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionRequest.askForLocationPermission(this, () -> {
            Log.d("PERMISSION_DEBUG_INFO", "Permission granted");
            //locationService.setupLocationUpdate();
        }, () -> {
            Log.d("PERMISSION_DEBUG_INFO", "Permission refused");
        }, () -> {
            Log.d("PERMISSION_DEBUG_INFO", "Educational popup");
        });
        FusedLocationProviderClient test = LocationServices.getFusedLocationProviderClient(this);
        try {
            test.getLastLocation().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Location location = new Location();
                    location.longitude = task.getResult().getLongitude();
                    location.latitude = task.getResult().getLatitude();
                    Log.d("LOCATION", "TEST");
                }
            });
        } catch(SecurityException e) {
            throw e;
        }

        try {
            test.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Location location = new Location();
                    location.longitude = task.getResult().getLongitude();
                    location.latitude = task.getResult().getLatitude();
                    Log.d("LOCATION", "TEST");
                }
            });
        } catch(SecurityException e) {
            throw e;
        }




        /*Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);*/

    }
}