package ch.epfl.sdp.appart;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.widget.Toast.makeText;

public class CameraActivity extends AppCompatActivity {

    private static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int GALLERY_REQUEST_CODE = 105;
    private static final String TAG = CameraActivity.class.getSimpleName();

    private Button cameraBtn, galleryBtn;
    private String currentPhotoPath;
    private ImageView mImageView;
    //private StorageReference storagereference;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        //storagereference = FirebaseStorage.getInstance().getReference();

        mImageView = findViewById(R.id.image_view);
        cameraBtn = findViewById(R.id.button_camera);
        galleryBtn = findViewById(R.id.button_gallery);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                askCamPermission();
            }
        });

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
        if (requestCode == CAMERA_REQUEST_CODE & resultCode == Activity.RESULT_OK) {
            mImageView.setImageURI(imageUri);
        }
        if (requestCode == GALLERY_REQUEST_CODE & resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            mImageView.setImageURI(imageUri);
        }
    }

    //add image to storage database
/*
  private void uploadImageToFirebase(String name, Uri imageUri){
    StorageReference image = storagereference.child("pic/"+ name);
    image.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
      @Override
      public void onSuccess(TaskSnapshot taskSnapshot) {
          image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
              Log.d(TAG, "Upload image URL is " + uri.toString());
            }
          });
          makeText(CameraActivity.this, "Image is uploaded!", Toast.LENGTH_SHORT).show();
      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        makeText(CameraActivity.this, "Upload failled!", Toast.LENGTH_SHORT).show();
      }
    });
  }
*/
}