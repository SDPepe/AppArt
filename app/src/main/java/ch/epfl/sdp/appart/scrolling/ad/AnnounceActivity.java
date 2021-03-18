package ch.epfl.sdp.appart.scrolling.ad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.util.List;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.Database;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.scrolling.ScrollingViewModel;
import ch.epfl.sdp.appart.vtour.VirtualTourActivity;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AnnounceActivity extends AppCompatActivity {

    @Inject
    Database database;
    private AnnounceViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);
        mViewModel = new ViewModelProvider(this).get(AnnounceViewModel.class);
        mViewModel.initAd();

        mViewModel.getTitle().observe(this, this::updateTitle);
        mViewModel.getPhotosRefs().observe(this, this::updatePhotos);
        mViewModel.getAddress().observe(this, this::updateAddress);
        mViewModel.getPrice().observe(this, this::updatePrice);
        mViewModel.getDescription().observe(this, this::updateDescription);
        mViewModel.getAdvertiser().observe(this, this::updateAdvertiser);

        initAdContent();
    }

    private void updateTitle(String title){}

    private void updatePhotos(List<String> references){
        // TODO use GlideLoaderVisitor to query cards from list of references
    }

    private void updateAddress(String address){}

    private void updatePrice(String price){}

    private void updateDescription(String description){}

    private void updateAdvertiser(String username){}

    private void initAdContent(){
        LinearLayout horizontalLayout = findViewById(R.id.horChildren);
        for(int i = 0; i < 5; i++) {
            LayoutInflater inflater =(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.photo_layout, null);
            ImageView photo = myView.findViewById(R.id.photoImageView);
            Glide.with(this)
                    .load(Uri.parse("file:///android_asset/fake_ad_" + (i+1) + ".jpg"))
                    .into(photo);
            horizontalLayout.addView(myView);
            if (i != 4){
                Space hspacer = new Space(this);
                hspacer.setLayoutParams(new ViewGroup.LayoutParams(
                        8,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));
                horizontalLayout.addView(hspacer);
            }
        }
    }

    public void goBack(View view) {
        finish();
    }

    public void openContactInfo(View view){
        DialogFragment contactFrag = new ContactInfoDialogFragment();
        //contactFrag.getView().setBackgroundColor(Color.TRANSPARENT);
        contactFrag.show(getSupportFragmentManager(), "contact dialog");
    }

    public void openVirtualTour(View view){
        Intent intent = new Intent(this, VirtualTourActivity.class);
        startActivity(intent);
    }

}