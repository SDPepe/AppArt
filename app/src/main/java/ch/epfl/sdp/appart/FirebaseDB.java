package ch.epfl.sdp.appart;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.sdp.appart.scrolling.card.Card;

public class FirebaseDB implements Database {

  private final FirebaseFirestore db;

  public FirebaseDB() {
    db = FirebaseFirestore.getInstance();
  }

  @Override
  public void getCards(OnCompleteListener<QuerySnapshot> callback) {
    db.collection("cards").get()
        .addOnCompleteListener(callback);
  }

  @Override
  public void putCard(Card card, OnCompleteListener<DocumentReference> callback) {
    db.collection("cards")
        .add(extractCardsInfo(card))
        .addOnCompleteListener(callback);
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
