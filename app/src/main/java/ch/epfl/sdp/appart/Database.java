package ch.epfl.sdp.appart;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

public interface Database {

    void getCards(OnCompleteListener<QuerySnapshot> callback);

    void putCard(Card card, OnCompleteListener<DocumentReference> callback);

    void updateCard(Card card, OnCompleteListener<Void> callback);

}
