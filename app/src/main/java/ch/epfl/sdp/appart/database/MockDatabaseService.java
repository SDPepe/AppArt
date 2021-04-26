package ch.epfl.sdp.appart.database;

import android.net.Uri;
import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.ContactInfo;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoaderVisitor;
import ch.epfl.sdp.appart.glide.visitor.GlideLoaderListenerVisitor;
import ch.epfl.sdp.appart.glide.visitor.GlideLoaderVisitor;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

/**
 * Mocked implementation of the DatabaseService to allows unit testing in a controlled environment.
 */
public class MockDatabaseService implements DatabaseService {

    private final List<Card> cards = new ArrayList<>();
    private final Ad ad;
    private final Map<String, User> users = new HashMap<>();

    public MockDatabaseService() {

        cards.add(new Card("unknown", "unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));
        cards.add(new Card("unknown", "unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));
        cards.add(new Card("unknown", "unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));
        cards.add(new Card("unknown", "unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));
        cards.add(new Card("unknown", "unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));
        cards.add(new Card("unknown", "unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));

        List<String> picturesReferences = Arrays.asList(
                "file:///android_asset/fake_ad_1.jpg",
                "file:///android_asset/fake_ad_2.jpg",
                "file:///android_asset/fake_ad_3.jpg",
                "file:///android_asset/fake_ad_4.jpg",
                "file:///android_asset/fake_ad_5.jpg"
        );

        ad = new Ad.AdBuilder()
                .withTitle("EPFL")
                .withPrice(100000)
                .withPricePeriod(PricePeriod.MONTH)
                .withStreet("Station 18").withCity("1015 Lausanne")
                .withAdvertiserId("vetterli-id")
                .withDescription("Ever wanted the EPFL campus all for yourself?")
                .withPhotosIds(picturesReferences)
                .hasVRTour(false)
                .build();

        users.put("id0", new AppUser("id0", "test0@epfl.ch"));
        users.put("id1", new AppUser("id1", "test1@epfl.ch"));
        users.put("id2", new AppUser("id2", "test2@epfl.ch"));

        /* for UserProfileActivity and SimpleUserProfileActivity testing */
        User vetterli = new AppUser("vetterli-id", "vetterli@epfl.ch");
        vetterli.setName("Martin Vetterli");
        vetterli.setAge(40);
        vetterli.setGender("MALE");
        vetterli.setPhoneNumber("0777777777");

        users.put("vetterli-id", vetterli);
        users.put("3333", new AppUser("3333", "carlo@epfl.ch"));
    }

    @NotNull
    @Override
    public CompletableFuture<List<Card>> getCards() {
        CompletableFuture<List<Card>> result = new CompletableFuture<>();
        result.complete(cards);
        return result;
    }

    @NotNull
    @Override
    public CompletableFuture<String> putCard(@NotNull Card card) {
        CompletableFuture<String> result = new CompletableFuture<>();
        cards.add(card);
        result.complete(card.getId());
        return result;
    }

    @NotNull
    @Override
    public CompletableFuture<Boolean> updateCard(@NotNull Card card) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        if (cards.contains(card)) {
            cards.set(cards.indexOf(card), card);
            result.complete(true);
        } else {
            result.complete(false);
        }
        return result;
    }

    @NotNull
    @Override
    public CompletableFuture<Ad> getAd(String id) {
        CompletableFuture<Ad> result = new CompletableFuture<>();
        result.complete(ad);
        return result;
    }

    @NotNull
    public CompletableFuture<User> getUser(String userId) {
        CompletableFuture<User> result = new CompletableFuture<>();
        result.complete(users.get(userId));
        return result;
    }

    @NotNull
    @Override
    public CompletableFuture<Boolean> putUser(User user) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        users.put(user.getUserId(), user);
        result.complete(true);
        return result;
    }

    @NotNull
    @Override
    public CompletableFuture<Boolean> updateUser(User user, Uri uri) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        if (users.containsValue(user)) {
            users.put(user.getUserId(), user);
            result.complete(true);
        } else {
            result.complete(false);
        }
        return result;
    }

    // TODO implement uriList
    @NotNull
    @Override
    public CompletableFuture<String> putAd(Ad ad, List<Uri> uriList) {
        CompletableFuture<String> result = new CompletableFuture<>();
        if (ad.getTitle().equals("failing")){
            result.completeExceptionally(new IllegalStateException());
        } else {
            result.complete("1234");
        }
        return result;
    }


    @NonNull
    @Override
    public CompletableFuture<Boolean> putImage(Uri uri, String name, String path) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        if (uri == null || name == null || path == null){
            result.completeExceptionally(new IllegalArgumentException());
        } else {
            result.complete(true);
        }
        return result;
    }

    @Override
    public CompletableFuture<Void> clearCache() {
        CompletableFuture<Void> futureClear = new CompletableFuture<>();
        futureClear.complete(null);
        return futureClear;
    }

    public void accept(GlideLoaderVisitor visitor) {
        visitor.visit(this);
    }

    public void accept(GlideBitmapLoaderVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(GlideLoaderListenerVisitor visitor) {
        visitor.visit(this);
    }
}
