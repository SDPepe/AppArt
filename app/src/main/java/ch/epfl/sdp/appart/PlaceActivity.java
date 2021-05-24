package ch.epfl.sdp.appart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import ch.epfl.sdp.appart.place.PlaceService;

public class PlaceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    @Inject
    public PlaceService placeService;

    private static final List<String> FIELDS = Arrays.asList("select one", "mall", "grocery", "gym");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_place_activity);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, FIELDS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}