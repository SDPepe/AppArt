package ch.epfl.sdp.appart;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.favorites.FavoriteViewModel;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.scrolling.card.CardAdapter;
import ch.epfl.sdp.appart.user.User;
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

        removeStaleAds().thenAccept(arg -> {
            CompletableFuture<Void> initRes = mViewModel.initHome();
            initRes.exceptionally(e -> {
                Log.d("FAVORITES", "Failed to init");
                return null;
            });
            initRes.thenAccept(res -> updateFavsLocally());
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });

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

    private CompletableFuture<Void> removeStaleAds() {

        if (!DatabaseSync.areWeOnline(this)) {
            return CompletableFuture.completedFuture(null);
        }
        CompletableFuture<Void> futureRemove = new CompletableFuture<>();
        //This should never be null as this will be called only if we are online
        User currentUser = loginService.getCurrentUser();


        database.getUser(currentUser.getUserId())
                .thenCompose(currentDBUser -> database.getCards().thenApply(cards -> new Pair(currentDBUser,
                        cards)))
                .thenAccept(dbUserAndCards -> {
                    User currentDBUser = (User) dbUserAndCards.first;
                    List<Card> cards = (List<Card>) dbUserAndCards.second;
                    Set<String> userFavIds = currentDBUser.getFavoritesIds();
                    List<String> adIds =
                            cards.parallelStream().map(card -> card.getAdId()).collect(Collectors.toList());
                    CompletableFuture<List<Card>> futureLocalCards =
                            localdb.getCards();
                    futureLocalCards.thenAccept(localCards -> {
                        for (String adId : userFavIds) {
                            if (!adIds.contains(adId)) {
                                //Update user
                                currentDBUser.removeFavorite(adId);

                                //Update local db
                                for (Card localCard : localCards) {
                                    if (localCard.getAdId().equals(adId)) {
                                        localdb.removeCard(localCard.getId());
                                    }
                                }
                            }
                        }
                        database.updateUser(currentDBUser).thenAccept(arg -> futureRemove.complete(null)).exceptionally(e -> {
                            e.printStackTrace();
                            futureRemove.completeExceptionally(e);
                            return null;
                        });
                    }).exceptionally(e -> {
                        futureRemove.completeExceptionally(e);
                        e.printStackTrace();
                        return null;
                    });
                }).exceptionally(e -> {
            e.printStackTrace();
            futureRemove.completeExceptionally(e);
            return null;
        });
        return futureRemove;
    }


}