package ch.epfl.sdp.appart;


import static android.content.ContentValues.TAG;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.FirestoreClient;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDB implements Database {


  private final FirebaseFirestore db;

  public FirebaseDB() {
    db = FirebaseFirestore.getInstance();
  }

  @Override
  public List<Card> getCards() {
    // TODO get cards and return list
    //return db.collection("cards").get();
    return null;
  }

  @Override
  public boolean putCard(Card card) {
    Map<String, Object> docData = new HashMap<>();
    docData.put("ownerId", card.getUserId());
    docData.put("city", card.getCity());
    docData.put("price", card.getPrice());
    docData.put("imageUrl", card.getImageUrl());

    // TODO correct callback definition
    if (card.getId() == null) {
      /*
      db.collection("cards").add(docData)
          .addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void aVoid) {
              Log.d(TAG, "DocumentSnapshot successfully written!");
              }
          })
          .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Log.w(TAG, "Error writing document", e);
            }
          });*/
    } else {
      /*
      db.collection("cards").document(card.getId()).set(docData)
          .addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void aVoid) {
              Log.d(TAG, "DocumentSnapshot successfully written!");
              }
          })
          .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Log.w(TAG, "Error writing document", e);
            }
          });
       */
    }
    return true;
  }
}
