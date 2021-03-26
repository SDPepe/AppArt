package ch.epfl.sdp.appart.database;

import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoaderVisitor;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.Gender;
import ch.epfl.sdp.appart.user.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.glide.visitor.GlideLoaderVisitor;
import ch.epfl.sdp.appart.scrolling.ad.Ad;
import ch.epfl.sdp.appart.scrolling.ad.ContactInfo;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.Gender;
import ch.epfl.sdp.appart.user.User;

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

                            queriedCards.add(
                                    new Card(document.getId(), (String) document.getData().get("userId"),
                                            (String) document.getData().get("city"),
                                            (long) document.getData().get("price"),
                                            (String) document.getData().get("imageUrl")));
                        }
                        result.complete(queriedCards);

                    } else {
                        result.completeExceptionally(new UnsupportedOperationException(
                                "failed to fetch the cards from firebase"));
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
                resultIdFuture
                        .completeExceptionally(new IllegalStateException("query of the cards failed"));
            }
        });
        return resultIdFuture;
    }

    @Override
    public CompletableFuture<Boolean> updateCard(Card card) {
        return update(null, card);
    }

    /**
     * Gets a document and apply success on success and failure on failure.
     *
     * @param collection the collection we want to get the document from
     * @param docId      the id of the document we want to get
     * @param success    the callback that is applied on success
     * @param failure    the callback that is applied on failure
     */
    private void getDocAndApply(String collection, String docId,
                                Consumer<Task<DocumentSnapshot>> success,
                                Consumer<Task<DocumentSnapshot>> failure) {
        this.db.collection(collection).document(docId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                success.accept(task);
            } else {
                failure.accept(task);
            }
        });
    }

    /**
     * Gets a collection and apply success on success and failure on failure
     *
     * @param ref        the DocumentRef from which we get the collection
     * @param collection the collection we want to get
     * @param success    the callback that is applied on success
     * @param failure    the callback that is applied on failure
     */
    private void getDocAndApply(DocumentReference ref, String collection,
                                Consumer<Task<QuerySnapshot>> success,
                                Consumer<Task<QuerySnapshot>> failure) {
        ref.collection(collection).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                success.accept(task);
            } else {
                failure.accept(task);
            }
        });
    }

    /**
     * Takes a cardId and fetch the adId for the corresponding card
     *
     * @param cardId the cardId which stores the relevant adId
     * @return a future which will contain the adId
     */
    private CompletableFuture<String> getAdId(String cardId) {
        CompletableFuture<String> adIdFuture = new CompletableFuture<>();
        getDocAndApply("cards", cardId,
                task -> adIdFuture.complete((String) task.getResult().get("adId"
                )),
                task -> adIdFuture.completeExceptionally(new DatabaseRequestFailedException(task.getException().getMessage())));
        return adIdFuture;
    }

    /**
     * Fetch the ad from the database using the adId. First, it fetches the photoRefs and stores
     * them in photoRefsFuture. Then, it fetches the other information and stores it in
     * partialAdFuture.
     *
     * @param adIdFuture      the future containing the adId
     * @param photoRefsFuture the future that will store the photoRefs
     * @param partialAdFuture the future that will store the other information
     */
    private void getAdFromAdId(CompletableFuture<String> adIdFuture,
                               CompletableFuture<List<String>> photoRefsFuture,
                               CompletableFuture<PartialAd> partialAdFuture,
                               CompletableFuture<ContactInfo> contactInfoFuture) {
        adIdFuture.thenAccept(adId -> {
            getDocAndApply("ads", adId,
                    adTask -> getDocAndApply(adTask.getResult().getReference(), "photosRefs",
                            task -> {
                                List<String> photoRefs =
                                        task.getResult().getDocuments().stream().map(documentSnapshot -> "Ads/" + documentSnapshot.get("ref")).collect(Collectors.toList());
                                photoRefsFuture.complete(photoRefs);
                            }, task -> {
                                photoRefsFuture.completeExceptionally(new DatabaseRequestFailedException(task.getException().getMessage()));
                            }),
                    adTask -> photoRefsFuture.completeExceptionally(new DatabaseRequestFailedException(adTask.getException().getMessage())));

            getDocAndApply("ads", adId, task -> {
                String title = (String) task.getResult().get("title");
                String price = (String) task.getResult().get("price");
                String address = (String) task.getResult().get("address");
                String advertiserId = (String) task.getResult().get("advertiserId");
                String description = (String) task.getResult().get("description");
                boolean hasVTour = (boolean) task.getResult().get("hasVTour");
                partialAdFuture.complete(new PartialAd(title, price, address, advertiserId,
                        description, hasVTour));
            }, task -> partialAdFuture.completeExceptionally(new DatabaseRequestFailedException(task.getException().getMessage())));

            partialAdFuture.thenAccept(partialAd -> getDocAndApply("users",
                    partialAd.advertiserId, userTask -> {
                        String userEmail = (String) userTask.getResult().get("email");
                        String userPhoneNumber = (String) userTask.getResult().get("phone");
                        String name = (String) userTask.getResult().get("name");
                        contactInfoFuture.complete(new ContactInfo(userEmail, userPhoneNumber
                                , name));
                    },
                    userTask -> contactInfoFuture.completeExceptionally(new DatabaseRequestFailedException(userTask.getException().getMessage()))));
        });
    }

    /**
     * Set photoRefsFuture and partialAdFuture in a fail state if adIdFuture fails
     *
     * @param adIdFuture        the future containing the adId
     * @param photoRefsFuture   the future containing the photosRefs
     * @param partialAdFuture   the future containing partial information about the ad
     * @param contactInfoFuture the future containing the contact info of the owner of the ad
     */
    private void setupGetAdFailure(CompletableFuture<String> adIdFuture,
                                   CompletableFuture<List<String>> photoRefsFuture,
                                   CompletableFuture<PartialAd> partialAdFuture,
                                   CompletableFuture<ContactInfo> contactInfoFuture) {
        adIdFuture.exceptionally(e -> {
            DatabaseRequestFailedException adIdFailed = new DatabaseRequestFailedException("adId " +
                    "failed !");
            photoRefsFuture.completeExceptionally(adIdFailed);
            partialAdFuture.completeExceptionally(adIdFailed);
            contactInfoFuture.completeExceptionally(adIdFailed);
            return null;
        });
    }

    /**
     * Returns a CompletableFuture<Ad> that will be filled with data from the photoRefsFuture,
     * the partialAdFuture and the contactInfoFuture
     *
     * @param photoRefsFuture   the future containing the photosRefs
     * @param partialAdFuture   the future containing the partialAdFuture
     * @param contactInfoFuture the future containing the
     * @return a future that will contain the ad
     */
    private CompletableFuture<Ad> buildAdFromFutures(CompletableFuture<List<String>> photoRefsFuture, CompletableFuture<PartialAd> partialAdFuture, CompletableFuture<ContactInfo> contactInfoFuture) {
        return CompletableFuture.allOf(photoRefsFuture,
                partialAdFuture, contactInfoFuture).thenApply(dummy -> {
            PartialAd partialAd = partialAdFuture.join();
            return new Ad(partialAd.title, partialAd.price, partialAd.address,
                    partialAd.advertiserId, partialAd.description, photoRefsFuture.join(),
                    partialAd.hasVTour, contactInfoFuture.join());
        });
    }

    @Override
    public CompletableFuture<Ad> getAd(String cardId) {

        //It is necessary to have these two futures since the photoRefs are stored into a
        // collection into the ad.
        CompletableFuture<List<String>> photoRefsFuture = new CompletableFuture<>();
        CompletableFuture<PartialAd> partialAdFuture = new CompletableFuture<>();
        CompletableFuture<ContactInfo> contactInfoFuture = new CompletableFuture<>();

        CompletableFuture<String> adIdFuture = getAdId(cardId);
        getAdFromAdId(adIdFuture, photoRefsFuture, partialAdFuture, contactInfoFuture);
        setupGetAdFailure(adIdFuture, photoRefsFuture, partialAdFuture, contactInfoFuture);

        return buildAdFromFutures(photoRefsFuture, partialAdFuture, contactInfoFuture);
    }

    @Override
    public void accept(GlideLoaderVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(GlideBitmapLoaderVisitor visitor) {
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
                        result.completeExceptionally(new UnsupportedOperationException(
                                "failed to fetch the user from firebase"));
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
            }
        });
        return isFinishedFuture;
    }

    @Override
    public CompletableFuture<Boolean> updateUser(User user) {
        return update(user, null);
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

    private CompletableFuture<Boolean> update(User u, Card c) {
        CompletableFuture<Boolean> isFinishedFuture = new CompletableFuture<>();
        if (u != null) {
            db.collection("user")
                    .document(u.getUserId())
                    .set(extractUserInfo(u))
                    .addOnCompleteListener(task -> {
                        isFinishedFuture.complete(task.isSuccessful());
                    });
        } else if (c != null) {
            db.collection("cards")
                    .document(c.getId())
                    .set(extractCardsInfo(c))
                    .addOnCompleteListener(task -> {
                        isFinishedFuture.complete(task.isSuccessful());
                    });
        }
        return isFinishedFuture;
    }

    /**
     * Returns the storage reference of a stored firebase object
     *
     * @param storageUrl the url in the storage like Cards/img.jpeg would return an image from
     *                   the the
     *                   Cards folder named img.jpeg
     * @return the StorageReference of the object.
     */
    public StorageReference getStorageReference(String storageUrl) {
        return storage.getReferenceFromUrl(STORAGE_URL + storageUrl);
    }

    /**
     * This class stores the partial information needed to build an Ad.
     * This is useful when getting an Ad from the database and allow the use of only
     * two futures.
     */
    private static class PartialAd {
        public final String title;
        public final String price;
        public final String address;
        public final String advertiserId;
        public final String description;
        public final boolean hasVTour;

        PartialAd(String title, String price, String address, String advertiserId,
                  String description, boolean hasVTour) {
            if (title == null || price == null || address == null || advertiserId == null || description == null)
                throw new IllegalArgumentException();
            this.title = title;
            this.price = price;
            this.address = address;
            this.advertiserId = advertiserId;
            this.description = description;
            this.hasVTour = hasVTour;
        }
    }
}

