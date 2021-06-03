package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.LocationService;
import ch.epfl.sdp.appart.location.geocoding.GeocodingService;
import ch.epfl.sdp.appart.location.place.Place;
import ch.epfl.sdp.appart.location.place.address.AddressFactory;
import ch.epfl.sdp.appart.map.ApartmentInfoWindow;
import ch.epfl.sdp.appart.map.MapService;
import ch.epfl.sdp.appart.map.helper.MapFrontendHelper;
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

    AlertDialog onFailedLocalizationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alertDialogMessage);
        builder.setPositiveButton(R.string.alertDialogButton,
                (dialogInterface, i) -> {
            onFailedLocalizationDialog.dismiss();
            finish();
        });
        onFailedLocalizationDialog = builder.create();

        PermissionRequest.askForLocationPermission(this, () -> {
            Log.d("PERMISSION", "Location permission granted");
            setupMap();
        }, () -> {
            Log.d("PERMISSION", "Refused");
            finish();
        }, () -> Log.d("PERMISSION", "Popup"));

    }

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
                    () -> {
                        Place place;
                        try {
                            place = AddressFactory.makeAddress(address);
                        } catch (Exception e) {
                            handleFailedLocalization(e);
                            return;
                        }
                        CompletableFuture<Location> futureLocation =
                                geocodingService.getLocation(place);
                        futureLocation.thenAccept(location -> mapService.addMarker(location, null, true, "AddressMarker"))
                                .exceptionally(e -> {
                                    e.printStackTrace();
                                    return null;
                                });
                        futureLocation.exceptionally(e -> {
                            handleFailedLocalization(e);
                            return null;
                        });
                    };
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
            onMapReadyCallback = buildOnMapReadyCallbackMain();
        }

        mapService.setOnReadyCallback(onMapReadyCallback);
        mapFragment.getMapAsync(mapService);

    }


    private Runnable buildOnMapReadyCallbackMain() {
        return () -> {
            CompletableFuture<List<Card>> futureCards =
                    MapFrontendHelper.retrieveCards(databaseService);
            CompletableFuture<Location> futureCurrentLocation =
                    locationService.getCurrentLocation();
            CompletableFuture.allOf(futureCards,
                    futureCurrentLocation).thenAccept(arg -> {
                Location currentLocation =
                        futureCurrentLocation.join();
                List<Card> cards = futureCards.join();

                MapFrontendHelper.centerOnCurrentLocation(mapService,
                        currentLocation);

                cards.parallelStream().forEach(card -> {

                    CompletableFuture<Ad> futureAd =
                            MapFrontendHelper.retrieveAd(databaseService,
                                    card.getAdId());
                    MapFrontendHelper.addMarker(futureAd, geocodingService,
                            currentLocation, mapService, card);
                });

            }).exceptionally(e -> {
                Log.d("MAP", "Failed to retrieve cards and current lcoation");
                return null;
            });
        };
    }

    private void handleFailedLocalization(Throwable e) {
        e.printStackTrace();
        onFailedLocalizationDialog.show();
    }
}

