package ch.epfl.sdp.appart;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.ad.AdCreationViewModel;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
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
    private final static int PICTURES_IMPORT_ACTIVITY_RESULT = 2;
    private List<Uri> picturesUris;
    private List<Uri> panoramaUris;

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
            Intent intent = new Intent(this, PicturesImportActivity.class);
            startActivityForResult(intent, PICTURES_IMPORT_ACTIVITY_RESULT);
        });

        /*
        if (MainActivity.isDemoMode()) {

            ((TextView)findViewById(R.id.title_AdCreation_editText)).setText("Cool Ad");
            ((TextView)findViewById(R.id.street_AdCreation_editText)).setText("Funny Street");
            ((TextView)findViewById(R.id.number_AdCreation_ediText)).setText("1A");
            ((TextView)findViewById(R.id.npa_AdCreation_editText)).setText("1000");
            ((TextView)findViewById(R.id.city_AdCreation_editText)).setText("Lausanne");
            ((TextView)findViewById(R.id.price_AdCreation_editText)).setText("1234");
            ((TextView)findViewById(R.id.description_AdCreation_editText)).setText("Welcome to Appart !");
            //"android.resource://ch.epfl.sdp.appart/"
            Uri simplePictureUri = Uri.parse("file:///android_asset/Ads/fake_ad_1.jpg");
            Uri panoramaUri = Uri.parse("file:///android_asset/panorama_test.jpg");
            List<Uri> picturesUris = Arrays.asList(simplePictureUri, simplePictureUri, simplePictureUri);
            List<Uri> panoramasUris = Arrays.asList(panoramaUri, panoramaUri, panoramaUri);
            this.picturesUris = picturesUris;
            this.panoramaUris = panoramasUris;

            fillHorizontalViewWithPictures(findViewById(R.id.pictures_AdCreation_linearLayout), picturesUris);
            fillHorizontalViewWithPictures(findViewById(R.id.panorama_AdCreation_linearLayout), panoramasUris);

        }
         */
    }

    private void setTextView(TextView textView, String content) {
        textView.setText(content);
    }

    /**
     * Sets values to viewmodel and confirm creation. If some fields are not filled, it shows a
     * snackbar message.
     */
    private void createAd() {
        // if some fields are not filled, show snackbar
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
        // TODO show a loading box and disable modifiying field / click buttons -> maybe a loading screen?
        CompletableFuture<Boolean> result = mViewModel.confirmCreation();
        result.thenAccept(completed -> {
            if (completed) {
                // TODO switch back when user is synced with firestore
                Intent intent = new Intent(this, ScrollingActivity.class);
                //intent.putExtra("fromAdCreation", true);
                startActivity(intent);

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
        mViewModel.setUri(picturesUris);
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
    @SuppressWarnings("deprecation")
    private void takePhoto() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(ActivityCommunicationLayout.PROVIDING_ACTIVITY_NAME, ActivityCommunicationLayout.AD_CREATION_ACTIVITY);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK) {
                int size = data.getIntExtra(ActivityCommunicationLayout.PROVIDING_SIZE, 0);
                List<Uri> listUri = new ArrayList<>();
                for(int i = 0; i< size; i++){
                 listUri.add(data.getParcelableExtra(ActivityCommunicationLayout.PROVIDING_IMAGE_URI + i));
                }
                picturesUris = listUri;
                fillHorizontalViewWithPictures(findViewById(R.id.pictures_AdCreation_linearLayout), listUri);
            }
        } else if (requestCode == PICTURES_IMPORT_ACTIVITY_RESULT) {
            if (resultCode == RESULT_OK) {
                ArrayList<Uri> uris = data.getParcelableArrayListExtra("uris");
                if (uris == null) {
                    throw new IllegalStateException("uris cannot be null");
                }
                mViewModel.setPanoramaUri(uris);
                fillHorizontalViewWithPictures(findViewById(R.id.panorama_AdCreation_linearLayout), uris);
            }
        }
    }

    protected void fillHorizontalViewWithPictures(LinearLayout horizontalLayout, List<Uri> uris) {

        if (uris == null || uris.isEmpty()) {
            return;
        }

        horizontalLayout.removeAllViews();

        for (int i = 0; i < uris.size(); i++) {
            LayoutInflater inflater =
                    (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.photo_layout, (ViewGroup) null);
            ImageView photo = myView.findViewById(R.id.photo_Photo_imageView);
            Glide.with(this).load(uris.get(i)).into(photo);
            horizontalLayout.addView(myView);
            if (i != 4) {
                Space hspacer = new Space(this);
                hspacer.setLayoutParams(new ViewGroup.LayoutParams(
                        8,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));
                horizontalLayout.addView(hspacer);
            }
        }
    }

    protected void setPanoramas(List<Uri> uris) {
        this.panoramaUris = uris;
    }

    protected void setPictures(List<Uri> uris) {
        this.picturesUris = uris;
    }

}
