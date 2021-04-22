package ch.epfl.sdp.appart.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

public class PermissionRequest {


    public static void askForLocationPermission(Activity activity, Runnable permissionGranted, Runnable permissionRefused, Runnable educationalPopup) {

        ActivityResultLauncher<String[]> requestPermissionLauncher =
                ((ComponentActivity)activity).registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                    if (isGranted.get(Manifest.permission.ACCESS_COARSE_LOCATION) && isGranted.get(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //Continue app workfwlo
                        permissionGranted.run();
                    } else {
                        //Tell the user the feature can't be used
                        permissionRefused.run();
                    }
                });

        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            boolean shouldShowRationale =
                    activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (shouldShowRationale) {
                //Show educational popup
                educationalPopup.run();
            } else {
                //Ask permission
                requestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
            }
        }
        else {
            permissionGranted.run();
        }
    }
}
