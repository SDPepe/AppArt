package ch.epfl.sdp.appart.scrolling.ad;

import android.content.Context;
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

import com.bumptech.glide.Glide;

import ch.epfl.sdp.appart.R;

public class AnnounceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);

        initAdContent();
        initBackButton();
        initVTourButton();
        initContactButton();
    }

    private void initBackButton(){
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            // TODO async caching of ad
            finish();
        });
    }

    private void initVTourButton(){
        Button backButton = findViewById(R.id.vtourButton);
        backButton.setOnClickListener(v -> {
            // TODO open virtual tour activity
        });
    }

    private void initContactButton(){
        Button contactButton = findViewById(R.id.contactInfoButton);
        contactButton.setOnClickListener(v -> {
            // TODO open popup with user info
        });
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
        TextView addressField = findViewById(R.id.addressField);
        addressField.setText(R.string.mock_address);
        TextView priceField = findViewById(R.id.priceField);
        priceField.setText(R.string.mock_price);
        TextView descriptionField = findViewById(R.id.descriptionField);
        descriptionField.setText(getString(R.string.mock_description));
        TextView userField = findViewById(R.id.userField);
        userField.setText(R.string.mock_user);
    }

}