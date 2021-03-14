package ch.epfl.sdp.appart;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.Iterator;
import java.util.Map;

import ch.epfl.sdp.appart.database.Document;
import ch.epfl.sdp.appart.database.Query;
import ch.epfl.sdp.appart.database.QueryDocument;
import ch.epfl.sdp.appart.scrolling.card.Card;

public class MockDataBase implements Database {

    public MockDataBase() {

    }

    @Override
    public void getCards(OnCompleteListener<Query> callback) {

    }

    @Override
    public void putCard(Card card, OnCompleteListener<Document> callback) {
        throw new UnsupportedOperationException("putCard is not implemented");
    }

    @Override
    public void updateCard(Card card, OnCompleteListener<Void> callback) {
        throw new UnsupportedOperationException("updateCard is not implemented");
    }

}
