package ch.epfl.sdp.appart;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.database.Document;
import ch.epfl.sdp.appart.database.Query;
import ch.epfl.sdp.appart.database.QueryDocument;
import ch.epfl.sdp.appart.scrolling.card.Card;

public class MockDataBase implements Database {

    public MockDataBase() {

    }

    @Override
    public CompletableFuture<List<Card>> getCards() {
        throw new UnsupportedOperationException("getCard is not implemented");
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
