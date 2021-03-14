package ch.epfl.sdp.appart;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.database.Document;
import ch.epfl.sdp.appart.database.Query;
import ch.epfl.sdp.appart.database.QueryDocument;
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
        throw new UnsupportedOperationException("putCard is not implemented");
    }

    @Override
    public CompletableFuture<Void> updateCard(Card card) {
        throw new UnsupportedOperationException("updateCard is not implemented");
    }

}
