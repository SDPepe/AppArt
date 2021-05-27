package ch.epfl.sdp.appart;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.favorites.FavoriteViewModel;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.scrolling.card.CardAdapter;
import ch.epfl.sdp.appart.utils.DatabaseSync;
import dagger.hilt.android.AndroidEntryPoint;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;

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
        recyclerView.setAdapter(new CardAdapter(this, database, new ArrayList<>()));
        recyclerView.setHasFixedSize(true); //use for performance if card dims does
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
    private void updateList(List<Card> ls) {
        recyclerView.setAdapter(new CardAdapter(this, database, ls));
    }

    /**
     * Gets all favorite ads and their images from the database and tries to save the new data
     * locally.
     */
    private void updateFavsLocally() {
        List<Card> favs = mViewModel.getFavorites().getValue();
        for (int i = 0; i < favs.size(); i++) {
            Card card = favs.get(i);
            CompletableFuture<Ad> adRes = database.getAd(card.getAdId());
            adRes.thenAccept(ad -> {
                List<CompletableFuture<Bitmap>> imgBitmapRes = DatabaseSync.fetchImages(this,
                        database, card.getAdId(), ad.getPhotosRefs());
                CompletableFuture<Void> allOfImages =
                        CompletableFuture.allOf(imgBitmapRes
                                .toArray(new CompletableFuture[imgBitmapRes.size()]));
                allOfImages.thenAccept(ignoreRes -> {
                    List<Bitmap> imgs = imgBitmapRes.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                    DatabaseSync.saveFavoriteAd(this, database, localdb, card.getId(),
                            card.getAdId(), ad, imgs)
                            .thenAccept(r -> Log.d("FAVORITE", "Ad saved locally"));
                });
                allOfImages.exceptionally(e -> {
                    Log.d("FAVORITE", "Failed to retrieve ad images");
                    return null;
                });
            });
        }
    }


}