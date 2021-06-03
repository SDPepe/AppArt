package ch.epfl.sdp.appart.database.firestoreservicehelpers;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.database.firebaselayout.CardLayout;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.utils.serializers.CardSerializer;

/**
 * Helper class to add cards to and retrieve them from Firestore.
 */
public class FirestoreCardHelper {

    private final FirebaseFirestore db;

    public FirestoreCardHelper() {
        db = FirebaseFirestore.getInstance();
    }

    @NotNull
    @NonNull
    public CompletableFuture<List<Card>> getCards() {
        CompletableFuture<List<Card>> result = new CompletableFuture<>();

        //ask firebase async to get the cards objects and notify the future
        //when they have been fetched
        db.collection(FirebaseLayout.CARDS_DIRECTORY).get().addOnCompleteListener(
                task -> getAndCheckCards(task,  result));
        return result;
    }

    @NotNull
    @NonNull
    public CompletableFuture<List<Card>> getCardsFilter(String location) {
        CompletableFuture<List<Card>> result = new CompletableFuture<>();
        //ask firebase async to get the cards objects and notify the future
        //when they have been fetched
        db.collection(FirebaseLayout.CARDS_DIRECTORY)
                .whereGreaterThanOrEqualTo("city",  location)
                .whereLessThanOrEqualTo("city", location+"\uF7FF").get().addOnCompleteListener(
                task -> getAndCheckCards(task,  result));
        return result;
    }
    @NotNull
    @NonNull
    public CompletableFuture<List<Card>> getCardsFilterPrice(int min, int max){
        CompletableFuture<List<Card>> result = new CompletableFuture<>();
        db.collection(FirebaseLayout.CARDS_DIRECTORY)
            .whereGreaterThanOrEqualTo(CardLayout.PRICE,  min)
            .whereLessThanOrEqualTo(CardLayout.PRICE, max).get().addOnCompleteListener(
            task -> getAndCheckCards(task,  result));
        return result;
    }

    @NotNull
    @NonNull
    public CompletableFuture<List<Card>> getCardsById(@NonNull List<String> ids){
        CompletableFuture<List<Card>> result = new CompletableFuture<>();
            db.collection(FirebaseLayout.CARDS_DIRECTORY)
                .get().addOnCompleteListener(
                task -> {
                    List<Card> queriedCards = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : (Iterable<? extends QueryDocumentSnapshot>) task.getResult()) {
                            Map<String, Object> data = document.getData();
                            Card card = CardSerializer.deserialize(document.getId(), data);
                            if (ids.contains(card.getAdId())){
                                queriedCards.add(card);
                            }
                        }
                        result.complete(queriedCards);
                    } else {
                        result.completeExceptionally(
                            new DatabaseServiceException(task.getException().getMessage()));
                    }
                }
            );
        return result;
    }

    @NotNull
    @NonNull
    public CompletableFuture<Boolean> updateCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("card cannot bu null");
        }
        CompletableFuture<Boolean> isFinishedFuture = new CompletableFuture<>();
        db.collection(FirebaseLayout.CARDS_DIRECTORY)
                .document(card.getId())
                .set(CardSerializer.serialize(card))
                .addOnCompleteListener(task -> isFinishedFuture.complete(task.isSuccessful()));
        return isFinishedFuture;
    }

    @NotNull
    @NonNull
    public CompletableFuture<Void> putCard(Card card, DocumentReference path) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        path.set(CardSerializer.serialize(card)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                result.complete(null);
            } else {
                result.completeExceptionally(new DatabaseServiceException(task.getException().getMessage()));
            }
        });
        return result;
    }

    @NotNull
    @NonNull
    public CompletableFuture<Boolean> deleteCard(String cardId) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        db.collection(FirebaseLayout.CARDS_DIRECTORY).document(cardId).delete().addOnCompleteListener(t -> {
            if (t.isSuccessful())
                result.complete(true);
            else
                result.complete(false);
        });
        return result;
    }

    /* <--- general util private methods ---> */

    /**
     * Creates the cards from the given task nad completes the future accordingly.
     */
    private void getAndCheckCards(Task task, CompletableFuture<List<Card>> result) {
        List<Card> queriedCards = new ArrayList<>();
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot document : (Iterable<? extends QueryDocumentSnapshot>) task.getResult()) {
                Map<String, Object> data = document.getData();
                queriedCards.add(CardSerializer.deserialize(document.getId(), data));
            }
            result.complete(queriedCards);
        } else {
            result.completeExceptionally(
                    new DatabaseServiceException(task.getException().getMessage()));
        }
    }
}
