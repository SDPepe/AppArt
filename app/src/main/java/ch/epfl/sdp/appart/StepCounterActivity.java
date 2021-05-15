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

import org.hamcrest.Condition;

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

    private int stepCount = 0;
    private boolean stopWasPressed = false;
    private boolean stepsHaveToBeRestored = false;

    private static boolean nothingWasPressedOnBackButton = false;
    private static int stepDetect = 0;
    private static int stepCountBeforeUnwantedPause = 0;
    private final static int STEP_COUNTER_PERMISSION_CODE = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        textViewStepCounter = findViewById(R.id.totalNumberOfSteps_StepCounter_TextView);
        textViewStepDetector = findViewById(R.id.numberOfSteps_StepCounter_TextView);
        startButton = findViewById(R.id.startStepCount_StepCounter_Button);
        stopButton = findViewById(R.id.stopStepCount_StepCounter_Button);
        progressBar = findViewById(R.id.progress_StepCounter_ProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        PermissionRequest.askForActivityRecognitionPermission(this, () -> {
            Log.d("PERMISSION", "Pedometer permission granted");
        }, () -> {
            Log.d("PERMISSION", "Pedometer permission refused");
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        if (stopWasPressed) {
            StepCounterActivity.stepDetect = 0;
            StepCounterActivity.stepCountBeforeUnwantedPause = 0;
            StepCounterActivity.nothingWasPressedOnBackButton = false;
        } else {
            StepCounterActivity.nothingWasPressedOnBackButton = true;
        }

        finish();
    }

    public void onStartButton(View view) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        startButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);

        stopWasPressed = false;

        if (StepCounterActivity.stepDetect > 0) {
            textViewStepCounter.setText(String.valueOf(stepCount));
            textViewStepDetector.setText(String.valueOf(StepCounterActivity.stepDetect));
        }
    }

    public void onStopButton(View view) {
        startButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.GONE);
        stopWasPressed = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            makeText(this, "Sensor error: this device does not support activity recognition!", Toast.LENGTH_SHORT).show();
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            mStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            makeText(this, "Sensor error: this device does not support activity recognition!", Toast.LENGTH_SHORT).show();
        }

        textViewStepCounter.setText(String.valueOf(stepCount));
        textViewStepDetector.setText(String.valueOf(StepCounterActivity.stepDetect));

        if (StepCounterActivity.nothingWasPressedOnBackButton) {
            onStartButton(this.findViewById(R.id.startStepCount_StepCounter_Button));
            StepCounterActivity.nothingWasPressedOnBackButton = false;
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
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.equals(mStepCounter)) {
            stepCount = (int) sensorEvent.values[0];
            textViewStepCounter.setText(String.valueOf(stepCount));
        } else if (sensorEvent.sensor.equals(mStepDetector)) {
            StepCounterActivity.stepDetect = (int) (StepCounterActivity.stepDetect+sensorEvent.values[0]);
            textViewStepDetector.setText(String.valueOf(StepCounterActivity.stepDetect));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        textViewStepCounter.setText(String.valueOf(stepCount));
        textViewStepDetector.setText(String.valueOf(StepCounterActivity.stepDetect));
    }

    @Override
    protected void onResume() {
        super.onResume();

        textViewStepCounter.setText(String.valueOf(stepCount));
        textViewStepDetector.setText(String.valueOf(StepCounterActivity.stepDetect));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            sensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (stepsHaveToBeRestored) {
            StepCounterActivity.stepDetect += stepCount - StepCounterActivity.stepCountBeforeUnwantedPause;
            textViewStepCounter.setText(String.valueOf(stepCount));
            textViewStepDetector.setText(String.valueOf(StepCounterActivity.stepDetect));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.unregisterListener(this, mStepCounter);
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            sensorManager.unregisterListener(this, mStepDetector);
        }

        if (!stopWasPressed) {
            stepsHaveToBeRestored = true;
        }
        StepCounterActivity.stepCountBeforeUnwantedPause = stepCount;
    }
}