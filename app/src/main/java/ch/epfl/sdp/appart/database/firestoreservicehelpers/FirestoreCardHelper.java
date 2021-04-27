package ch.epfl.sdp.appart.database.firestoreservicehelpers;

import androidx.annotation.NonNull;

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

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.database.firebaselayout.CardLayout;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.scrolling.card.Card;

/**
 * Helper class to add cards to and retrieve them from Firestore.
 */
public class FirestoreCardHelper {

    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final String cardssPath;

    @Inject
    public FirestoreCardHelper() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        cardssPath = FirebaseLayout.CARDS_DIRECTORY + FirebaseLayout.SEPARATOR;
    }

    @NotNull
    @NonNull
    public CompletableFuture<List<Card>> getCards() {
        CompletableFuture<List<Card>> result = new CompletableFuture<>();

        //ask firebase async to get the cards objects and notify the future
        //when they have been fetched
        db.collection(FirebaseLayout.CARDS_DIRECTORY).get().addOnCompleteListener(
                task -> {

                    List<Card> queriedCards = new ArrayList<>();

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            queriedCards.add(
                                    new Card(document.getId(), (String) data.get(CardLayout.AD_ID), (String) data.get(CardLayout.USER_ID),
                                            (String) data.get(CardLayout.CITY),
                                            (long) data.get(CardLayout.PRICE),
                                            (String) data.get(CardLayout.IMAGE)));
                        }
                        result.complete(queriedCards);

                    } else {
                        result.completeExceptionally(
                                new DatabaseServiceException(
                                        "failed to fetch the cards from firebase"
                                ));
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
                .set(extractCardsInfo(card))
                .addOnCompleteListener(task -> isFinishedFuture.complete(task.isSuccessful()));
        return isFinishedFuture;
    }

    @NotNull
    @NonNull
    public CompletableFuture<Boolean> putCard(Card card, DocumentReference path) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        path.set(extractCardsInfo(card)).addOnCompleteListener(task -> {
            result.complete(task.isSuccessful());
        });
        return result;
    }

    /* <--- general util private methods ---> */

    private Map<String, Object> extractCardsInfo(Card card) {
        Map<String, Object> docData = new HashMap<>();
        docData.put(CardLayout.USER_ID, card.getUserId());
        docData.put(CardLayout.CITY, card.getCity());
        docData.put(CardLayout.PRICE, card.getPrice());
        docData.put(CardLayout.IMAGE, card.getImageUrl());
        docData.put(CardLayout.AD_ID, card.getAdId());
        return docData;
    }
}
