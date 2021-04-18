package ch.epfl.sdp.appart;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ch.epfl.sdp.appart.database.DatabaseService;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

import static android.widget.Toast.makeText;

/**
 * This class manages the UI of the Camera.
 */
@AndroidEntryPoint
public class CameraActivity extends AppCompatActivity {

    private static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int GALLERY_REQUEST_CODE = 105;
    private static final String TAG = CameraActivity.class.getSimpleName();

    private ImageView mImageView;
    private Uri imageUri;

    @Inject
    DatabaseService database;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mImageView = findViewById(R.id.image_Camera_imageView);
        Button cameraBtn = findViewById(R.id.camera_Camera_button);
        Button galleryBtn = findViewById(R.id.gallery_Camera_button);

        cameraBtn.setOnClickListener(w -> askCamPermission());

        galleryBtn.setOnClickListener((v) -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST_CODE);
        });
    }

    private void askCamPermission() {
        if (ContextCompat.checkSelfPermission(this, permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //show popup to request permission
            ActivityCompat
                    .requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            startCamera();
        }
    }

    @SuppressWarnings("deprecation")
    private void startCamera() {
        ContentValues val = new ContentValues();
        val.put(MediaStore.Images.Media.TITLE, "new picture");
        val.put(Media.DESCRIPTION, "form the phone");
        imageUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, val);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            checkCamPermission(grantResults);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void checkCamPermission(@NonNull int[] grantResults) {
        if ((grantResults.length > 0) & (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            startCamera();
        } else {
            makeText(this, "Camera permission is required to use camera!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE & resultCode == Activity.RESULT_OK)
            uploadImage();
        if (requestCode == GALLERY_REQUEST_CODE & resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage(){
        CompletableFuture<Boolean> futureImage= database.putImage(imageUri, "test"+System.currentTimeMillis()+"." +getFileExtension(imageUri), "test/1234qwe123/photo0" );
        futureImage.exceptionally(e -> {
            Toast.makeText(CameraActivity.this, "Upload of image fail", Toast.LENGTH_LONG).show();
            return null;
        });
        futureImage.thenAccept( res -> {
            if(res == true){
                Toast.makeText(CameraActivity.this, "Successfully upload image ", Toast.LENGTH_LONG).show();
                mImageView.setImageURI(imageUri);
            } else {
                Toast.makeText(CameraActivity.this, "Upload of image fail", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}


