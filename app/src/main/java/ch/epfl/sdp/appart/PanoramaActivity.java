package ch.epfl.sdp.appart;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.panoramagl.PLImage;
import com.panoramagl.PLManager;
import com.panoramagl.PLSphericalPanorama;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;
import ch.epfl.sdp.appart.database.firebaselayout.CardLayout;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoader;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoader;
import ch.epfl.sdp.appart.utils.FirebaseIndexedImagesComparator;
import ch.epfl.sdp.appart.utils.StoragePathBuilder;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * This class manages the UI of the Panorama.s
 */
@AndroidEntryPoint
public class PanoramaActivity extends AppCompatActivity {

    @Inject
    DatabaseService database;
    private PLManager plManager;
    List<String> images;
    int currImage;
    ImageButton leftButton;
    ImageButton rightButton;
    private Bitmap bitmap;
    private String currentAdId;


    //only meant for testing and should be used a single time !
    private CompletableFuture<Boolean> hasCurrentImageLoadingFailed = new CompletableFuture<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panoramagl);

        Bundle extras = this.getIntent().getExtras();
        if (extras != null && extras.containsKey(AdActivity.Intents.INTENT_PANORAMA_PICTURES)
                && extras.containsKey(AdActivity.Intents.INTENT_AD_ID))  {
            images = extras.getStringArrayList(AdActivity.Intents.INTENT_PANORAMA_PICTURES);
            Collections.sort(images, new FirebaseIndexedImagesComparator());
            currentAdId = extras.getString(AdActivity.Intents.INTENT_AD_ID);
        }

        leftButton = (ImageButton) findViewById(R.id.leftImage_Panorama_imageButton);
        rightButton = (ImageButton) findViewById(R.id.rightImage_Panorama_imageButton);

        // TODO will have to switch to future when it will load from db
        getImages();
        currImage = 0;

        // init PL manager
        plManager = new PLManager(this);
        plManager.setContentView(findViewById(R.id.content_Panorama_relativeLayout));
        plManager.onCreate();
        plManager.setAccelerometerEnabled(true);
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
     * Method called when the device back button is tapped. It closes the activity.
     */
    @Override
    public void onBackPressed() {
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
    private void getImages() {}

    /**
     * Load image at current index into panorama
     */
    private void loadImage() {
        PLSphericalPanorama panorama = new PLSphericalPanorama();

        CompletableFuture<Bitmap> bitmapFuture = new CompletableFuture<>();

        String imagePath = new StoragePathBuilder()
                .toAdsStorageDirectory()
                .toDirectory(currentAdId)
                .withFile(images.get(currImage));

        database.accept(new GlideBitmapLoader(this, bitmapFuture, imagePath));

        bitmapFuture.thenApply(bitmap -> {
            hasCurrentImageLoadingFailed.complete(true);
            panorama.setImage(new PLImage(bitmap, true));
            panorama.getCamera().lookAtAndZoomFactor(30.0f, 90.0f, 0.5f, false);
            plManager.setPanorama(panorama);
            this.bitmap = bitmap;
            return bitmap;
        });

        bitmapFuture.exceptionally(e -> {
            hasCurrentImageLoadingFailed.complete(false);
            Snackbar.make(findViewById(R.id.horizontal_AdCreation_scrollView),
                    getResources().getText(R.string.snackbarError_Panorama),
                    Snackbar.LENGTH_SHORT).show();
            return null;
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

    /**
     * WARNING should be used only for testing
     * @return a future that complete once the image has loaded and will be true if success,
     * false otherwise.
     */
    @Deprecated
    public CompletableFuture<Boolean> hasCurrentImageLoadingFailed() {
        return this.hasCurrentImageLoadingFailed;
    }

}
