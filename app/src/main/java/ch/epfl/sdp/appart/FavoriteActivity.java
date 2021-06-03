package ch.epfl.sdp.appart;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.favorites.FavoriteViewModel;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.scrolling.card.CardAdapter;
import ch.epfl.sdp.appart.utils.DatabaseSync;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoriteActivity extends ToolbarActivity {

    @Inject
    DatabaseService database;
    @Inject
    LocalDatabaseService localdb;

    private RecyclerView recyclerView;
    private FavoriteViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = findViewById(R.id.Favorites_toolbar);
        setSupportActionBar(toolbar);

        mViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        recyclerView = findViewById(R.id.recycler_favorites);
        recyclerView.setAdapter(new CardAdapter(this, database,
                new ArrayList<>()));
        recyclerView.setHasFixedSize(true); //use for performance if card
        // dims does
        // not change
        mViewModel.getFavorites().observe(this, this::updateList);

        CompletableFuture<Void> initRes = mViewModel.initHome();
        initRes.exceptionally(e -> {
            Log.d("FAVORITES", "Failed to init");
            return null;
        });
        initRes.thenAccept(res -> updateFavsLocally());
    }

    /**
     * Update the list of cards.
     *
     * @param ls a list of card.
     */
    private void updateList(Pair<List<Card>, Boolean> ls) {
        recyclerView.setAdapter(new CardAdapter(this, database, ls.first,
                false, ls.second));
    }

    /**
     * Gets all favorite ads and their images from the database and tries to
     * save the new data
     * locally.
     */
    private void updateFavsLocally() {

        //So cleanFavorites is not appropriate.
        if (DatabaseSync.areWeOnline(this)) {
            writeAdsToDisk();
        }

    }

    private void writeAdsToDisk() {
        /*
            We need this clean if we have the ability to remove or suppress ads.
            Adding a clean favorites as a preparation for the possibility to
            remove favorites. However, this should not clean the currentUser
            data, which it currently  does.
         */
        //localdb.cleanFavorites();
        List<Card> favs = mViewModel.getFavorites().getValue().first;
        for (int i = 0; i < favs.size(); i++) {
            Card card = favs.get(i);
            DatabaseSync.writeAd(database, card, this, localdb);
        }
    }


}