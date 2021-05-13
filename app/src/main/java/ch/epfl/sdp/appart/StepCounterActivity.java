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
    private ProgressBar progressBar;

    private SensorManager sensorManager;
    private Sensor mStepCounter;
    private Sensor mStepDetector;
    private boolean counterSensorIsPresent;
    private boolean detectorSensorIsPresent;

    private int stepCount = 0;
    private int stepDetect = 0;

    private final static int STEP_COUNTER_PERMISSION_CODE = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        PermissionRequest.askForActivityRecognitionPermission(this, () -> {
            Log.d("PERMISSION", "Pedometer permission granted");
            initActivity();
        }, () -> {
            Log.d("PERMISSION", "Pedometer permission refused");
            finish();
        });
    }

    private void initActivity() {
        textViewStepCounter = findViewById(R.id.totalNumberOfSteps_StepCounter_TextView);
        textViewStepDetector = findViewById(R.id.numberOfSteps_StepCounter_TextView);
        startButton = findViewById(R.id.startStepCount_StepCounter_Button);
        progressBar = findViewById(R.id.progress_StepCounter_ProgressBar);

        textViewStepDetector.setText("0");
        textViewStepCounter.setText("0");
        progressBar.setVisibility(View.INVISIBLE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * closes activity when back button pressed on phone
     */
    @Override
    public void onBackPressed() {
       getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       finish();
    }

    public void onStartButton(View view) {
        startButton.setVisibility(View.GONE);

        setStepCounter();
        setStepDetector();
        progressBar.setVisibility(View.VISIBLE);

        this.textViewStepDetector.setText("1");
        this.textViewStepCounter.setText("1");
    }

    public void setStepCounter() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            counterSensorIsPresent = true;
        } else {
            // show snake
            counterSensorIsPresent = false;
        }
    }

    public void setStepDetector() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            mStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            detectorSensorIsPresent = true;
        } else {
            // show snake
            detectorSensorIsPresent = false;
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
            setStepCounter();
            setStepDetector();
            progressBar.setVisibility(View.VISIBLE);
        } else {
            makeText(this, "Permission is required to use feature!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == mStepCounter) {
            stepCount = (int) sensorEvent.values[0];
            textViewStepCounter.setText(String.valueOf(stepCount));
        } else if (sensorEvent.sensor.equals(mStepDetector)) {
            stepDetect = (int) (stepDetect+sensorEvent.values[0]);
            textViewStepDetector.setText(String.valueOf(stepDetect));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            sensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
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
    }
}