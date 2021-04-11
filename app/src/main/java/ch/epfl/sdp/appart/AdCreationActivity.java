package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.ad.AdCreationViewModel;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.ad.PricePeriod;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity for creating a new Ad.
 * <p>
 * It should be accessible only if the user is logged in.
 */
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

    /**
     * Set values to viewmodel and confirm creation
     */
    private void createAd() {
        // set values to viewmodel
        setVMValues();

        // confirm creation and elaborate result
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

    private void setVMValues(){
        mViewModel.setTitle(((EditText) findViewById(R.id.title_AdCreation_editText)).getText().toString());
        mViewModel.setStreet(joinStrings(
                ((EditText) findViewById(R.id.street_AdCreation_editText)).getText().toString(),
                ((EditText) findViewById(R.id.number_AdCreation_ediText)).getText().toString()));
        mViewModel.setCity(joinStrings(
                ((EditText) findViewById(R.id.npa_AdCreation_editText)).getText().toString(),
                ((EditText) findViewById(R.id.city_AdCreation_editText)).getText().toString()));
        mViewModel.setPrice(Long.valueOf(Long.parseLong(
                ((EditText) findViewById(R.id.price_AdCreation_editText)).getText().toString()
        )));
        mViewModel.setPricePeriod(
                PricePeriod.ALL.get(
                        ((Spinner) findViewById(R.id.period_AdCreation_spinner))
                                .getSelectedItemPosition())
        );
        mViewModel.setDescription(
                ((EditText) findViewById(R.id.description_AdCreation_editText)).getText().toString()
        );
        // TODO modify when logic for adding vrtour is added
        mViewModel.setVRTourEnable(false);
        // TODO modify whe logic for adding images is added
        mViewModel.setPhotosRefs(new ArrayList<>());
    }

    private String joinStrings(String s1, String s2){
        return s1 + " " + s2;
    }

    /**
     * Opens camera activity to take/select images to add to the ad
     */
    private void takePhoto() {
        // TODO save photos path to VM photosRefs
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
}
