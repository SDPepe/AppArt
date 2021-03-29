package ch.epfl.sdp.appart;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.panoramagl.PLImage;
import com.panoramagl.PLManager;
import com.panoramagl.PLSphericalPanorama;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoader;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PanoramaActivity extends AppCompatActivity {

    /**
     * WARNING : For people using bitmaps loaded with Glide:
     * DON'T STORE THE BITMAP ANYWHERE IT WOULD GET RECYCLED BY SOMEONE ELSE
     * Glide has a Cache and take care of its own recycling. as the commented
     * line above would be uncommented and would contain a Glide's loaded Bitmap
     * The activity would have called recycled() on this bitmap when closing.
     * This would have caused an IllegalState and its hard to debug.
     */

    @Inject
    DatabaseService database;
    //private Bitmap bitmap;
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

        panorama.getCamera().lookAtAndZoomFactor(30.0f, 90.0f, 0.5f, false);
        CompletableFuture<Bitmap> bitmapFuture = new CompletableFuture<>();
        database.accept(new GlideBitmapLoader(this, bitmapFuture, "file:///android_asset/panorama_test.jpg"));

        bitmapFuture.thenApply(bitmap -> {
            panorama.setImage(new PLImage(bitmap, true));
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

    public void goBack(View view) {
        finish();
    }

}
