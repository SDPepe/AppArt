package ch.epfl.sdp.appart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.place.Address;
import ch.epfl.sdp.appart.place.GooglePlaceService;
import ch.epfl.sdp.appart.place.PlaceOfInterest;
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
        Address address = new Address("Rue du Lac 34, 1400 Yverdon-les-Bains", new Location(6.639180, 46.779770));
        CompletableFuture<List<PlaceOfInterest>> places
                = placeService.getPlacesOfInterests(address, 1000, "gym");

        places.thenAccept(p -> {
            int i = 0;
        }).exceptionally(e -> {
            return null;
        });
    }
}