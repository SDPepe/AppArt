package ch.epfl.sdp.appart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import javax.inject.Inject;

import ch.epfl.sdp.appart.place.PlaceService;

public class PlaceActivity extends AppCompatActivity {

    @Inject
    public PlaceService placeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
    }


}