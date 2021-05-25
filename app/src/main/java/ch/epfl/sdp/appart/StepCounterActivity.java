package ch.epfl.sdp.appart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
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
    private Button closeButton;
    private ProgressBar progressBar;

    /* sensors */
    private SensorManager sensorManager;
    private Sensor mStepCounter;
    private Sensor mStepDetector;

    /* this activity takes into account the possibility of
     * being executed by a test in order to mock the sensors */
    private boolean ANDROID_TEST_IS_EXECUTING = false;
    private final static int MOCK_STEP_ITERATIONS = 25;

    private final static int STEP_COUNTER_PERMISSION_CODE = 110;

    /* the below constants are for the 'number of meters' text view */
    private final static double AVERAGE_STEP_SIZE_IN_METERS = 0.65;
    private final static String METERS_UNIT = " meters";

    /* in case this is not available (depends on the device running the app) the steps will be
     * computed as a subtraction of current total STEP_COUNT with the previous total STEP_COUNT */
    private boolean stepDetectorSensorIsAvailable = true;

    /* boolean values which determine the state of the StepCounter activity */
    private boolean startWasPressed = false;
    private boolean stopWasPressed = false;

    /* this value is updated with the STEP_COUNTER sensor */
    private int totalStepCountFromBoot = 0;
    /* these values are updated with the STEP_DETECTOR sensor */
    private int detectedStepsCount = 0;

    /* number of steps registered by STEP_COUNT on start button pressed */
    private int initialTotalStepCountFromBoot = 0;
    private boolean initialTotalStepCountWasSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        textViewStepCounter = findViewById(R.id.totalNumberOfSteps_StepCounter_TextView);
        textViewStepDetector = findViewById(R.id.numberOfSteps_StepCounter_TextView);
        textViewKm = findViewById(R.id.km_StepCounter_TextView);
        startButton = findViewById(R.id.startStepCount_StepCounter_Button);
        stopButton = findViewById(R.id.stopStepCount_StepCounter_Button);
        closeButton = findViewById(R.id.closeStepCount_StepCounter_Button);
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

        /* this activity receives an extra string when it is executed
         * by an android test in order to mock onSensorChanged */
        int intentionCode = getIntent().getIntExtra(String.valueOf(ActivityCommunicationLayout.ANDROID_TEST_IS_RUNNING), 0);
        if (intentionCode == ActivityCommunicationLayout.ANDROID_TEST_IS_RUNNING) {
            ANDROID_TEST_IS_EXECUTING = true;
        }

        getSensorsAndAddListeners();
    }


    @Override
    protected void onStart() {
        super.onStart();

        setTextViewStepCounter();
        setTextViewStepDetector();
    }

    @Override
    public void onBackPressed() {
        /* do nothing - the step counter activity can only be finished with the close button - see below */
    }

    /**
     * This method finishes the activity
     * Called by the close button
     */
    public void onCloseButton(View view) {
        finish();
    }

    /**
     * This method starts the count
     * Called by the start button
     */
    public void onStartButton(View view) {
        startWasPressed = true;

        startButton.setVisibility(View.GONE);
        closeButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);

        /* needed for sensors */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        getSensorsAndAddListeners();

        setTextViewStepCounter();
        setTextViewStepDetector();
    }

    /**
     * This method stops the step count and resets all counters and states
     * Called by the stop button
     */
    public void onStopButton(View view) {
        stopWasPressed = true;
        startWasPressed = false;

        progressBar.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);
        closeButton.setVisibility(View.VISIBLE);

        /* clear the screen flag */
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /* sets step counter to '-' if stop was pressed while loading... */
        if (textViewStepCounter.getText().toString().contains(getResources().getString(R.string.loadingMessage))) {
            textViewStepCounter.setText("-");
        }

        /* unregister listeners for step sensor signals */
        unregisterListeners();
    }

    private void getSensorsAndAddListeners() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            mStepDetector = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
            stepDetectorSensorIsAvailable = true;

        } else {
            stepDetectorSensorIsAvailable = false;

            /* if only the STEP_DETECTOR sensor is missing this activity will
             * still work but with less accuracy */
            makeText(this, R.string.noStepDetectorSensorErrorMessage, Toast.LENGTH_LONG).show();
        }

        if (sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_STEP_COUNTER) != null) {
            mStepCounter = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            makeText(this, R.string.noStepCounterSensorErrorMessage, Toast.LENGTH_LONG).show();

            setStartButtonDisabledIfNoTestIsExecuting();
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
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {
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
        if (sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.unregisterListener(this, mStepCounter);
        }

        if (sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_STEP_DETECTOR) != null) {
            sensorManager.unregisterListener(this, mStepDetector);
        }
    }

    /* =================================TESTING PURPOSES================================= */

    private void setStartButtonDisabledIfNoTestIsExecuting() {
        /* verify if the sensor is null because a test is running */
        if (ANDROID_TEST_IS_EXECUTING) {
            onSensorChangedMock();
        } else {
            /* if the STEP_COUNTER and STEP_DETECTOR sensor is missing on device this activity cannot work */
            if (!stepDetectorSensorIsAvailable) {
                startButton.setEnabled(false);
            }
        }
    }

    /**
     * mocks the onSensorChanged call
     */
    private void onSensorChangedMock() {
        for (int i=0; i < MOCK_STEP_ITERATIONS; ++i) {
            totalStepCountFromBoot += 1; detectedStepsCount += 1;
            setTextViewStepCounter(); setTextViewStepDetector();
        }
    }
}
