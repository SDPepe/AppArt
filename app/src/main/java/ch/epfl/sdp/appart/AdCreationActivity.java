package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.adcreation.AdCreationViewModel;
import ch.epfl.sdp.appart.database.DatabaseService;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdCreationActivity extends AppCompatActivity {

    @Inject
    DatabaseService database;

    AdCreationViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_adcreation);
        mViewModel = new ViewModelProvider(this).get(AdCreationViewModel.class);

        // init buttons
        Button confirmButton = findViewById(R.id.confirm_AdCreation_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAd();
            }
        });
        Button addPhotoButton = findViewById(R.id.addPhoto_AdCreation_button);
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });
    }

    private void createAd() {
        CompletableFuture<Boolean> result = mViewModel.confirmCreation();
        result.thenAccept(completed -> {
            if (completed) {
                Intent intent = new Intent(this, AdActivity.class);
                intent.putExtra("fromAdCreation", true);
                startActivity(intent);
            } else {
                Snackbar.make(findViewById(R.id.horizontal_AdCreation_scrollView),
                        getResources().getText(R.string.toolbarTitle_AdCreation),
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void takePhoto() {
        // TODO save photos path to VM photosRefs
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
}
