package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.CompletableFuture;

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

        // init buttons
        Button confirmButton = findViewById(R.id.confirm_AdCreation_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAd();
            }
        });
        Button addPhotoButton = findViewById(R.id.addPhoto_AdCreation_button);
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     *
     * @param menu The options menu in which you place your items.
     * @return boolean return true for the menu to be displayed
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_toolbar, menu);
        return true;
    }


    private void createAd(){
        // TODO call viewmodel to create ad
        /* CompletableFuture<Boolean> result = mVIewModel.confirmCreation();
        result.thenAccept(completed -> {
           if (completed){
               finish();
           } else {
               Snackbar.make(findViewById(R.id.horizontal_AdCreation_scrollView),
                       "Here's a Snackbar", Snackbar.LENGTH_LONG)
                       .setAction("Action", null).show();
           }
        });*/
    }

    private void takePhoto(){
        // TODO save photos path to VM photosRefs
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
}
