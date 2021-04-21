package ch.epfl.sdp.appart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
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
        confirmButton.setOnClickListener((View view) -> createAd());
        Button addPhotoButton = findViewById(R.id.addPhoto_AdCreation_button);
        addPhotoButton.setOnClickListener((View view) -> takePhoto());
        Button createPanoramaTourButton = findViewById(R.id.createVirtualTour_AdCreation_button);
        createPanoramaTourButton.setOnClickListener((View view) -> {
            Intent intent = new Intent(this, PanoramaTourCreationActivity.class);
            startActivity(intent);
        });


    }

    /**
     * Sets values to viewmodel and confirm creation. If some fields are not filled, it shows a
     * snackbar message.
     */
    private void createAd() {
        // if some fields are not filled, show snackbar

        String log = null;
        if(mViewModel.getUri() != null){
            log = mViewModel.getUri().toString();
        }


        if (!everyFieldFilled()) {
            Snackbar.make(findViewById(R.id.horizontal_AdCreation_scrollView),
                    getResources().getText(R.string.snackbarNotFilled_AdCreation),
                    Snackbar.LENGTH_SHORT).show();
            return;
        }
        // TODO add back when new loading image system is finished
        /*if (!mViewModel.hasPhotos()){
            Snackbar.make(findViewById(R.id.horizontal_AdCreation_scrollView),
                    getResources().getText(R.string.snackbarNoPhotos_AdCreation),
                    Snackbar.LENGTH_SHORT).show();
            return;
        }*/

        // set values to viewmodel
        setVMValues();

        // confirm creation and elaborate result
        CompletableFuture<Boolean> result = mViewModel.confirmCreation();
        result.thenAccept(completed -> {
            if (completed) {
                // TODO switch back when user is synced with firestore
                finish();
                //Intent intent = new Intent(this, AdActivity.class);
                //intent.putExtra("fromAdCreation", true);
                //startActivity(intent);
            } else {
                Snackbar.make(findViewById(R.id.horizontal_AdCreation_scrollView),
                        getResources().getText(R.string.snackbarFailed_AdCreation),
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Takes the values from the EditTexts of the activity and sets the values in the ViewModel
     */
    private void setVMValues() {
        mViewModel.setTitle(getContentOfEditText(R.id.title_AdCreation_editText));
        mViewModel.setStreet(joinStrings(
                R.id.street_AdCreation_editText, R.id.number_AdCreation_ediText));
        mViewModel.setCity(joinStrings(
                R.id.npa_AdCreation_editText, R.id.city_AdCreation_editText));
        mViewModel.setPrice(Long.parseLong(getContentOfEditText(R.id.price_AdCreation_editText)));
        mViewModel.setPricePeriod(
                PricePeriod.ALL.get(
                        ((Spinner) findViewById(R.id.period_AdCreation_spinner))
                                .getSelectedItemPosition())
        );
        mViewModel.setDescription(getContentOfEditText(R.id.description_AdCreation_editText));
        // TODO modify when logic for adding vrtour is added
        mViewModel.setVRTourEnable(false);
    }

    private String joinStrings(int id1, int id2) {
        return getContentOfEditText(id1) + " " + getContentOfEditText(id2);
    }

    /**
     * Checks that every text field is has been filled
     *
     * @return true if every string is valid, false otherwise
     */
    private boolean everyFieldFilled() {
        if (stringIsInvalid(getContentOfEditText(R.id.title_AdCreation_editText)) ||
                stringIsInvalid(getContentOfEditText(R.id.street_AdCreation_editText)) ||
                stringIsInvalid(getContentOfEditText(R.id.number_AdCreation_ediText)) ||
                stringIsInvalid(getContentOfEditText(R.id.npa_AdCreation_editText)) ||
                stringIsInvalid(getContentOfEditText(R.id.city_AdCreation_editText)) ||
                stringIsInvalid(getContentOfEditText(R.id.price_AdCreation_editText)) ||
                stringIsInvalid(getContentOfEditText(R.id.description_AdCreation_editText)))
            return false;
        return true;
    }

    /**
     * Gets the content of the EditText.
     *
     * @param id the id of the view
     * @return the string content of the EditText
     */
    private String getContentOfEditText(int id) {
        return ((EditText) findViewById(id)).getText().toString();
    }

    /**
     * Checks whether the string is null or empty.
     *
     * @param s the string to check
     * @return true if the string is null or empty, false otherwise
     */
    private boolean stringIsInvalid(String s) {
        return s == null || s.equals("");
    }

    /**
     * Opens camera activity to take/select images to add to the ad
     */
    private void takePhoto() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("Activity","Ads");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                int size = data.getIntExtra("size", 0);
                List<Uri> listUri = new ArrayList<>();
                for(int i = 0; i< size; i++){
                 listUri.add(data.getParcelableExtra("imageUri"+i));
                }
                mViewModel.setUri(listUri);
            }
        }
    }

}
