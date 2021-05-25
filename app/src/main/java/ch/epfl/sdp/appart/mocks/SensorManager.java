package ch.epfl.sdp.appart.mocks;

import ch.epfl.sdp.appart.mocks.Sensor;
import android.hardware.SensorEventListener;

public class SensorManager {

    public static final int SENSOR_DELAY_NORMAL = 3;

    public boolean registerListener(SensorEventListener listener, ch.epfl.sdp.appart.mocks.Sensor sensor, int rate) { return true; }

    public void unregisterListener(SensorEventListener listener, ch.epfl.sdp.appart.mocks.Sensor sensor) {}

    public ch.epfl.sdp.appart.mocks.Sensor getDefaultSensor(int type) {
        return new ch.epfl.sdp.appart.mocks.Sensor();
    }
}
