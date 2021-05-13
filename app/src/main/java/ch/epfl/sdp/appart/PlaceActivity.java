package ch.epfl.sdp.appart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import javax.inject.Inject;

import ch.epfl.sdp.appart.place.GooglePlaceService;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlaceActivity extends AppCompatActivity {

    @Inject
    public GooglePlaceService placeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        placeService.initialize(this);
    }
}