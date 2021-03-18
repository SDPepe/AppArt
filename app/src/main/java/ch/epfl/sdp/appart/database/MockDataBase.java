package ch.epfl.sdp.appart.database;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.glide.visitor.GlideLoaderVisitor;
import ch.epfl.sdp.appart.scrolling.ad.Ad;
import ch.epfl.sdp.appart.scrolling.card.Card;

public class MockDataBase implements Database {

    private final List<Card> cards = new ArrayList<>();
    private final Ad ad;

    public MockDataBase() {
        cards.add(new Card("unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));
        cards.add(new Card("unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));
        cards.add(new Card("unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));
        cards.add(new Card("unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));
        cards.add(new Card("unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));

        List<String> refs = new ArrayList<>();
        refs.add("file:///android_asset/fake_ad_1.jpg");
        refs.add("file:///android_asset/fake_ad_2.jpg");
        refs.add("file:///android_asset/fake_ad_3.jpg");
        refs.add("file:///android_asset/fake_ad_4.jpg");
        refs.add("file:///android_asset/fake_ad_5.jpg");
        ad = new Ad("EPFL", "100'000 / mo", "Station 18, 1015 Lausanne",
                "vetterli-id", "Ever wanted the EPFL campus all for yourself?",
                refs);
    }

    @Override
    public CompletableFuture<List<Card>> getCards() {
        CompletableFuture<List<Card>> result = new CompletableFuture<>();
        result.complete(cards);
        return result;
    }

    @Override
    public CompletableFuture<String> putCard(Card card) {
        CompletableFuture<String> result = new CompletableFuture<>();
        cards.add(card);
        result.complete(card.getId());
        return result;
    }

    @Override
    public CompletableFuture<Boolean> updateCard(Card card) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        if (cards.contains(card)) {
            cards.set(cards.indexOf(card), card);
            result.complete(true);
        } else {
            result.complete(false);
        }
        return result;
    }

    @Override
    public CompletableFuture<Ad> getAd(String id) {
        CompletableFuture<Ad> result = new CompletableFuture<>();
        result.complete(ad);
        return result;
    }

    @Override
    public void accept(GlideLoaderVisitor visitor) {
        visitor.visit(this);
    }

}
