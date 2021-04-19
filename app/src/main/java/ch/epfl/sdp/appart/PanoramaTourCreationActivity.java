package ch.epfl.sdp.appart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

public class PanoramaTourCreationActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panorama_tour_creation);

        Button plusButton = findViewById(R.id.plus_PanoramaCreation);
        Button minusButton = findViewById(R.id.minus_PanoramaCreation);
        Button addPanoramaButton = findViewById(R.id.add_PanoramaCreation);

        addPanoramaButton.setOnClickListener((View view) -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            // String picturePath contains the path of selected Image
        } else if (resultCode != RESULT_OK || null == data) {
            throw new IllegalStateException("failed to retrieve the picture");
        }
    }

}