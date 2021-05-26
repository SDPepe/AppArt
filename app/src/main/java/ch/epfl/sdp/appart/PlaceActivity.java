package ch.epfl.sdp.appart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.place.PlaceOfInterest;
import ch.epfl.sdp.appart.place.PlaceService;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlaceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    @Inject
    public PlaceService placeService;

    private final static int DEFAULT_POSITION = 0;
    private int currentSelectedIndex = DEFAULT_POSITION;

    private static final List<String> FIELDS = Arrays.asList("select one", "shop", "bakery", "gym", "dentist", "drugstore", "restaurant", "university", "library");

    private List<Pair<PlaceOfInterest, Float>> currentSelectedPlaces;
    private HashMap<String, List<Pair<PlaceOfInterest, Float>>> selectionCache = new HashMap<>();
    private Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_place_activity);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, FIELDS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //here we will get the user location from the intents
        userLocation = new Location(6.635510, 46.781170);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentSelectedIndex = position;
        if (position == DEFAULT_POSITION) return;

        String type = FIELDS.get(position);
        //here get the type in the list
        if (selectionCache.containsKey(type)) {
            currentSelectedPlaces = selectionCache.get(type);
        }

        CompletableFuture<List<Pair<PlaceOfInterest, Float>>> queriedPlaces =
            placeService.getNearbyPlacesWithDistances(userLocation, 100, type, 20);

        queriedPlaces.thenAccept(pairs -> {
            currentSelectedPlaces = pairs;
        });

        //maybe we don't do anything if the request fail

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        int i = 0;
    }
}