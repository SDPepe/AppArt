package ch.epfl.sdp.appart.scrolling.ad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.sdp.appart.R;

public class AnnounceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);

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
}