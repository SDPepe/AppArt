package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.LocationService;
import ch.epfl.sdp.appart.location.geocoding.GeocodingService;
import ch.epfl.sdp.appart.location.place.Place;
import ch.epfl.sdp.appart.location.place.address.AddressFactory;
import ch.epfl.sdp.appart.location.place.locality.LocalityFactory;
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

    //We need this because we have fake ads that do not have a real address
    private static final String FAILED_ADDR = "failedAddr";

    /**
     * This corresponds to the maximum distance from the current user
     * position at which apartments get drawn on the map.
     */
    private static final Float MAX_DISTANCE = 50_000.0f;

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
                    () -> geocodingService.getLocation(LocalityFactory.makeLocality(address)).
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
                CompletableFuture<List<Card>> futureCards =
                        databaseService.getCards();
                futureCards.exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
                CompletableFuture<Location> futureCurrentLocation =
                        locationService.getCurrentLocation();
                CompletableFuture<Stream<Pair<Card, Location>>> filteredAds =
                        CompletableFuture.allOf(futureCards,
                                futureCurrentLocation).thenCompose(arg -> {
                            Location currentLocation =
                                    futureCurrentLocation.join();
                            List<Card> cards = futureCards.join();
                            mapService.zoomOnPosition(currentLocation, 12.0f);
                            List<CompletableFuture<Pair<Card, Location>>> adsAndLocs =
                                    cards.parallelStream().map(card -> databaseService.getAd(card.getAdId()).thenCompose(ad -> {
                                        String city = ad.getCity();
                                        Matcher extractPostalCodeMatcher =
                                                Pattern.compile("\\d+").matcher(city);
                                        Place place;
                                        try {
                                            if (extractPostalCodeMatcher.find()) {
                                                String postalCode =
                                                        extractPostalCodeMatcher.group();
                                                String locality =
                                                        ad.getCity().replaceAll(
                                                                "\\d" +
                                                                        "+",
                                                                "");
                                                place = AddressFactory.makeAddress(ad.getStreet()
                                                        , postalCode, locality);

                                            } else {
                                                place = LocalityFactory.makeLocality(city);
                                            }
                                        } catch (Exception e) {
                                            return CompletableFuture.completedFuture(new Pair<>(new Card(FAILED_ADDR, "adId", "ownerID", "city", 0, "url", false), new Location()));
                                        }

                                        return geocodingService.getLocation(place).thenApply(loc -> new Pair<>(card, loc));

                                    })).collect(Collectors.toList());
                            CompletableFuture<Stream<Pair<Card, Location>>> futureAdsAndLocs = CompletableFuture.allOf(adsAndLocs.toArray(new CompletableFuture[0]))
                                    .thenApply(data -> adsAndLocs.parallelStream().map(CompletableFuture::join));
                            futureAdsAndLocs.exceptionally(e -> {
                                e.printStackTrace();
                                return null;
                            });
                            return
                                    futureAdsAndLocs.thenApply(adAndLocStream -> adAndLocStream.filter(adAndLoc -> {
                                        Float distance =
                                                geocodingService.getDistanceSync(adAndLoc.second, currentLocation);
                                        return distance < MAX_DISTANCE && !adAndLoc.first.getId().equals(FAILED_ADDR);
                                    }));

                        });
                filteredAds.thenAcceptAsync(adsAndLocs -> adsAndLocs.forEach(adAndLoc -> {
                    mapService.addMarker(adAndLoc.second, adAndLoc.first,
                            false, adAndLoc.first.getCity());
                }));
            };
        }

        mapService.setOnReadyCallback(onMapReadyCallback);
        mapFragment.getMapAsync(mapService);
    }
}
