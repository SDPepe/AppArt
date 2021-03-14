package ch.epfl.sdp.appart;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;

import ch.epfl.sdp.appart.database.Document;
import ch.epfl.sdp.appart.database.Query;
import ch.epfl.sdp.appart.scrolling.card.Card;

public interface Database {

    void getCards(OnCompleteListener<Query> callback);

    void putCard(Card card, OnCompleteListener<Document> callback);

    void updateCard(Card card, OnCompleteListener<Void> callback);

}
