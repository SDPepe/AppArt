package ch.epfl.sdp.appart.map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ch.epfl.sdp.appart.location.Location;

public class GoogleMapWrapper implements OnMapReadyCallback {

    private GoogleMap map;

    private final GoogleMap.InfoWindowAdapter infoWindowAdapter;
    private Runnable onReadyCallback = null;

    public GoogleMapWrapper(GoogleMap.InfoWindowAdapter infoWindowAdapter) {
        this.infoWindowAdapter = infoWindowAdapter;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(infoWindowAdapter != null) {
            map.setInfoWindowAdapter(infoWindowAdapter);
        }

        try {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e) {
            throw e;
        }

       /* CompletableFuture<List<Card>> futureCards = databaseService
       .getCards();
        futureCards.exceptionally(e -> {
            Log.d("EXCEPTION_DB", e.getMessage());
            return null;
        });

        futureCards.thenAccept(cards -> {
            for(Card card : cards) {
                Location apartmentLoc = locationService.getLocationFromName
                (card.getCity());
                Marker cardMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(apartmentLoc.latitude, apartmentLoc
                .longitude)).title(card.getCity()));
                cardMarker.setTag(card);

            }
        });*/
        if(onReadyCallback != null) {
            onReadyCallback.run();
        }
    }

    public void addMarker(Location location, Object tag, boolean centerOnMarker) {
        if (location == null) {
            throw new IllegalArgumentException();
        }
        Marker cardMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(location.latitude, location
                        .longitude)));

        cardMarker.setTag(tag);

        if(centerOnMarker) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(cardMarker.getPosition(), 12.0f));
        }
    }

    public void setOnReadyCallback(Runnable onReadyCallback) {
        //No need to check for null here, the check line 56 makes more sense
        this.onReadyCallback = onReadyCallback;
    }
}
