package ch.epfl.sdp.appart.hilt;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.sdp.appart.Database;
import ch.epfl.sdp.appart.R;
import dagger.hilt.android.AndroidEntryPoint;

import android.os.Bundle;

import javax.inject.Inject;

@AndroidEntryPoint
public class HiltTestActivity extends AppCompatActivity {

    @Inject
    Database databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hilt_test);
    }
}