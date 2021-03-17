package ch.epfl.sdp.appart;

import android.net.Uri;

import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.Gender;
import ch.epfl.sdp.appart.user.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

    private Map<String, Object> extractCardsInfo(Card card) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("userId", card.getUserId());
        docData.put("city", card.getCity());
        docData.put("price", card.getPrice());
        docData.put("imageUrl", card.getImageUrl());
        return docData;
    }

    @Override
    public CompletableFuture<User> getUser(String userId) {
        CompletableFuture<User> result = new CompletableFuture<>();

        //ask firebase async to get the user objects and notify the future
        //when they have been fetched
        db.collection("users").document(userId).get().addOnCompleteListener(
            task -> {
                if (task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    AppUser user = new AppUser((String) data.get("email"), userId);

                    user.setAge((int) data.get("age"));
                    user.setUserEmail((String) data.get("email"));
                    user.setGender(Gender.ALL.get((int) data.get("gender")));
                    user.setName((String) data.get("name"));
                    user.setPhoneNumber((String) data.get("phoneNumber"));
                    user.setProfileImage((String) data.get("profilePicture"));

                    result.complete(user);

                } else {
                    result.completeExceptionally(new UnsupportedOperationException("failed to fetch the user from firebase"));
                }
            }
        );
        return result;
    }

    @Override
    public CompletableFuture<Boolean> putUser(User user) {
        CompletableFuture<Boolean> isFinishedFuture = new CompletableFuture<>();
        db.collection("users")
            .document(user.getUserId())
            .set(extractUserInfo(user)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                isFinishedFuture.complete(task.isSuccessful());
            }});
        return isFinishedFuture;
    }

    @Override
    public CompletableFuture<Boolean> updateUser(User user) {
        CompletableFuture<Boolean> isFinishedFuture = new CompletableFuture<>();
        db.collection("user")
            .document(user.getUserId())
            .set(extractUserInfo(user))
            .addOnCompleteListener(task -> {
                isFinishedFuture.complete(task.isSuccessful());
            });
        return isFinishedFuture;
    }

    private Map<String, Object> extractUserInfo(User user) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("age", user.getAge());
        docData.put("email", user.getUserEmail());
        docData.put("gender", user.getGender());
        docData.put("name", user.getName());
        docData.put("phoneNumber", user.getPhoneNumber());
        docData.put("profilePicture", user.getProfileImage());
        return docData;
    }



}
