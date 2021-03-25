package ch.epfl.sdp.appart.virtualtour;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.sdp.appart.R;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.panoramagl.PLBlankPanorama;
import com.panoramagl.PLCubicPanorama;
import com.panoramagl.PLCylindricalPanorama;
import com.panoramagl.PLICamera;
import com.panoramagl.PLIQuadricPanorama;
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

        //init camera parameter for the view
        PLSphericalPanorama panorama = new PLSphericalPanorama();

        panorama.getCamera().lookAtAndZoomFactor(30.0f, 90.0f, 0.4f, false);
        //panorama.setImage(new PLImage(PLUtils.getBitmap(this, R.drawable.panorama_test), false));

        Glide.with(this)
                .asBitmap()
                .load(R.drawable.panorama_test)
                .into(new CustomTarget<Bitmap>(){
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        panorama.setImage(new PLImage(resource));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                });

        float pitch = 5f;
        float yaw = 0f;
        float zoomFactor = 1.0f;
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
