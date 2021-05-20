package ch.epfl.sdp.appart;

import android.graphics.Bitmap;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;

public class MockLocalDatabase implements LocalDatabaseService {
    @Override
    public User getCurrentUser() {
        return null;
    }

    @Override
    public CompletableFuture<Void> writeCompleteAd(String adId, String cardId, Ad ad, User user,
                                                   List<Bitmap> adPhotos, List<Bitmap> panoramas,
                                                   Bitmap profilePic) {
        return null;
    }

    @Override
    public CompletableFuture<List<Card>> getCards() {
        return null;
    }

    @Override
    public CompletableFuture<Ad> getAd(String adId) {
        return null;
    }

    @Override
    public CompletableFuture<User> getUser(String wantedUserID) {
        return null;
    }

    @Override
    public void cleanFavorites() {

    }

    @Override
    public void removeCard(String cardId) {

    }

    @Override
    public CompletableFuture<Void> setCurrentUser(User currentUser, Bitmap profilePic) {
        return null;
    }

    @Override
    public User loadCurrentUser() {
        return null;
    }

    @Override
    public void clearCurrentUser() {

    }

    @Override
    public CompletableFuture<List<String>> getPanoramasPaths(String adID) {
        return null;
    }
}
