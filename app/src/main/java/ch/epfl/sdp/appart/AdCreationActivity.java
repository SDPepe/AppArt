package ch.epfl.sdp.appart;

import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdCreationActivity extends ToolbarActivity {

    @Inject
    DatabaseService database;

    // AdCreationViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adcreation);
        //mViewModel = new ViewModelProvider(this).get(AdCreationViewModel.class);
    }

    public void createAd(View view){
        // TODO call viewmodel to create ad
    }

    public void takePhoto(View view){
        // TODO open camera activity, add chosen images to linear layout
    }
}
