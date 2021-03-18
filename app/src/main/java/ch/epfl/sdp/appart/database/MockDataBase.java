package ch.epfl.sdp.appart.database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.glide.visitor.GlideLoaderVisitor;
import ch.epfl.sdp.appart.scrolling.card.Card;

public class MockDataBase implements Database {

    private final List<Card> cards = new ArrayList<>();

    public MockDataBase() {
        cards.add(new Card("unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));
        cards.add(new Card("unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));
        cards.add(new Card("unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));
        cards.add(new Card("unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));
        cards.add(new Card("unknown", "unknown", "Lausanne", 1000, "file:///android_asset/apart_fake_image_1.jpeg"));
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
    public void accept(GlideLoaderVisitor visitor) {
        visitor.visit(this);
    }

}
