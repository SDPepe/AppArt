package ch.epfl.sdp.appart;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.database.QueryResult;
import ch.epfl.sdp.appart.database.QuerySnapshotAdapter;
import ch.epfl.sdp.appart.scrolling.card.Card;
import kotlin.reflect.KCallable;

@Singleton
public class FirebaseDB implements Database {

  private final FirebaseFirestore db;

  @Inject
  public FirebaseDB() {
    db = FirebaseFirestore.getInstance();
  }

  @Override
  public void getCards(OnCompleteListener<QueryResult> callback) {

    Task<QueryResult> query = db.collection("cards").get()
            .continueWith((Task<QuerySnapshot> t) -> {
               return new QuerySnapshotAdapter(t.getResult());
            });
    query.addOnCompleteListener(callback);

            /*
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {

                  Task<QueryResult> queryResultTask = new Task<QueryResult>() {
                    @Override
                    public boolean isComplete() {
                      return task.isComplete();
                    }

                    @Override
                    public boolean isSuccessful() {
                      return task.isSuccessful();
                    }

                    @Override
                    public boolean isCanceled() {
                      return task.isCanceled();
                    }

                    @Nullable
                    @Override
                    public QueryResult getResult() {
                      return new QuerySnapshotAdapter(task.getResult());
                    }

                    @Nullable
                    @Override
                    public <X extends Throwable> QueryResult getResult(@NonNull Class<X> aClass) throws X {
                      throw new UnsupportedOperationException("getResult not implemented");
                    }

                    @Nullable
                    @Override
                    public Exception getException() {
                      return null;
                    }

                    @NonNull
                    @Override
                    public Task<QueryResult> addOnSuccessListener(@NonNull OnSuccessListener<? super QueryResult> onSuccessListener) {
                      return null;
                    }

                    @NonNull
                    @Override
                    public Task<QueryResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super QueryResult> onSuccessListener) {
                      return null;
                    }

                    @NonNull
                    @Override
                    public Task<QueryResult> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super QueryResult> onSuccessListener) {
                      return null;
                    }

                    @NonNull
                    @Override
                    public Task<QueryResult> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                      return null;
                    }

                    @NonNull
                    @Override
                    public Task<QueryResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                      return null;
                    }

                    @NonNull
                    @Override
                    public Task<QueryResult> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                      return null;
                    }
                  };
                  queryResultTask.addOnCompleteListener(callback);
              }

            });
             */
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
