package ch.epfl.sdp.appart;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.database.local.LocalDatabase;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.favorites.FavoriteViewModel;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoader;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.scrolling.card.CardAdapter;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.StoragePathBuilder;
import dagger.hilt.android.AndroidEntryPoint;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import java.lang.reflect.Array;
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
    LoginService login;
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

        mViewModel.initHome()
                .exceptionally(e -> {
                    // when initHome completes exceptionally, the exception message is the string to
                    // show to user (see favoriteVM)
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return null;
                })
                .thenAccept(res -> {
                    recyclerView = findViewById(R.id.recycler_favorites);
                    recyclerView.setAdapter(new CardAdapter(this, database, new ArrayList<>()));
                    recyclerView.setHasFixedSize(true); //use for performance if card dims does
                    // not change
                    mViewModel.getFavorites().observe(this, this::updateList);
                    saveLocally();
                });
    }

    /**
     * Update the list of cards.
     *
     * @param ls a list of card.
     */
    private void updateList(List<Card> ls) {
        recyclerView.setAdapter(new CardAdapter(this, database, ls));
    }

    private void saveLocally() {
        localdb.cleanFavorites();
        List<String> adIds = new ArrayList<>();
        List<Card> cards = mViewModel.getFavorites().getValue();
        for (Card c : cards) {
            saveAd(c.getAdId(), c.getId());
        }
    }

    private void saveAd(String adId, String cardId) {
        User user = login.getCurrentUser();
        database.getAd(adId)
                .exceptionally(e -> {
                    showFailToast();
                    return null;
                })
                .thenAccept(ad -> {
                    CompletableFuture<List<Bitmap>> images = getImages(adId, ad.getPhotosRefs());
                    CompletableFuture<List<Bitmap>> panoramas = getImages(adId,
                            ad.getPanoramaReferences());
                    CompletableFuture<Bitmap> pfp = new CompletableFuture<>();
                    database.accept(new GlideBitmapLoader(this, pfp,
                            user.getProfileImagePathAndName()));
                    images.thenAcceptBoth(panoramas, (imgs, panms) -> {
                        pfp
                                .exceptionally(e -> {
                                    showFailToast();
                                    return null;
                                })
                                .thenAccept(pfpBitmap -> {
                                    localdb.writeCompleteAd(adId, cardId, ad, user, imgs, panms,
                                            pfpBitmap);
                                });
                    }).exceptionally(e -> {
                        showFailToast();
                        return null;
                    });
                });
    }

    private CompletableFuture<List<Bitmap>> getImages(String adId, List<String> imageIds) {
        List<CompletableFuture<Bitmap>> imageBitmaps = new ArrayList<>();
        for (String imageId : imageIds) {
            String path = new StoragePathBuilder()
                    .toAdsStorageDirectory()
                    .toDirectory(adId)
                    .withFile(imageId);
            CompletableFuture<Bitmap> bitmapRes = new CompletableFuture<>();
            database.accept(new GlideBitmapLoader(this, bitmapRes, path));
            imageBitmaps.add(bitmapRes);
        }
        CompletableFuture<List<Bitmap>> res = new CompletableFuture<>();
        CompletableFuture.allOf(imageBitmaps.toArray(
                new CompletableFuture[imageIds.size()]))
                .exceptionally(e -> {
                    res.completeExceptionally(e);
                    return null;
                })
                .thenAccept(ignoreRes -> {
                    res.complete(imageBitmaps.stream().map(CompletableFuture::join)
                            .collect(Collectors.toList()));
                });
        return res;
    }

    private void showFailToast() {
        Toast.makeText(this, R.string.localSaved_Favorite, Toast.LENGTH_SHORT).show();
    }
}