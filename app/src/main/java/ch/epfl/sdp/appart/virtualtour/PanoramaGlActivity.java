package ch.epfl.sdp.appart.virtualtour;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.database.Database;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoaderImpl;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoaderVisitor;
import dagger.hilt.android.AndroidEntryPoint;

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

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

@AndroidEntryPoint
public class PanoramaGlActivity extends AppCompatActivity {

    private PLManager plManager;

    @Inject
    Database database;

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

        panorama.getCamera().lookAtAndZoomFactor(30.0f, 90.0f, 0.5f, false);
        CompletableFuture<Bitmap> bitmapFuture = new CompletableFuture<>();
        database.accept(new GlideBitmapLoaderImpl(this, bitmapFuture, "file:///android_asset/panorama_test.jpg"));

        bitmapFuture.thenApply(bitmap -> {
            panorama.setImage(new PLImage(bitmap, false));
            plManager.setPanorama(panorama);
            return bitmap;
        });

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

    public void goBack(View view){
        finish();
    }

}
