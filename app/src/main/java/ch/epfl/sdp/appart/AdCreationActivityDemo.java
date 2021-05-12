package ch.epfl.sdp.appart;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * This class is used to demo the app and display an already filled ad in the ad creation
 * activity.
 */
public class AdCreationActivityDemo extends AdCreationActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TextView)findViewById(R.id.title_AdCreation_editText)).setText("Cool Ad");
        ((TextView)findViewById(R.id.street_AdCreation_editText)).setText("Funny Street");
        ((TextView)findViewById(R.id.number_AdCreation_ediText)).setText("1A");
        ((TextView)findViewById(R.id.npa_AdCreation_editText)).setText("1000");
        ((TextView)findViewById(R.id.city_AdCreation_editText)).setText("Lausanne");
        ((TextView)findViewById(R.id.price_AdCreation_editText)).setText("1234");
        ((TextView)findViewById(R.id.description_AdCreation_editText)).setText("Welcome to Appart !");

        Uri simplePictureUri = Uri.parse("file:///android_asset/Ads/fake_ad_1.jpg");
        Uri panoramaUri = Uri.parse("file:///android_asset/panorama_test.jpg");
        List<Uri> picturesUris = Arrays.asList(simplePictureUri, simplePictureUri, simplePictureUri);
        List<Uri> panoramasUris = Arrays.asList(panoramaUri, panoramaUri, panoramaUri);

        this.fillHorizontalViewWithPictures(findViewById(R.id.pictures_AdCreation_linearLayout), picturesUris);
        this.fillHorizontalViewWithPictures(findViewById(R.id.panorama_AdCreation_linearLayout), panoramasUris);

    }
}