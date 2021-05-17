package ch.epfl.sdp.appart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.sdp.appart.utils.PermissionRequest;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class StepCounterActivity extends AppCompatActivity implements SensorEventListener  {

    private TextView textViewStepCounter;
    private TextView textViewStepDetector;
    private Button startButton;
    private Button stopButton;
    private ProgressBar progressBar;

    private SensorManager sensorManager;
    private Sensor mStepCounter;
    private Sensor mStepDetector;

    private final static int STEP_COUNTER_PERMISSION_CODE = 110;

    /* in case this is not available (depends on the device running the app) the steps will be
     * computed as a subtraction of current total STEP_COUNT with the previous total STEP_COUNT */
    private static boolean stepDetectorSensorIsAvailable = false;

    /* boolean values which determine the state of the StepCounter activity */
    private static boolean startWasPressed = false;
    private static boolean stopWasPressed = false;

    /* this value is updated with the STEP_COUNTER sensor */
    private static int totalStepCountFromBoot = 0;
    /* this value is updated with the STEP_DETECTOR sensor */
    private static int detectedStepsCount = 0;

    /* number of steps registered by STEP_COUNT on start button pressed */
    private static int initialTotalStepCountFromBoot = 0;
    private static boolean initialTotalStepCountWasSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        textViewStepCounter = findViewById(R.id.totalNumberOfSteps_StepCounter_TextView);
        textViewStepDetector = findViewById(R.id.numberOfSteps_StepCounter_TextView);
        startButton = findViewById(R.id.startStepCount_StepCounter_Button);
        stopButton = findViewById(R.id.stopStepCount_StepCounter_Button);
        progressBar = findViewById(R.id.progress_StepCounter_ProgressBar);
        progressBar.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);

        PermissionRequest.askForActivityRecognitionPermission(this, () -> {
            Log.d("PERMISSION", "Activity recognition permission granted");
        }, () -> {
            Log.d("PERMISSION", "Activity recognition permission refused");
            finish();
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onStartButton(View view) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        getSensorsAndAddListeners();
        startButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);

        startWasPressed = true;

        if (stopWasPressed) {
            textViewStepDetector.setText("0");
            stopWasPressed = false;
        }

        setTextViewStepCounter();
        textViewStepDetector.setText(String.valueOf(detectedStepsCount));

    }

    public void onStopButton(View view) {
        startButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);

        stopWasPressed = true;

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.unregisterListener(this, mStepCounter);
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            sensorManager.unregisterListener(this, mStepDetector);
        }

        /* reset activity attributes */
        startWasPressed = false;
        initialTotalStepCountWasSet = false;
        totalStepCountFromBoot = 0;
        detectedStepsCount = 0;
        initialTotalStepCountFromBoot = 0;

        /* reset step counter to 0 if its state is loading... */
        if (textViewStepCounter.getText().toString().contains("loading")) {
            textViewStepCounter.setText("0");
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSensorsAndAddListeners();

        setTextViewStepCounter();
        textViewStepDetector.setText(String.valueOf(detectedStepsCount));

        if (startWasPressed) {
            onStartButton(this.findViewById(R.id.startStepCount_StepCounter_Button));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == STEP_COUNTER_PERMISSION_CODE) {
            checkPermission(grantResults);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void checkPermission(@NonNull int[] grantResults) {
        if ((grantResults.length > 0) & (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            makeText(this, "Permission is required to use feature!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A sensor of this type returns the number of steps taken by the user since the
     * last reboot while activated. The value is returned as a float (with the
     * fractional part set to zero) and is reset to zero only on a system reboot.
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (!stopWasPressed) {
            if (sensorEvent.sensor.equals(mStepCounter)) {
                totalStepCountFromBoot = (int) sensorEvent.values[0];
                setTextViewStepCounter();

                if (!stepDetectorSensorIsAvailable) {
                    if (!initialTotalStepCountWasSet) {
                        initialTotalStepCountFromBoot = (int) sensorEvent.values[0];
                        initialTotalStepCountWasSet = true;
                    }
                    detectedStepsCount = ((int)sensorEvent.values[0] - initialTotalStepCountFromBoot);
                    textViewStepDetector.setText(String.valueOf(detectedStepsCount));
                }

            } else if (sensorEvent.sensor.equals(mStepDetector)) {
                if (stepDetectorSensorIsAvailable) {
                    detectedStepsCount += 1;
                    textViewStepDetector.setText(String.valueOf(detectedStepsCount));
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        setTextViewStepCounter();
        textViewStepDetector.setText(String.valueOf(detectedStepsCount));
    }

    private void getSensorsAndAddListeners() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);

            if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
                mStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
                sensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
                stepDetectorSensorIsAvailable = true;
            } else {
                makeText(this, "Attention: this device has no step detector sensor. This causes higher latency and less accurate step count!", Toast.LENGTH_LONG).show();
                stepDetectorSensorIsAvailable = false;
            }

        } else {
            makeText(this, "Attention: this device does not support the step counter sensor!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setTextViewStepCounter() {
        if (detectedStepsCount > 0) {
            textViewStepCounter.setText(String.valueOf(totalStepCountFromBoot));
        } else {
            if (startWasPressed) {
                textViewStepCounter.setText("loading...");
            } else {
                textViewStepCounter.setText("0");
            }
        }

    }

}


