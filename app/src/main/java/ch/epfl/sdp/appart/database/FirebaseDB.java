package ch.epfl.sdp.appart.database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
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

import ch.epfl.sdp.appart.glide.visitor.GlideLoaderVisitor;
import ch.epfl.sdp.appart.scrolling.ad.Ad;
import ch.epfl.sdp.appart.scrolling.card.Card;
import kotlin.NotImplementedError;

@Singleton
public class FirebaseDB implements Database {

    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final static String STORAGE_URL = "gs://appart-ec344.appspot.com/";

    @Inject
    public FirebaseDB() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public CompletableFuture<List<Card>> getCards() {

        CompletableFuture<List<Card>> result = new CompletableFuture<>();

        //ask firebase async to get the cards objects and notify the future
        //when they have been fetched
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
                        result.completeExceptionally(new UnsupportedOperationException("failed to fetch the cards from firebase"));
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
    public CompletableFuture<Boolean> updateCard(Card card) {
        CompletableFuture<Boolean> isFinishedFuture = new CompletableFuture<>();
        db.collection("cards")
                .document(card.getId())
                .set(extractCardsInfo(card))
                .addOnCompleteListener(task -> {
                    isFinishedFuture.complete(task.isSuccessful());
                });
        return isFinishedFuture;
    }

    private <T> void getField(CompletableFuture<T> future, String collection, String rootId, String field) {
        this.db.collection(collection).document(rootId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                future.complete((T)task.getResult().get(field));
            }
            else {
                future.completeExceptionally(new DatabaseRequestFailedException(task.getException().getMessage()));
            }
        });
    }

    @Override
    public CompletableFuture<Ad> getAd(String id) {
        CompletableFuture<List<String>> photoRefsFuture = new CompletableFuture<>();
        this.db.collection("ads").document(id).get().addOnCompleteListener(adTask -> {
            if(adTask.isSuccessful()) {
                adTask.getResult().getReference().collection("photosRefs").get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        List<String> photoRefs = task.getResult().getDocuments().stream().map(documentSnapshot -> (String)documentSnapshot.get("ref")).collect(Collectors.toList());
                        photoRefsFuture.complete(photoRefs);
                    }
                    else {
                        photoRefsFuture.completeExceptionally(new DatabaseRequestFailedException(task.getException().getMessage()));
                    }
                });
            }
        });

        CompletableFuture<String> addressFuture = new CompletableFuture<>();
        getField(addressFuture, "ads", id, "address");

        CompletableFuture<String> advertiserIdFuture = new CompletableFuture<>();
        getField(advertiserIdFuture, "ads", id, "advertiserId");

        throw new NotImplementedError();
    }

    @Override
    public void accept(GlideLoaderVisitor visitor) {
        visitor.visit(this);
    }

    private Map<String, Object> extractCardsInfo(Card card) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("userId", card.getUserId());
        docData.put("city", card.getCity());
        docData.put("price", card.getPrice());
        docData.put("imageUrl", card.getImageUrl());
        return docData;
    }

    /**
     * Returns the storage reference of a stored firebase object
     * @param storageUrl the url in the storage like Cards/img.jpeg
     *                   would return an image from the the Cards folder named img.jpeg
     * @return the StorageReference of the object.
     */
    public StorageReference getStorageReference(String storageUrl) {
        return storage.getReferenceFromUrl(STORAGE_URL + storageUrl);
    }

}
