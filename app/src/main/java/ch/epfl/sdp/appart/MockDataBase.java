package ch.epfl.sdp.appart;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import ch.epfl.sdp.appart.scrolling.card.Card;

public class MockDataBase implements Database {
    @Override
    public void getCards(OnCompleteListener<QuerySnapshot> callback) {

    }

    @Override
    public void putCard(Card card, OnCompleteListener<DocumentReference> callback) {

    }

    @Override
    public void updateCard(Card card, OnCompleteListener<Void> callback) {

    }
}
