package ch.epfl.sdp.appart.scrolling.ad;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import ch.epfl.sdp.appart.scrolling.CameraActivity;
import java.util.List;


import javax.inject.Inject;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.ui.ToolbarActivity;
import ch.epfl.sdp.appart.user.LoginActivity;

import ch.epfl.sdp.appart.virtualtour.PanoramaGlActivity;
import ch.epfl.sdp.appart.database.Database;
import ch.epfl.sdp.appart.glide.visitor.GlideLoaderVisitorImpl;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AnnounceActivity extends ToolbarActivity {

    @Inject
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);
        AnnounceViewModel mViewModel = new ViewModelProvider(this).get(AnnounceViewModel.class);


        Toolbar toolbar = (Toolbar) findViewById(R.id.account_toolbar);
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
        TextView titleView = findViewById(R.id.titleField);
        if (title != null) {
            titleView.setText(title);
        } else {
            titleView.setText(R.string.default_loading);
        }
    }

    private void updatePhotos(List<String> references) {
        LinearLayout horizontalLayout = findViewById(R.id.horChildren);
        horizontalLayout.removeAllViews();

        for (int i = 0; i < references.size(); i++) {
            LayoutInflater inflater =
                    (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.photo_layout, null);
            ImageView photo = myView.findViewById(R.id.photoImageView);
            database.accept(new GlideLoaderVisitorImpl(this, photo,
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
        TextView addressView = findViewById(R.id.addressField);
        if (address != null) {
            addressView.setText(address);
        } else {
            addressView.setText(R.string.default_loading);
        }
    }

    private void updatePrice(String price) {
        TextView priceView = findViewById(R.id.priceField);
        if (price != null) {
            priceView.setText(price);
        } else {
            priceView.setText(R.string.default_loading);
        }
    }

    private void updateDescription(String description) {
        TextView descriptionView = findViewById(R.id.descriptionField);
        if (description != null) {
            descriptionView.setText(description);
        } else {
            descriptionView.setText(R.string.default_loading);
        }
    }

    private void updateAdvertiser(String username) {
        TextView usernameView = findViewById(R.id.userField);
        if (username != null) {
            usernameView.setText(username);
        } else {
            usernameView.setText(R.string.default_loading);
        }
    }

    public void goBack(View view) {
        finish();
    }

    public void openContactInfo(View view) {
        DialogFragment contactFrag = ContactInfoDialogFragment.newInstance();
        //contactFrag.getView().setBackgroundColor(Color.TRANSPARENT);
        contactFrag.show(getSupportFragmentManager(), "contact dialog");
    }

    public void openVirtualTour(View view){
        Intent intent = new Intent(this, PanoramaGlActivity.class);
        startActivity(intent);
    }

    public void openCamera(View view){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

}