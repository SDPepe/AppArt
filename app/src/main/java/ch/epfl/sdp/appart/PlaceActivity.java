package ch.epfl.sdp.appart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.location.GoogleGeocodingService;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.address.Address;
import ch.epfl.sdp.appart.location.address.AddressFactory;
import ch.epfl.sdp.appart.place.GooglePlaceService;
import ch.epfl.sdp.appart.place.PlaceOfInterest;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlaceActivity extends AppCompatActivity {

    @Inject
    public GooglePlaceService placeService;

    @Inject
    public GoogleGeocodingService geocodingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        placeService.initialize(this);
        Address address = AddressFactory.makeAddress("Rue du Lac 34, 1400 Yverdon-les-Bains"); //new Address("Rue du Lac 34, 1400 Yverdon-les-Bains");

        CompletableFuture<Location> locationFuture = geocodingService.getLocation(address);

        locationFuture.thenAccept(location -> {
           int i = 0;
        });

        locationFuture.exceptionally(e -> {
           return null;
        });

        CompletableFuture<Address> addressFuture = geocodingService.getAddress(new Location(6.534650, 46.765120));

        addressFuture.thenAccept(a -> {
            int i = 0;
        });
        addressFuture.exceptionally(e -> {
            return null;
        });

        CompletableFuture<List<PlaceOfInterest>> places
                = placeService.getNearbyPlaces(new Location(6.639180, 46.779770), 1000, "shop", 10);


        places.thenAccept(p -> {
            int i = 0;
        }).exceptionally(e -> {
            return null;
        });


    }
}