package ch.epfl.sdp.appart.hilt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import ch.epfl.sdp.appart.Database;
import ch.epfl.sdp.appart.R;
import dagger.hilt.android.AndroidEntryPoint;

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