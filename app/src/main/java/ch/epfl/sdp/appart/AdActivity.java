package ch.epfl.sdp.appart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import javax.inject.Inject;

import ch.epfl.sdp.appart.ad.AdViewModel;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoader;
import ch.epfl.sdp.appart.login.LoginService;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * This class manages the UI of the ad.
 */
@AndroidEntryPoint
public class AdActivity extends ToolbarActivity {

    @Inject
    DatabaseService database;
    @Inject
    LoginService login;
    String adId;

    private String advertiserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);
        AdViewModel mViewModel = new ViewModelProvider(this).get(AdViewModel.class);


        Toolbar toolbar = findViewById(R.id.account_Ad_toolbar);
        setSupportActionBar(toolbar);

        mViewModel.getTitle().observe(this, this::updateTitle);
        mViewModel.getPhotosRefs().observe(this, this::updatePhotos);
        mViewModel.getAddress().observe(this, this::updateAddress);
        mViewModel.getPrice().observe(this, this::updatePrice);
        mViewModel.getDescription().observe(this, this::updateDescription);
        mViewModel.getAdvertiser().observe(this, this::updateAdvertiser);
        mViewModel.getAdvertiserId().observe(this, this::updateAdvertiserId);

        // if activity opened by adcreation, load the last ad created by the user,
        // otherwise load the id passed by scrollingactivity
        /*
        if (getIntent().getBooleanExtra("fromAdCreation", false)) {
            List<String> adIds = login.getCurrentUser().getAdsIds();
            mViewModel.initAd(adIds.get(adIds.size() - 1));
        } else {
            mViewModel.initAd(getIntent().getStringExtra("adID"));
        }
        */
        adId = getIntent().getStringExtra("adID");
        mViewModel.initAd(getIntent().getStringExtra("cardID"));
    }

    private void updateTitle(String title) {
        TextView titleView = findViewById(R.id.title_Ad_textView);
        setIfNotNull(titleView, title);
    }

    private void updatePhotos(List<String> references) {
        LinearLayout horizontalLayout = findViewById(R.id.horizontal_children_Ad_linearLayout);
        horizontalLayout.removeAllViews();

        for (int i = 0; i < references.size(); i++) {
            LayoutInflater inflater =
                    (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.photo_layout, (ViewGroup) null);
            ImageView photo = myView.findViewById(R.id.photo_Photo_imageView);
            String sep = FirebaseLayout.SEPARATOR;
            database.accept(new GlideImageViewLoader(this, photo,
                    FirebaseLayout.ADS_DIRECTORY + sep + adId + sep + references.get(i)));
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

    private void updateAddress(String address) {
        TextView addressView = findViewById(R.id.address_field_Ad_textView);
        setIfNotNull(addressView, address);
    }

    private void updatePrice(String price) {
        TextView priceView = findViewById(R.id.price_field_Ad_textView);
        setIfNotNull(priceView, price);
    }

    private void updateDescription(String description) {
        TextView descriptionView = findViewById(R.id.description_field_Ad_textView);
        setIfNotNull(descriptionView, description);
    }

    private void updateAdvertiser(String username) {
        TextView usernameView = findViewById(R.id.user_field_Ad_textView);
        setIfNotNull(usernameView, username);
    }

    private void updateAdvertiserId(String advertiserId) {
        this.advertiserId = advertiserId;
    }

    private void setIfNotNull(TextView view, String content) {
        if (content == null) {
            view.setText(R.string.loadingTextAdActivity);
        } else {
            view.setText(content);
        }
    }

    /**
     * Method called when the device back button is pressed.
     * <p>
     * It goes back to the scrolling activity.
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Method called when you want open the contact info.
     *
     * @param view
     */
    public void openContactInfo(View view) {
        Intent intent = new Intent(this, SimpleUserProfileActivity.class);
        intent.putExtra("advertiserId", this.advertiserId);
        startActivity(intent);
    }

    /**
     * Method called when you want open the virtual tour.
     *
     * @param view
     */
    public void openVirtualTour(View view) {
        Intent intent = new Intent(this, PanoramaActivity.class);
        startActivity(intent);
    }

    public void onSeeLocationClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        TextView addressView = findViewById(R.id.address_field_Ad_textView);
        intent.putExtra(getString(R.string.intentLocationForMap), addressView.getText().toString());
        startActivity(intent);
    }

}