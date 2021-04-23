package ch.epfl.sdp.appart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

import ch.epfl.sdp.appart.panorama.PictureCardAdapter;
import ch.epfl.sdp.appart.panorama.SwapNotifiable;

/**
 * Manager for the importation of pictures.
 */
public class PicturesImportActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private RecyclerView recyclerView;
    private PictureCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_import);

        recyclerView = findViewById(R.id.recyclerView_PictureImport);
        adapter = new PictureCardAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        Button addButton = findViewById(R.id.add_PictureImport_button);
        Button importButton = findViewById(R.id.finish_PictureImport_button);

        addButton.setOnClickListener((View view) -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
        });

        importButton.setOnClickListener((View view) -> {
            Intent intent = new Intent(this, AdCreationActivity.class);
            startActivity(intent);
        });

    }

    public List<Uri> getOrderedPictures() {
        return adapter.getOrderedPicturesUris();
    }

    /**
     * For testing only !
     * @return
     */
    public SwapNotifiable getAdapter() {
        return adapter;
    }

    /**
     * This function is only meant for testing to isolate the camera
     * @param uri
     */
    public void addPictureToAdapterAndNotify(Uri uri) {
        adapter.addPictureCard(uri);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            adapter.addPictureCard(data.getData());
            adapter.notifyDataSetChanged();
            // String picturePath contains the path of selected Image
        } else if (resultCode != RESULT_OK || null == data) {
            throw new IllegalStateException("failed to retrieve the picture");
        }
    }

}