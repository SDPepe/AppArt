package ch.epfl.sdp.appart.map.helper;

import android.util.Log;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.geocoding.GeocodingService;
import ch.epfl.sdp.appart.location.place.Place;
import ch.epfl.sdp.appart.location.place.address.AddressFactory;
import ch.epfl.sdp.appart.location.place.locality.LocalityFactory;
import ch.epfl.sdp.appart.map.MapService;
import ch.epfl.sdp.appart.scrolling.card.Card;

public class MapFrontendHelper {

    private static final Pattern extractPostalCodePattern =
            Pattern.compile("\\d+");
    /**
     * This corresponds to the maximum distance from the current user
     * position at which apartments get drawn on the map.
     */
    private static final Float MAX_DISTANCE = 50_000.0f;

    public static CompletableFuture<List<Card>> retrieveCards(DatabaseService databaseService) {
        CompletableFuture<List<Card>> futureCards =
                databaseService.getCards();
        futureCards.exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
        return futureCards;
    }

    public static void centerOnCurrentLocation(MapService mapService,
                                               Location currentLocation) {
        if (currentLocation != null) {
            mapService.zoomOnPosition(currentLocation,
                    12.0f);
        }
    }

    public static CompletableFuture<Ad> retrieveAd(DatabaseService databaseService, String adId) {
        CompletableFuture<Ad> futureAd = databaseService.getAd(adId);
        return basicExceptionally(futureAd);
    }

    private static <T> CompletableFuture<T> basicExceptionally(CompletableFuture<T> future) {
        future.exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
        return future;
    }

    public static Place getPlaceFromAdLocation(Ad ad) {

        Matcher extractPostalCodeMatcher =
                extractPostalCodePattern.matcher(ad.getCity());
        Place place;
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
            place = LocalityFactory.makeLocality(ad.getCity());
        }
        return place;
    }

    public static void addMarker(CompletableFuture<Ad> futureAd,
                                 GeocodingService geocodingService,
                                 Location currentLocation,
                                 MapService mapService, Card card) {
        futureAd.thenAccept(ad -> {
            Place place;
            try {
                place = MapFrontendHelper.getPlaceFromAdLocation(ad);
            } catch (Exception e) {
                return;
            }

            geocodingService.getLocation(place).thenAccept(adLoc -> {
                boolean addMarker =
                        currentLocation == null || geocodingService.getDistanceSync(adLoc, currentLocation) < MAX_DISTANCE;
                if (addMarker) {
                    mapService.addMarker(adLoc, card,
                            false, card.getCity());
                }
            }).exceptionally(e -> {
                Log.d("MAP_HELPER", "Failed to get location");
                return null;
            });
        });
    }
}
