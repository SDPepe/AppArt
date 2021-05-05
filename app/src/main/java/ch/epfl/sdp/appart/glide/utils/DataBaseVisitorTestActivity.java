package ch.epfl.sdp.appart.glide.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;

import javax.inject.Inject;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.database.DatabaseService;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DataBaseVisitorTestActivity extends AppCompatActivity {

    @Inject
    DatabaseService database;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_data_base_visitor_test);


    }

    public Bitmap getBitmap() {
        return bitmap;
    }

}