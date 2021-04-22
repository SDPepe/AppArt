package ch.epfl.sdp.appart;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoader;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoaderListener;
import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.LocationService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.utils.PermissionRequest;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Inject
    DatabaseService databaseService;

    @Inject
    LocationService locationService;

    Context context;

    GoogleMap.InfoWindowAdapter windowAdapter = new GoogleMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View v = getLayoutInflater().inflate(R.layout.infowindow, null);

            TextView cityTextView = v.findViewById(R.id.city_InfoWindow_textView);
            TextView priceTextView = v.findViewById(R.id.price_InfoWindow_textView);

            ImageView photo = v.findViewById(R.id.photo_InfoWindow_imageView);

            Card card = (Card) marker.getTag();

            if(card.getCity() != null) {
                cityTextView.setText(card.getCity());
            }

            priceTextView.setText(card.getPrice() + " CHF");
            if(card.getImageUrl() != null) {
                databaseService.accept(new GlideImageViewLoaderListener(context, photo, "Cards/" + card.getImageUrl(), new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                Target<Drawable> target,
                                                boolean isFirstResource) {
                        e.printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if(!dataSource.equals(DataSource.MEMORY_CACHE)) marker.showInfoWindow();
                        return false;
                    }
                }));
            }


            return v;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        this.context = getApplicationContext();
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        PermissionRequest.askForLocationPermission(this, () -> {
            Log.d("PERMISSION", "Location permission granted");
            mapFragment.getMapAsync(this);
        }, () -> {
            Log.d("PERMISSION", "Refused");
            finish();
        }, () -> {
            Log.d("PERMISSION", "Popup");
        });

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(windowAdapter);

        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e) {
            throw e;
        }

        CompletableFuture<List<Card>> futureCards = databaseService.getCards();
        futureCards.exceptionally(e -> {
            Log.d("EXCEPTION_DB", e.getMessage());
            return null;
        });

        futureCards.thenAccept(cards -> {
            for(Card card : cards) {
                Location apartmentLoc = locationService.getLocationFromName(card.getCity());
                Marker cardMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(apartmentLoc.latitude, apartmentLoc.longitude)).title(card.getCity()));
                cardMarker.setTag(card);

            }
        });

    }
}