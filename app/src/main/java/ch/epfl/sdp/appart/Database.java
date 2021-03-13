package ch.epfl.sdp.appart;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import ch.epfl.sdp.appart.database.QueryResult;
import ch.epfl.sdp.appart.scrolling.card.Card;

public interface Database {

    void getCards(OnCompleteListener<QueryResult> callback);

    void putCard(Card card, OnCompleteListener<DocumentReference> callback);

    void updateCard(Card card, OnCompleteListener<Void> callback);

}
