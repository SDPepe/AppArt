package ch.epfl.sdp.appart;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.database.Document;
import ch.epfl.sdp.appart.database.adapters.DocumentReferenceAdapter;
import ch.epfl.sdp.appart.database.Query;
import ch.epfl.sdp.appart.database.adapters.QuerySnapshotAdapter;
import ch.epfl.sdp.appart.scrolling.card.Card;

@Singleton
public class FirebaseDB implements Database {

  private final FirebaseFirestore db;

  @Inject
  public FirebaseDB() {
    db = FirebaseFirestore.getInstance();
  }

  @Override
  public void getCards(OnCompleteListener<Query> callback) {

    Task<Query> query = db.collection("cards").get()
            .continueWith((Task<QuerySnapshot> t) -> {
               return new QuerySnapshotAdapter(t.getResult());
            });
    query.addOnCompleteListener(callback);

  }

  @Override
  public void putCard(Card card, OnCompleteListener<Document> callback) {
      Task<Document> document = db.collection("cards")
        .add(extractCardsInfo(card)).continueWith((Task<DocumentReference> t) -> {
           return new DocumentReferenceAdapter(t.getResult());
        });
        document.addOnCompleteListener(callback);
  }
  
  @Override
  public void updateCard(Card card, OnCompleteListener<Void> callback) {
    db.collection("cards")
        .document(card.getId())
        .set(extractCardsInfo(card))
            .addOnCompleteListener(callback);
  }

  private Map<String, Object> extractCardsInfo(Card card){
    Map<String, Object> docData = new HashMap<>();
    docData.put("userId", card.getUserId());
    docData.put("city", card.getCity());
    docData.put("price", card.getPrice());
    docData.put("imageUrl", card.getImageUrl());
    return docData;
  }

}
