package ch.epfl.sdp.appart.database.local;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import kotlin.NotImplementedError;

@Singleton
public class MockLocalDatabase implements LocalDatabaseService {

    Map<String,User> users;

    public MockLocalDatabase() {
        users = new HashMap<>();
        User vetterli = new AppUser("vetterli-id", "veterli@epfl.ch");
        vetterli.setName("Martin Vetterli");
        vetterli.setAge(40);
        vetterli.setGender("MALE");
        vetterli.setPhoneNumber("0777777777");
        users.put("vetterli-id", vetterli);
    }

    @Override
    public User getCurrentUser() {
        throw new NotImplementedError();
    }

    @Override
    public CompletableFuture<Void> writeCompleteAd(String adId, String cardId, Ad ad, User user,
                                                   List<Bitmap> adPhotos, List<Bitmap> panoramas,
                                                   Bitmap profilePic) {
        throw new NotImplementedError();
    }

    @Override
    public CompletableFuture<List<Card>> getCards() {
        throw new NotImplementedError();
    }

    @Override
    public CompletableFuture<Ad> getAd(String adId) {
        throw new NotImplementedError();
    }

    @Override
    public CompletableFuture<User> getUser(String wantedUserID) {
        CompletableFuture<User> result = new CompletableFuture<>();
        if (wantedUserID.equals("vetterli-id")) result.complete(
                users.get("vetterli-id"));
        else result.completeExceptionally(new DatabaseServiceException("User not in db"));
        return result;
    }

    @Override
    public void cleanFavorites() {
        throw new NotImplementedError();
    }

    @Override
    public void removeCard(String cardId) {
        throw new NotImplementedError();
    }

    @Override
    public CompletableFuture<Void> setCurrentUser(User currentUser, Bitmap profilePic) {
        throw new NotImplementedError();
    }

    @Override
    public User loadCurrentUser() {
        throw new NotImplementedError();
    }

    @Override
    public CompletableFuture<List<String>> getPanoramasPaths(String adID) {
        throw new NotImplementedError();
    }
}
