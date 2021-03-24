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

import ch.epfl.sdp.appart.scrolling.CameraActivity;
import com.bumptech.glide.Glide;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.Database;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.vtour.VirtualTourActivity;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AnnounceActivity extends AppCompatActivity {

    @Inject
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);

        initAdContent();
    }

    private void initAdContent(){
        // TODO update with database query
        TextView title = findViewById(R.id.titleField);
        title.setText(R.string.mock_title);
        LinearLayout verticalLayout = findViewById(R.id.verChildren);
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
        setFields();
    }

    private void setFields(){
        TextView addressField = findViewById(R.id.addressField);
        addressField.setText(R.string.mock_address);
        TextView priceField = findViewById(R.id.priceField);
        priceField.setText(R.string.mock_price);
        TextView descriptionField = findViewById(R.id.descriptionField);
        descriptionField.setText(getString(R.string.mock_description));
        TextView userField = findViewById(R.id.userField);
        userField.setText(R.string.mock_user);
    }

    public void goBack(View view){
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

    public void openCamera(View view){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

}