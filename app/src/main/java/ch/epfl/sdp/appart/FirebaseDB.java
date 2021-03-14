package ch.epfl.sdp.appart;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.database.Document;
import ch.epfl.sdp.appart.database.QueryDocument;
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
  public CompletableFuture<List<Card>> getCards() {

    CompletableFuture<List<Card>> result = new CompletableFuture<>();

    db.collection("cards").get().addOnCompleteListener(
            task -> {
              List<Card> queriedCards = new ArrayList<>();

              if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                  queriedCards.add(new Card(document.getId(), (String) document.getData().get("userId"),
                          (String) document.getData().get("city"),
                          (long) document.getData().get("price"),
                          (String) document.getData().get("imageUrl")));
                }
                result.complete(queriedCards);
              } else {
                result.completeExceptionally(new IllegalStateException("query of the cards failed"));
              }

            }
    );

   return result;
  }

  @Override
  public CompletableFuture<String> putCard(Card card) {
      CompletableFuture<String> resultIdFuture = new CompletableFuture<>();
      db.collection("cards")
        .add(extractCardsInfo(card)).addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            resultIdFuture.complete(task.getResult().getId());
          } else {
            resultIdFuture.completeExceptionally(new IllegalStateException("query of the cards failed"));
          }
        });
        return resultIdFuture;
  }
  
  @Override
  public CompletableFuture<Void> updateCard(Card card) {
    CompletableFuture<Void> isFinishedFuture = new CompletableFuture<>();
    db.collection("cards")
        .document(card.getId())
        .set(extractCardsInfo(card))
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                isFinishedFuture.complete(null);
              } else {
                isFinishedFuture.completeExceptionally(new IllegalStateException("update of the cards failed"));
              }
            });
    return isFinishedFuture;
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
