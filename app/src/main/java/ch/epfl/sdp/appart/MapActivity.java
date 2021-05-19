package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.location.LocationService;
import ch.epfl.sdp.appart.location.address.AddressFactory;
import ch.epfl.sdp.appart.location.geocoding.GeocodingService;
import ch.epfl.sdp.appart.map.ApartmentInfoWindow;
import ch.epfl.sdp.appart.map.MapService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.utils.PermissionRequest;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapActivity extends AppCompatActivity {

    @Inject
    DatabaseService databaseService;

    @Inject
    LocationService locationService;

    @Inject
    GeocodingService geocodingService;

    @Inject
    MapService mapService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        PermissionRequest.askForLocationPermission(this, () -> {
            Log.d("PERMISSION", "Location permission granted");
            setupMap();
        }, () -> {
            Log.d("PERMISSION", "Refused");
            finish();
        }, () -> Log.d("PERMISSION", "Popup"));

    }


    /*
        To implement the feature :
            - Set map center on user location
            - Get location from all the ads (or all the ads in the country),
            see if it is possible to get ad only
                if they satisfy a condition.
                It would be great if I could get all the ads (location only
                because if we have millions of ad this is going to be huge,
                or restrict by country for instance)
                , then transform the address into latitude and longitude,
                then maybe it is possible to ask the map object if a
                   specific location is on the map. If it is display it.
     */

    private void setupMap() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getView().setContentDescription("WAITING");

        mapService.setActivity(this);


        Runnable onMapReadyCallback;

        String address =
                getIntent().getStringExtra(getString(R.string.intentLocationForMap));

        /*
            We need to execute addMarker on the main UI thread, that's why
            the Async version of thenAccept is used with the main thread as
            an Executor.
         */
        if (address != null) {
            mapService.setMapFragment(mapFragment);
            onMapReadyCallback =
                    () -> geocodingService.getLocation(AddressFactory.makeAddress(address)).
                            thenAcceptAsync(location -> mapService.addMarker(location, null, true, null), ContextCompat.getMainExecutor(this))
                            .exceptionally(e -> {
                                e.printStackTrace();
                                return null;
                            });
        } else {

            mapService.setInfoWindow(new ApartmentInfoWindow(this,
                    databaseService));
            mapService.setOnInfoWindowClickListener(marker -> {
                Card card = (Card) marker.getTag();
                String adId = card.getAdId();
                Intent intent = new Intent(this, AdActivity.class);
                intent.putExtra("adID", adId);
                startActivity(intent);

            });

            mapService.setMapFragment(mapFragment);
            onMapReadyCallback = () -> {
                CompletableFuture<List<Card>> futureCards = databaseService
                        .getCards();
                futureCards.exceptionally(e -> {
                    Log.d("EXCEPTION_DB", e.getMessage());
                    return null;
                });

                futureCards.thenAccept(cards -> {
                    for (Card card : cards) {
                        //First filter on location of the card
                        databaseService.getAd(card.getAdId()).thenCompose(ad ->
                                geocodingService.getLocation(AddressFactory.
                                        makeAddress(ad.getStreet(),
                                                ad.getCity())))
                                .thenAcceptAsync(location ->
                                        mapService.addMarker(location
                                                , card,
                                                false, card.getCity()),
                                        ContextCompat.getMainExecutor(this));
                    }
                });
            };
        }

        mapService.setOnReadyCallback(onMapReadyCallback);
        mapFragment.getMapAsync(mapService);
    }
}

