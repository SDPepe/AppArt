package ch.epfl.sdp.appart.database.local;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import kotlin.NotImplementedError;

@Singleton
public class MockLocalDatabase implements LocalDatabaseService {

    Map<String, User> users;
    Ad ad;
    List<Card> cards = new ArrayList<>();
    private User currentUser = null;

    public MockLocalDatabase() {
        users = new HashMap<>();
        User vetterli = new AppUser("vetterli-id", "veterli@epfl.ch");
        vetterli.setName("Martin Vetterli");
        vetterli.setAge(40);
        vetterli.setGender("MALE");
        vetterli.setPhoneNumber("0777777777");
        users.put("vetterli-id", vetterli);
        User unknown = new AppUser("unknown", "unknown@email.com");
        unknown.setName("Unknown");
        unknown.setPhoneNumber("0000000000");
        users.put("unknown", unknown);

        List<String> picturesReferences = Arrays.asList(
                "fake_ad_1.jpg",
                "fake_ad_2.jpg",
                "fake_ad_3.jpg",
                "fake_ad_4.jpg",
                "fake_ad_5.jpg"
        );
        ad = new Ad.AdBuilder()
                .withTitle("EPFL")
                .withPrice(100000)
                .withPricePeriod(PricePeriod.MONTH)
                .withStreet("Station 18").withCity("1015 Lausanne")
                .withAdvertiserName("Martin Vetterli")
                .withAdvertiserId("vetterli-id")
                .withDescription("Ever wanted the EPFL campus all for yourself?")
                .withPicturesReferences(picturesReferences)
                .withPanoramaReferences(picturesReferences) //put the pictures since its mocked
                .hasVRTour(false)
                .build();

        cards.add(new Card("1111", "unknown", "unknown", "Lausanne", 1000, PricePeriod.MONTH , "apart_fake_image_1.jpeg"));
        cards.add(new Card("2222", "unknown2", "vetterli-id", "Lausanne", 1000, PricePeriod.MONTH,
                "apart_fake_image_1" +
                ".jpeg"));
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public CompletableFuture<Void> writeCompleteAd(String adId, String cardId, Ad ad, User user,
                                                   List<Bitmap> adPhotos, List<Bitmap> panoramas,
                                                   Bitmap profilePic) {
        throw new NotImplementedError();
    }

    @Override
    public CompletableFuture<List<Card>> getCards() {
        CompletableFuture<List<Card>> result = new CompletableFuture<>();
        result.complete(cards);
        return result;
    }

    @Override
    public CompletableFuture<Ad> getAd(String adId) {
        CompletableFuture<Ad> result = new CompletableFuture<>();
        result.complete(ad);
        return result;
    }

    @Override
    public CompletableFuture<User> getUser(String wantedUserID) {
        CompletableFuture<User> result = new CompletableFuture<>();
        if (users.containsKey(wantedUserID))
            result.complete(users.get(wantedUserID));
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
        this.currentUser = currentUser;
        return CompletableFuture.completedFuture(null);
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
