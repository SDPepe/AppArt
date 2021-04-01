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
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.List;
import javax.inject.Inject;
import ch.epfl.sdp.appart.ad.AdViewModel;
import ch.epfl.sdp.appart.ad.ContactInfoDialogFragment;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoader;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * This class manages the UI of the ad.
 */
@AndroidEntryPoint
public class AdActivity extends ToolbarActivity {

    @Inject
    DatabaseService database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);
        AdViewModel mViewModel = new ViewModelProvider(this).get(AdViewModel.class);


        Toolbar toolbar = (Toolbar) findViewById(R.id.account_Ad_toolbar);
        setSupportActionBar(toolbar);

        mViewModel.getTitle().observe(this, this::updateTitle);
        mViewModel.getPhotosRefs().observe(this, this::updatePhotos);
        mViewModel.getAddress().observe(this, this::updateAddress);
        mViewModel.getPrice().observe(this, this::updatePrice);
        mViewModel.getDescription().observe(this, this::updateDescription);
        mViewModel.getAdvertiser().observe(this, this::updateAdvertiser);

        mViewModel.initAd(getIntent().getStringExtra("adID"));
    }

    private void updateTitle(String title) {
        TextView titleView = findViewById(R.id.title_Ad_textView);
        if (title != null) {
            titleView.setText(title);
        } else {
            titleView.setText(R.string.default_loading);
        }
    }

    private void updatePhotos(List<String> references) {
        LinearLayout horizontalLayout = findViewById(R.id.horizontal_children_Ad_linearLayout);
        horizontalLayout.removeAllViews();

        for (int i = 0; i < references.size(); i++) {
            LayoutInflater inflater =
                    (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.photo_layout, null);
            ImageView photo = myView.findViewById(R.id.photo_Photo_imageView);
            database.accept(new GlideImageViewLoader(this, photo,
                    references.get(i)));
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
        if (address != null) {
            addressView.setText(address);
        } else {
            addressView.setText(R.string.default_loading);
        }
    }

    private void updatePrice(String price) {
        TextView priceView = findViewById(R.id.price_field_Ad_textView);
        if (price != null) {
            priceView.setText(price);
        } else {
            priceView.setText(R.string.default_loading);
        }
    }

    private void updateDescription(String description) {
        TextView descriptionView = findViewById(R.id.description_field_Ad_textView);
        if (description != null) {
            descriptionView.setText(description);
        } else {
            descriptionView.setText(R.string.default_loading);
        }
    }

    private void updateAdvertiser(String username) {
        TextView usernameView = findViewById(R.id.user_field_Ad_textView);
        if (username != null) {
            usernameView.setText(username);
        } else {
            usernameView.setText(R.string.default_loading);
        }
    }

    /**
     * Method called when the activity is done and should be closed.
     *
     * @param view
     */
    public void goBack(View view) {
        finish();
    }

    /**
     * Method called when you want open the contact info.
     *
     * @param view
     */
    public void openContactInfo(View view) {
        DialogFragment contactFrag = ContactInfoDialogFragment.newInstance();
        //contactFrag.getView().setBackgroundColor(Color.TRANSPARENT);
        contactFrag.show(getSupportFragmentManager(), "contact dialog");
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

    /**
     * Method called when you want open the camera.
     *
     * @param view
     */
    public void openCamera(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

}