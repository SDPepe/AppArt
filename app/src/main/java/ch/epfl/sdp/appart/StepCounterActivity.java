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

/**
 * This activity counts the steps of the user once the start button is pressed.
 * It also shows the total number of steps done from last boot. It works
 * with the STEP_COUNTER and the STEP_DETECTOR sensor.
 */
public class StepCounterActivity extends AppCompatActivity implements SensorEventListener  {

    /* UI components */
    private TextView textViewStepCounter;
    private TextView textViewStepDetector;
    private TextView textViewKm;
    private Button startButton;
    private Button stopButton;
    private ProgressBar progressBar;

    /* sensors */
    private SensorManager sensorManager;
    private Sensor mStepCounter;
    private Sensor mStepDetector;

    private final static int STEP_COUNTER_PERMISSION_CODE = 110;

    /* the below constants are for the 'number of meters' text view */
    private final static double AVERAGE_STEP_SIZE_IN_METERS = 0.65;
    private final static String METERS_UNIT = " meters";

    /* in case this is not available (depends on the device running the app) the steps will be
     * computed as a subtraction of current total STEP_COUNT with the previous total STEP_COUNT */
    private static boolean stepDetectorSensorIsAvailable = false;

    /* boolean values which determine the state of the StepCounter activity */
    private static boolean startWasPressed = false;
    private static boolean stopWasPressed = false;

    /* this value is updated with the STEP_COUNTER sensor */
    private static int totalStepCountFromBoot = 0;
    /* these values are updated with the STEP_DETECTOR sensor */
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
        textViewKm = findViewById(R.id.km_StepCounter_TextView);
        startButton = findViewById(R.id.startStepCount_StepCounter_Button);
        stopButton = findViewById(R.id.stopStepCount_StepCounter_Button);
        progressBar = findViewById(R.id.progress_StepCounter_ProgressBar);
        progressBar.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);

        /* activity recognition permission request */
        PermissionRequest.askForActivityRecognitionPermission(this, () -> {
            Log.d("PERMISSION", "Activity recognition permission granted");
        }, () -> {
            Log.d("PERMISSION", "Activity recognition permission refused");
            finish();
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSensorsAndAddListeners();

        setTextViewStepCounter();
        setTextViewStepDetector();

        /* this resumes the activity when the user previously
         * moved the execution in background by quitting */
        if (startWasPressed) {
            onStartButton(this.findViewById(R.id.startStepCount_StepCounter_Button));
        }
    }

    /**
     * This method starts the count.
     * Called by the start button.
     */
    public void onStartButton(View view) {
        /* needed for sensors */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        getSensorsAndAddListeners();
        startButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);

        startWasPressed = true;

        /* this resets the text detector to 0 once the start button is re-pressed after stop */
        if (stopWasPressed) {
            textViewStepDetector.setText("0");
            stopWasPressed = false;
        }

        setTextViewStepCounter();
        setTextViewStepDetector();

    }

    /**
     * This method stops the step count and resets all counters and states.
     * Called by the stop button.
     */
    public void onStopButton(View view) {
        startButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);

        stopWasPressed = true;

        /* unregister listeners for step sensor signals */
        unregisterListeners();

        resetDefaultActivityValues();

        /* clear the screen flag */
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void getSensorsAndAddListeners() {

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            mStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
            stepDetectorSensorIsAvailable = true;
        } else {
            /* if only the STEP_DETECTOR sensor is missing this activity will still work
             * but with less accuracy */
            makeText(this, R.string.noStepDetectorSensorErrorMessage, Toast.LENGTH_LONG).show();
            stepDetectorSensorIsAvailable = false;
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            makeText(this, R.string.noStepCounterSensorErrorMessage, Toast.LENGTH_LONG).show();
            /* if the STEP_COUNTER and STEP_DETECTOR sensor is missing on device this activity cannot work */
            if (!stepDetectorSensorIsAvailable) {
                finish();
            }
        }
    }

    /**
     * The sensor of type STEP_COUNTER returns the number of steps taken by the user since the
     * last reboot while activated. The value is returned as a float (with the
     * fractional part set to zero) and is reset to zero only on a system reboot.
     *
     * The sensor of type STEP_DETECTOR send a signal whenever the sensor registers a step.
     *
     * @param sensorEvent the event given by the sensor
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (startWasPressed && !stopWasPressed) {
            if (sensorEvent.sensor.equals(mStepCounter)) {
                totalStepCountFromBoot = (int) sensorEvent.values[0];
                setTextViewStepCounter();

                /* this is used to compute the steps on devices with no STEP_DETECTOR sensor */
                if (!stepDetectorSensorIsAvailable) {
                    computeStepsWithMissingStepDetector(sensorEvent);
                }

            } else if (sensorEvent.sensor.equals(mStepDetector)) {
                if (stepDetectorSensorIsAvailable) {
                    detectedStepsCount += 1;
                    setTextViewStepDetector();
                }
            }
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        setTextViewStepCounter();
        setTextViewStepDetector();
    }

    private void setTextViewStepCounter() {
        if (detectedStepsCount > 0) {
            textViewStepCounter.setText(String.valueOf(totalStepCountFromBoot));
        } else {
            if (startWasPressed) {
                textViewStepCounter.setText(getResources().getString(R.string.loadingMessage));
            } else {
                textViewStepCounter.setText("0");
            }
        }
    }

    private void setTextViewStepDetector() {
        textViewStepDetector.setText(String.valueOf(detectedStepsCount));

        int meters = Math.round((float) (detectedStepsCount * AVERAGE_STEP_SIZE_IN_METERS));
        String metersInString = "~ ";
        metersInString = metersInString.concat(String.valueOf(meters).concat(METERS_UNIT));

        textViewKm.setText(metersInString);
    }

    private void computeStepsWithMissingStepDetector(SensorEvent sensorEvent) {
        if (!initialTotalStepCountWasSet) {
            initialTotalStepCountFromBoot = (int) sensorEvent.values[0];
            initialTotalStepCountWasSet = true;
        }
        detectedStepsCount = ((int)sensorEvent.values[0] - initialTotalStepCountFromBoot);
        setTextViewStepDetector();
    }

    private void unregisterListeners() {
        /* unregister listeners for step sensor signals */
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.unregisterListener(this, mStepCounter);
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            sensorManager.unregisterListener(this, mStepDetector);
        }
    }

    private void resetDefaultActivityValues() {
        /* reset activity attributes */
        startWasPressed = false;
        initialTotalStepCountWasSet = false;
        totalStepCountFromBoot = 0;
        detectedStepsCount = 0;
        initialTotalStepCountFromBoot = 0;

        /* reset step counter to 0 if its state is loading... */
        if (textViewStepCounter.getText().toString().contains(getResources().getString(R.string.loadingMessage))) {
            textViewStepCounter.setText("0");
        }
    }

}
