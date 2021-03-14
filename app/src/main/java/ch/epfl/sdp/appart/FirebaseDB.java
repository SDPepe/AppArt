package ch.epfl.sdp.appart;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

                  List<CompletableFuture<Card>> cardsFutures = new ArrayList<>();

                  for (QueryDocumentSnapshot document : task.getResult()) {
                      CompletableFuture<Card> cardFuture = new CompletableFuture<>();

                      String imageUrl = (String) document.getData().get("imageUrl");
                      StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://appart-ec344.appspot.com/Cards/" + imageUrl);
                      ref.getDownloadUrl().addOnCompleteListener(getUrlTask -> {
                          if (getUrlTask.isSuccessful()) {
                              Uri uri = getUrlTask.getResult();
                              cardFuture.complete(new Card(document.getId(), (String) document.getData().get("userId"),
                                      (String) document.getData().get("city"),
                                      (long) document.getData().get("price"),
                                      /*uri.toString()*/imageUrl));
                          } else {
                              cardFuture.completeExceptionally(new IllegalStateException("failed to download one of the URL"));
                          }
                      });
                      cardsFutures.add(cardFuture);
                  }

                  allOf(cardsFutures).thenApply(cardsCompleted -> {
                      return result.complete(cardsCompleted);
                  });

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

  private <T> CompletableFuture<List<T>> allOf(List<CompletableFuture<T>> futuresList) {
          CompletableFuture<Void> allFuturesResult =
                  CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]));
          return allFuturesResult.thenApply(v ->
                  futuresList.stream().
                          map(future -> future.join()).
                          collect(Collectors.<T>toList())
          );
    }

}
