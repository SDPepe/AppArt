package ch.epfl.sdp.appart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.configuration.ApplicationConfiguration;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabase;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.database.preferences.SharedPreferencesHelper;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.ScrollingViewModel;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.scrolling.card.CardAdapter;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
import dagger.hilt.android.AndroidEntryPoint;

import static android.widget.Toast.makeText;

/**
 * This class manages the UI for Scrolling into the add.
 */
@AndroidEntryPoint
public class ScrollingActivity extends ToolbarActivity {

    ScrollingViewModel mViewModel;

    @Inject
    DatabaseService database;

    @Inject
    LoginService loginService;

    @Inject
    LocalDatabaseService localDatabaseService;

    @Inject
    ApplicationConfiguration configuration;

    private RecyclerView recyclerView;

    private AlertDialog onBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.scrollingActivityAlertDialogMessage);
        builder.setPositiveButton(R.string.scrollingActivityAlertDialogPositiveButton, (dialogInterface, i) -> {
            loginService.signOut();
            localDatabaseService.cleanFavorites();
            SharedPreferencesHelper.clearSavedUserForAutoLogin(this);

            /*
                We can't just do finish because we want to completely reset the login activity.
                For instance, if we just use finish the progress bar still appears on the login activity
                and there is no way to hide it.
             */
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        builder.setNegativeButton(R.string.scrollingActivityAlertDialogNegativeButton, (dialogInterface, i) ->  {
        });
        onBackPressed = builder.create();

        Toolbar toolbar = findViewById(R.id.login_Scrolling_toolbar);
        setSupportActionBar(toolbar);

        mViewModel = new ViewModelProvider(this).get(ScrollingViewModel.class);
        mViewModel.initHome();

        recyclerView = findViewById(R.id.recycler_Scrolling_recyclerView);
        recyclerView.setAdapter(new CardAdapter(this, database,
                new ArrayList<>()));
        recyclerView.setHasFixedSize(true); //use for performance if card
        // dims does not change
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mViewModel.getCards().observe(this, this::updateList);

        // init floating action button
        FloatingActionButton fab =
                findViewById(R.id.newAd_Scrolling_floatingActionButton);
        fab.setOnClickListener((View view) -> onFloatingButtonAction());

        //search bar
        mViewModel.getCardsFilter().observe(this, this::updateList);
        EditText searchText =
                (EditText) findViewById(R.id.search_bar_Scrolling_editText);
        searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                mViewModel.filter(
                        ((EditText) findViewById(R.id.search_bar_Scrolling_editText)).getText().toString());
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });

        //filter
        Button filterBtn = findViewById(R.id.filter_Scrolling_button);
        filterBtn.setOnClickListener(v -> onFilterButtonAction());

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Scroll", "stopping");
    }

    /**
     * Update the list of cards.
     *
     * @param ls a list of card.
     */
    private void updateList(List<Card> ls) {
        recyclerView.setAdapter(new CardAdapter(this, database, ls));
    }

    /**
     * Opens the Ad creation activity.
     */
    private void onFloatingButtonAction() {
        //AdCreationActivity.class
        Intent intent = new Intent(this,
                configuration.demoModeSelector(AdCreationActivity.class,
                        AdCreationActivityDemo.class));
        startActivity(intent);
    }

    /**
     * Opens the Filter activity.
     */
    @SuppressWarnings("deprecation")
    private void onFilterButtonAction() {
        Intent intent = new Intent(this, FilterActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                handleResult(data);

            }
        }
    }

    private void handleResult(Intent data) {
        int size =
                data.getIntExtra(ActivityCommunicationLayout.PROVIDING_SIZE, 0);
        if (size > 0) {
            List<String> filterCardsId = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                filterCardsId.add(data.getStringExtra(
                        ActivityCommunicationLayout.PROVIDING_CARD_ID + i));
            }
            CompletableFuture<List<Card>> allCards =
                    database.getCardsById(filterCardsId);
            allCards.thenAccept(this::updateList
            ).exceptionally(e -> {
                makeText(this, "Error in loading the image, try again!",
                        Toast.LENGTH_SHORT)
                        .show();
                return null;
            });
        } else {
            mViewModel.initHome();
        }
    }

    @Override
    public void onBackPressed() {
        onBackPressed.show();
    }

}