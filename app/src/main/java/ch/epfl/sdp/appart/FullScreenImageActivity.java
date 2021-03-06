package ch.epfl.sdp.appart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import ch.epfl.sdp.appart.ad.ResizableImageView;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoader;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity that shows an image fullscreen.
 * <p>
 * The image is loaded from the path passed through the intent extras. The image can be zoomed in
 * and out with pinch gestures.
 */
@AndroidEntryPoint
public class FullScreenImageActivity extends AppCompatActivity {

    @Inject
    DatabaseService db;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_fullscreen_image);
        ResizableImageView photo = findViewById(R.id.image_FullScreenImage_imageView);
        String fullRef = getIntent().getStringExtra("imageId");
        boolean isLocal = getIntent().getBooleanExtra("isLocalExtra", false);
        if(isLocal) {
            Bitmap bitmap = BitmapFactory.decodeFile(fullRef);
            Glide.with(this).load(bitmap).into(photo);
        } else {
            db.accept(new GlideImageViewLoader(this, photo, fullRef));
        }
    }

}

