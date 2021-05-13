package ch.epfl.sdp.appart.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

public class PermissionRequest {

    public static void askForLocationPermission(Activity activity, Runnable permissionGranted, Runnable permissionRefused, Runnable educationalPopup) {

        ActivityResultLauncher<String[]> requestPermissionLauncher =
                ((ComponentActivity) activity).registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
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
        } else {
            permissionGranted.run();
        }
    }

    public static void askForCameraPermission(Activity activity, Runnable permissionGranted,
                                              Runnable permissionRefused) {
        ActivityResultLauncher<String[]> requestPermissionLauncher;
        String[] permissions;


        if (Build.VERSION.SDK_INT < 29) {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
        } else {
            permissions = new String[]{
                    Manifest.permission.CAMERA};
        }

        requestPermissionLauncher = resultLauncherFor(activity, permissionGranted,
                permissionRefused, permissions);

        if (!permissionsAlreadyGranted(activity, permissions)) {
            requestPermissionLauncher.launch(permissions);
        } else {
            permissionGranted.run();
        }
    }

    /**
     * Returns an ActivityResultLauncher for the given permissions requests
     */
    private static ActivityResultLauncher<String[]> resultLauncherFor(Activity activity,
                                                                      Runnable permissionGranted,
                                                                      Runnable permissionRefused,
                                                                      String... permissions){
        return ((ComponentActivity) activity).registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                    Boolean everythingGranted = true;
                    for (int i = 0; i < permissions.length; i++){
                        if (!isGranted.get(permissions[i])) everythingGranted = false;
                    }
                    if (everythingGranted){
                        permissionGranted.run();
                    } else {
                        permissionRefused.run();
                    }
                });
    }

    /**
     * Checks if the given permissions are granted, if not ask for them
     */
    private static boolean permissionsAlreadyGranted(Activity activity, String... permissions){
        Boolean everythingGranted = true;
        for (int i = 0; i < permissions.length; i++){
            if (ActivityCompat.checkSelfPermission(activity, permissions[i])
                    != PackageManager.PERMISSION_GRANTED){
                everythingGranted = false;
            }
        }
        return everythingGranted;
    }
}
