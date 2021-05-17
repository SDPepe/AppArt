package ch.epfl.sdp.appart.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

public class PermissionRequest {

    private static void askForPermission(Activity activity,
                                         Runnable permissionGranted,
                                         Runnable permissionRefused,
                                         Runnable educationalPopup,
                                         String permissions[]) {
        ActivityResultLauncher<String[]> requestPermissionLauncher =
                ((ComponentActivity) activity).registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                    boolean areGranted = true;
                    for (String permission : permissions) {
                        if (!isGranted.get(permission)) {
                            areGranted = false;
                        }
                    }
                    if (areGranted) {
                        //Continue app workfwlo
                        permissionGranted.run();
                    } else {
                        //Tell the user the feature can't be used
                        permissionRefused.run();
                    }
                });

        boolean areNotGranted = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity,
                    permission) == PackageManager.PERMISSION_GRANTED) {
                areNotGranted = false;
            }
        }
        if (areNotGranted) {

            boolean shouldShowRationale = false;
            for (String permission : permissions) {
                shouldShowRationale |= activity.shouldShowRequestPermissionRationale(permission);
            }
            if (shouldShowRationale) {
                //Show educational popup
                educationalPopup.run();
            } else {
                //Ask permission
                requestPermissionLauncher.launch(permissions);
            }
        } else {
            permissionGranted.run();
        }
    }

    public static void askForCameraPermission(Activity activity,
                                              Runnable permissionGranted,
                                              Runnable permissionRefused) {
        String[] permissions;

        if (Build.VERSION.SDK_INT < 29) {
            permissions =
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA};
        } else {
            permissions = new String[]{
                    Manifest.permission.CAMERA};
        }
        askForPermission(activity, permissionGranted, permissionRefused,
                () -> Log.d("PERMISSION", "Educational popup for Camera !"),
                permissions);

    }

    public static void askForLocationPermission(Activity activity,
                                                Runnable permissionGranted,
                                                Runnable permissionRefused,
                                                Runnable educationalPopup) {
        askForPermission(activity, permissionGranted, permissionRefused,
                educationalPopup,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION});
    }

    public static void askForActivityRecognitionPermission(Activity activity,
                                                           Runnable permissionGranted,
                                                           Runnable permissionRefused)  {
        String[] permissions;

        if (Build.VERSION.SDK_INT < 29) {
            permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACTIVITY_RECOGNITION};
        } else {
            permissions = new String[]{
                    Manifest.permission.ACTIVITY_RECOGNITION};
        }

        askForPermission(activity, permissionGranted, permissionRefused,
                () -> Log.d("PERMISSION", "Educational popup for Camera !"),
                permissions);
    }


    public static void askForStoragePermission(Activity activity,
                                               Runnable permissionGranted,
                                               Runnable permissionRefused,
                                               Runnable educationalPopup) {
        askForPermission(activity, permissionGranted, permissionRefused,
                educationalPopup,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }
}
