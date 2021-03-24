package ch.epfl.sdp.appart.virtualtour;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.sdp.appart.R;

import com.panoramagl.PLICamera;
import com.panoramagl.PLImage;
import com.panoramagl.PLManager;
import com.panoramagl.PLSphericalPanorama;
import com.panoramagl.utils.PLUtils;

public class PanoramaGlActivity extends AppCompatActivity {

    private PLManager plManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panoramagl);
        plManager = new PLManager(this);
        plManager.setContentView((ViewGroup) findViewById(R.id.content_view));
        plManager.onCreate();

        plManager.setAccelerometerEnabled(false);
        plManager.setInertiaEnabled(false);
        plManager.setZoomEnabled(false);

        PLSphericalPanorama panorama = new PLSphericalPanorama();
        panorama.setImage(new PLImage(PLUtils.getBitmap(this, R.drawable.panorama_test), false));

        float pitch = 5f;
        float yaw = 0f;
        float zoomFactor = 0.8f;


        PLICamera camera = plManager.getPanorama().getCamera();
        pitch = camera.getPitch();
        yaw = camera.getYaw();
        zoomFactor = camera.getZoomFactor();

        panorama.getCamera().lookAtAndZoomFactor(pitch, yaw, zoomFactor, false);
        plManager.setPanorama(panorama);
    }

    @Override
    protected void onResume() {
        super.onResume();
        plManager.onResume();
    }

    @Override
    protected void onPause() {
        plManager.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        plManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return plManager.onTouchEvent(event);
    }
}
