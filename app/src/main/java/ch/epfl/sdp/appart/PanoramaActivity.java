package ch.epfl.sdp.appart;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.panoramagl.PLImage;
import com.panoramagl.PLManager;
import com.panoramagl.PLSphericalPanorama;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoader;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * This class manages the UI of the Panorama.
 */
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
    List<String> images;
    int currImage;
    ImageButton leftButton;
    ImageButton rightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panoramagl);
        leftButton = (ImageButton) findViewById(R.id.leftImage_Panorama_imageButton);
        rightButton = (ImageButton) findViewById(R.id.rightImage_Panorama_imageButton);

        getImages();
        currImage = 0;


        // init PL manager
        plManager = new PLManager(this);
        plManager.setContentView(findViewById(R.id.content_Panorama_relativeLayout));
        plManager.onCreate();
        plManager.setAccelerometerEnabled(false);
        plManager.setInertiaEnabled(false);
        plManager.setZoomEnabled(false);

        loadImage();
        disableLeftButton();
        if (images.size() < 2)
            disableRightButton();
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

    /**
     * Method called when the activity is done and should be closed.
     *
     * @param view
     */
    public void goBack(View view) {
        finish();
    }

    /**
     * Load previous panorama image
     *
     * @param view
     */
    public void goLeft(View view) {
        currImage--;
        loadImage();
        if (currImage == 0)
            disableLeftButton();
        if (currImage == images.size() - 2)
            enableRightButton();
    }

    /**
     * Load next panorama image
     *
     * @param view
     */
    public void goRight(View view) {
        currImage++;
        loadImage();
        if (currImage == images.size() - 1)
            disableRightButton();
        if (currImage == 1)
            enableLeftButton();
    }

    /**
     * Init the list of panorama images, set the current image index to 0, load image.
     */
    private void getImages() {
        images = new ArrayList<>();
        // TODO change with database call to get image references
        images.add("file:///android_asset/panorama_test.jpg");
        images.add("file:///android_asset/panorama_test_2.jpg");
        images.add("file:///android_asset/panorama_test_3.jpg");
        images.add("file:///android_asset/panorama_test_4.jpg");
    }

    /**
     * Load image at current index into panorama
     */
    private void loadImage() {
        PLSphericalPanorama panorama = new PLSphericalPanorama();

        CompletableFuture<Bitmap> bitmapFuture = new CompletableFuture<>();
        database.accept(new GlideBitmapLoader(this, bitmapFuture, images.get(currImage)));

        bitmapFuture.thenApply(bitmap -> {
            panorama.setImage(new PLImage(bitmap, true));
            panorama.getCamera().lookAtAndZoomFactor(30.0f, 90.0f, 0.5f, false);
            plManager.setPanorama(panorama);
            return bitmap;
        });
    }

    /**
     * Disable left button and make it invisible
     */
    private void disableLeftButton() {
        leftButton.setEnabled(false);
        leftButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Enable left button and make it visible
     */
    private void enableLeftButton() {
        leftButton.setEnabled(true);
        leftButton.setVisibility(View.VISIBLE);
    }

    /**
     * Disable right button and make it invisible
     */
    private void disableRightButton() {
        rightButton.setEnabled(false);
        rightButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Enable right button and make it visible
     */
    private void enableRightButton() {
        rightButton.setEnabled(true);
        rightButton.setVisibility(View.VISIBLE);
    }

}
