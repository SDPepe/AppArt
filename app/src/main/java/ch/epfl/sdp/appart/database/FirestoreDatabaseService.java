package ch.epfl.sdp.appart.database;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.ContactInfo;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;
import ch.epfl.sdp.appart.database.firebaselayout.CardLayout;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.database.firebaselayout.UserLayout;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoaderVisitor;
import ch.epfl.sdp.appart.glide.visitor.GlideLoaderListenerVisitor;
import ch.epfl.sdp.appart.glide.visitor.GlideLoaderVisitor;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

/**
 * Implementation of the DatabaseService with Firestore from Firebase.
 */
@Singleton
public class FirestoreDatabaseService implements DatabaseService {

    private final static String STORAGE_URL = "gs://appart-ec344.appspot.com/";
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    @Inject
    public FirestoreDatabaseService() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<List<Card>> getCards() {

        CompletableFuture<List<Card>> result = new CompletableFuture<>();

        //ask firebase async to get the cards objects and notify the future
        //when they have been fetched
        db.collection(FirebaseLayout.CARDS_DIRECTORY).get().addOnCompleteListener(
                task -> {
                    handleTask(task,  result);
                }
        );
        return result;
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<List<Card>> getCardsFilter(String location) {
        CompletableFuture<List<Card>> result = new CompletableFuture<>();
        //ask firebase async to get the cards objects and notify the future
        //when they have been fetched
        db.collection(FirebaseLayout.CARDS_DIRECTORY)
            .whereGreaterThanOrEqualTo("city",  location)
            .whereLessThanOrEqualTo("city", location+"\uF7FF").get().addOnCompleteListener(
            task -> {
                handleTask(task,  result);
            }
        );

        return result;
    }
    private Card generateCard(Map<String, Object> data,QueryDocumentSnapshot document){
        return new Card(
            document.getId(),
            (String) data.get(CardLayout.AD_ID),
            (String) data.get(CardLayout.USER_ID),
            (String) data.get(CardLayout.CITY),
            (long) data.get(CardLayout.PRICE),
            (String) data.get(CardLayout.IMAGE));
    }

    private void handleTask(Task task, CompletableFuture<List<Card>> result){
        List<Card> queriedCards = new ArrayList<>();
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot document : (List<QueryDocumentSnapshot>) task.getResult()) {
                Map<String, Object> data = document.getData();
                queriedCards.add(generateCard(data, document));
            }
            result.complete(queriedCards);
        } else {
            result.completeExceptionally(
                new DatabaseServiceException(
                    "failed to fetch the cards from firebase"
                ));
        }
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<Boolean> updateCard(@NotNull @NonNull Card card) {

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
    @Override
    @NonNull
    public CompletableFuture<User> getUser(@NonNull String userId) {

        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        CompletableFuture<User> result = new CompletableFuture<>();

        //ask firebase asynchronously to get the associated user object and notify the future
        //when they have been fetched
        db.collection(FirebaseLayout.USERS_DIRECTORY).document(userId).get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> data = task.getResult().getData();
                        //TODO: Handle case where the string does not match a gender
                        AppUser user = new AppUser(userId, (String) data.get(UserLayout.EMAIL));
                        user.setUserEmail((String) data.get(UserLayout.EMAIL));
                        Object rawAge = data.get(UserLayout.AGE);
                        if (rawAge != null) {
                            user.setAge((long) rawAge);
                        }

                        Object rawGender = data.get(UserLayout.GENDER);
                        if (rawGender != null) {
                            user.setGender((String) rawGender);
                        }

                        Object rawName = data.get(UserLayout.NAME);
                        if (rawName != null) {
                            user.setName((String) rawName);
                        }

                        Object rawPhoneNumber = data.get(UserLayout.PHONE);
                        if (rawPhoneNumber != null) {
                            user.setPhoneNumber((String) rawPhoneNumber);
                        }

                        Object rawPfpRef = data.get(UserLayout.PICTURE);
                        if (rawPfpRef != null) {
                            user.setProfileImage((String) rawPfpRef); //WARNING WAS "profilePicture" before not matching our actual
                        }

                        result.complete(user);

                    } else {
                        result.completeExceptionally(
                                new DatabaseServiceException(
                                        "failed to request the user from firebase"
                                )
                        );
                    }
                }
        );
        return result;
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<Boolean> putUser(@NonNull User user) {
        CompletableFuture<Boolean> isFinishedFuture = new CompletableFuture<>();
        db.collection(FirebaseLayout.USERS_DIRECTORY)
                .document(user.getUserId())
                .set(extractUserInfo(user)).addOnCompleteListener(task -> isFinishedFuture.complete(task.isSuccessful()));
        return isFinishedFuture;
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<Boolean> updateUser(@NonNull User user, Uri uri) {

        /*if (user == null) {
            throw new IllegalArgumentException("user cannot bu null");
        }

        CompletableFuture<Boolean> isFinishedFuture = new CompletableFuture<>();

        if (uri != null) {
            String name = "photo" + ".jpeg"; // getTypeFromUri(uri);
            String path = "User" + "/" + user.getUserId();

            putImage(uri, name, path).thenAccept(res -> {
                if (!res) {
                    isFinishedFuture.completeExceptionally(
                        new UnsupportedOperationException("Ad upload failed!"));
                }
            });
        } else {
            return updateUserDb(isFinishedFuture, user);
        }
        return updateUserDb(isFinishedFuture, user);*/
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        return updateUserDb(result, user);

    }

    private CompletableFuture<Boolean> updateUserDb(CompletableFuture<Boolean> res, User user){
            db.collection(FirebaseLayout.USERS_DIRECTORY)
                    .document(user.getUserId())
                    .set(extractUserInfo(user))
                    .addOnCompleteListener(
                            task -> res.complete(task.isSuccessful()));
            return res;
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<Ad> getAd(String cardId) {
        if (cardId == null) {
            throw new IllegalArgumentException("card id cannot be null");
        }

        CompletableFuture<Ad> result = new CompletableFuture<>();
        CompletableFuture<String> adIdFuture = getAdIdFromCard(cardId);
        CompletableFuture<Ad.AdBuilder> partialAdFuture = getPartialAdFromFutureAdId(adIdFuture);

        CompletableFuture<List<String>> photosReferencesFuture
                = getPhotosReferencesFromFutureAdId(adIdFuture);


        CompletableFuture<ContactInfo> contactInfoFuture
                = getContactInfoFromFuturePartialAd(partialAdFuture);


        CompletableFuture<CompletableFuture<Ad>> chain =
                adIdFuture.thenCombine(photosReferencesFuture, (adId, photosReferences)
                        -> partialAdFuture.thenCombine(contactInfoFuture, (adBuilder, contactInfo)
                        -> {
                    adBuilder.withPhotosIds(photosReferences);
                    return adBuilder.build();
                }));

        chain.thenAccept(topmost -> {
            topmost.thenAccept(result::complete);
            topmost.exceptionally(exception -> {
                result.completeExceptionally(exception);
                return null;
            });
        });

        chain.exceptionally(exception -> {
            result.completeExceptionally(exception);
            return null;
        });

        return result;
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<String> putAd(Ad ad, List<Uri> uriList) {
        CompletableFuture<String> result = new CompletableFuture<>();
        CompletableFuture<Void> imagesResult = new CompletableFuture<>();
        CompletableFuture<Void> adResult = new CompletableFuture<>();
        CompletableFuture<Void> cardResult = new CompletableFuture<>();
        DocumentReference newAdRef = db.collection(FirebaseLayout.ADS_DIRECTORY).document();
        DocumentReference cardRef = db.collection(FirebaseLayout.CARDS_DIRECTORY).document();
        String storagePath = FirebaseLayout.ADS_DIRECTORY + FirebaseLayout.SEPARATOR + newAdRef.getId();

        // upload photos
        List<String> actualRefs = new ArrayList<>();
        List<CompletableFuture<Boolean>> imagesUploadResults = new ArrayList<>();
        Log.d("URI", "size" + uriList.size());
        for (int i = 0; i < uriList.size(); i++) {
            String name = FirebaseLayout.PHOTO_NAME + i + ".jpeg"; // TODO modify to support other extensions
            actualRefs.add(name);
            imagesUploadResults.add(putImage(uriList.get(i), name, storagePath));
        }
        // check whether any of the uploads failed
        checkPhotosUpload(imagesUploadResults, imagesResult, newAdRef, cardRef, storage.getReference(storagePath));

        // build and send card / ad
        checkCardUpload(cardResult, ad, newAdRef, cardRef, null, actualRefs.get(0));
        checkAdUpload(adResult, ad, newAdRef, cardRef, null, actualRefs);

        // check if everything succeeded
        finalizeAdUpload(result, imagesResult, adResult, cardResult, newAdRef);
        return result;
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<Boolean> putImage(Uri uri, String name, String path) {
        if (uri == null || name == null) {
            throw new IllegalArgumentException("parameters cannot be null");
        }
        CompletableFuture<Boolean> isFinishedFuture = new CompletableFuture<>();
        StorageReference storeRef = storage.getReference(path);
        StorageReference fileReference = storeRef.child(name);
        fileReference.putFile(uri).addOnCompleteListener(
                task -> isFinishedFuture.complete(task.isSuccessful()));
        return isFinishedFuture;
    }

    @Override
    public CompletableFuture<Void> clearCache() {
        CompletableFuture<Void> futureClear = new CompletableFuture<>();
        db.clearPersistence().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                futureClear.complete(null);
            } else {
                futureClear.completeExceptionally(new DatabaseServiceException(task.getException().getMessage()));
            }
        });
        return futureClear;
    }

    /**
     * If failed, deletes from firebase the given references and completes exceptionally
     */
    private void cleanUpIfFailed(boolean taskSuccessful, CompletableFuture<Void> result, DocumentReference adRef,
                                 DocumentReference cardRef, StorageReference imagesRef) {
        if (!taskSuccessful) {
            Log.d("Ad creation", "ad upload failed");
            adRef.delete();
            cardRef.delete();
            imagesRef.listAll().addOnSuccessListener(listResult -> {
                List<StorageReference> items = listResult.getItems();
                for (StorageReference item : items) {
                    item.delete();
                }
            }).addOnFailureListener(e -> Log.d("Ad upload", "Failed to cleanup after failed upload"));

            // TODO create exception
            result.completeExceptionally(
                    new UnsupportedOperationException("Ad upload failed!"));
        }
    }

    /**
     * Adds to the ad a collection with the ids of the images. Cleans up if it fails.
     */
    private void setPhotosReferencesForAd(List<String> actualRefs, DocumentReference newAdRef,
                                          DocumentReference cardRef, StorageReference storageRef) {
        for (int i = 0; i < actualRefs.size(); i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", actualRefs.get(i));
            DocumentReference photoRefDocReference = newAdRef.collection(AdLayout.PICTURES_DIRECTORY)
                    .document();
            photoRefDocReference.set(data); // TODO check all uploads
        }
    }

    /**
     * Checks whether all image uploads completed successfully. If they didn't, cleans up and
     * completes exceptionally
     */
    private void checkPhotosUpload(List<CompletableFuture<Boolean>> futures,
                                   CompletableFuture<Void> result, DocumentReference adRef,
                                   DocumentReference cardRef, StorageReference imagesRef) {
        CompletableFuture<Boolean>[] resultsArray = new CompletableFuture[futures.size()];
        futures.toArray(resultsArray);
        CompletableFuture.allOf(resultsArray).thenAccept(res -> {
            boolean successful = true;
            List<Boolean> completedUploadResults = futures.stream()
                    .map(CompletableFuture::join).collect(Collectors.toList());
            for (boolean b : completedUploadResults) {
                successful = successful && b;
                cleanUpIfFailed(successful, result, adRef, cardRef, imagesRef);
            }
            result.complete(null);
        });
    }

    private void checkAdUpload(CompletableFuture<Void> result, Ad ad, DocumentReference adRef,
                               DocumentReference cardRef, StorageReference imagesRef,
                               List<String> imagesRefsList) {
        adRef.set(extractAdInfo(ad)).addOnCompleteListener(
                task -> {
                    cleanUpIfFailed(task.isSuccessful(), result, adRef, cardRef, imagesRef);
                    result.complete(null);
                });
        setPhotosReferencesForAd(imagesRefsList, adRef, cardRef, imagesRef);
    }

    private void checkCardUpload(CompletableFuture<Void> result, Ad ad, DocumentReference adRef,
                                 DocumentReference cardRef, StorageReference imagesRef,
                                 String firstImageRef) {
        Card c = new Card(cardRef.getId(), adRef.getId(), ad.getAdvertiserId(), ad.getCity(),
                ad.getPrice(), firstImageRef, ad.hasVRTour());
        cardRef.set(extractCardsInfo(c)).addOnCompleteListener(
                task -> {
                    cleanUpIfFailed(task.isSuccessful(), result, adRef, cardRef, imagesRef);
                    result.complete(null);
                });
    }

    private void finalizeAdUpload(CompletableFuture<String> result, CompletableFuture<Void> imagesResult,
                                  CompletableFuture<Void> adResult, CompletableFuture<Void> cardResult,
                                  DocumentReference adRef) {
        CompletableFuture<Void> allOf = CompletableFuture.allOf(adResult, cardResult);
        allOf.thenAccept(res -> result.complete(adRef.getId()));
        allOf.exceptionally(e -> {
            result.completeExceptionally(e);
            return null;
        });

    }

    @Override
    public void accept(GlideLoaderVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(GlideBitmapLoaderVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(GlideLoaderListenerVisitor visitor) {
        visitor.visit(this);
    }

    private Map<String, Object> extractCardsInfo(Card card) {
        Map<String, Object> docData = new HashMap<>();
        docData.put(CardLayout.USER_ID, card.getUserId());
        docData.put(CardLayout.CITY, card.getCity());
        docData.put(CardLayout.PRICE, card.getPrice());
        docData.put(CardLayout.IMAGE, card.getImageUrl());
        docData.put(CardLayout.AD_ID, card.getAdId());
        return docData;
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
     * Utility function to clean up storage database
     *
     * @param ref reference to the folder/file to delete
     */
    public void removeFromStorage(StorageReference ref) {
        ref.delete();
    }

    /**
     * Takes a cardId and fetch the adId for the corresponding card.
     * Indeed, all the cards that are showed in the scrolling menu
     * refers to an ad by an AdId. So in order to query the right
     * Ad we need to retrieve the right ad id first.
     * The future will complete with the right id if the task is
     * successful and will complete with an exception otherwise.
     *
     * @param cardId the id of the card which we want to collect the ad id
     * @return a CompletableFuture<String> that will hold a String, the card id or a DatabaseServiceException
     * if it failed to retrieve the id
     * @throws IllegalArgumentException if the cardId is null
     */
    private CompletableFuture<String> getAdIdFromCard(String cardId) {

        if (cardId == null) {
            throw new IllegalArgumentException("card id cannot be null");
        }

        CompletableFuture<String> result = new CompletableFuture<>();
        db.collection(FirebaseLayout.CARDS_DIRECTORY).document(cardId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                result.complete((String) task.getResult().get(CardLayout.AD_ID));
            } else {
                result.completeExceptionally(new DatabaseServiceException(task.getException().getMessage()));
            }
        });

        return result;
    }

    /**
     * Once the ad id future is completed we retrieve the lists of photos references
     *
     * @param adIdFuture the CompletableFuture that will hold the id of the card when completed
     * @return a CompletableFuture<List<String>> that will hold the list of references to the pictures
     * or the Future can complete exceptionally with a DatabaseServiceException if the request was unsuccessful.
     * @throws IllegalArgumentException if adIdFuture is null
     */
    private CompletableFuture<List<String>> getPhotosReferencesFromFutureAdId(CompletableFuture<String> adIdFuture) {

        if (adIdFuture == null) {
            throw new IllegalArgumentException("ad id future cannot be null");
        }

        CompletableFuture<DocumentReference> adReferenceFuture = new CompletableFuture<>();
        CompletableFuture<List<String>> photosReferencesListFuture = new CompletableFuture<>();

        adIdFuture.thenAccept(adId -> this.db.collection(FirebaseLayout.ADS_DIRECTORY).document(adId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                adReferenceFuture.complete(task.getResult().getReference());
            } else {
                adReferenceFuture.completeExceptionally(new DatabaseServiceException(task.getException().getMessage()));
            }
        }));

        adIdFuture.exceptionally(exception -> {
            photosReferencesListFuture.completeExceptionally(exception);
            return null;
        });


        //once the ad firestore document reference is ready is ready
        adReferenceFuture.thenAccept(adReference -> adReference.collection(AdLayout.PICTURES_DIRECTORY).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                List<DocumentSnapshot> documentSnapshots = snapshot.getDocuments();
                List<String> result = documentSnapshots.stream().map(documentSnapshot ->
                        (String) documentSnapshot.get("id")).collect(Collectors.toList());
                photosReferencesListFuture.complete(result);
            } else {
                photosReferencesListFuture.completeExceptionally(
                        new DatabaseServiceException(task.getException().getMessage())
                );
            }
        }));

        return photosReferencesListFuture;
    }

    /**
     * Once the ad id future is completed we retrieve some relevant information about the ad.
     *
     * @param adIdFuture a CompletableFuture<String> that will hold the id of the card when completed
     * @return a CompletableFuture<PartialAd> that will hold the PartialAd. The Future can complete
     * exceptionally with a DatabaseServiceException if the request was unsuccessful.
     * @throws IllegalArgumentException if adIdFuture is null
     */
    private CompletableFuture<Ad.AdBuilder> getPartialAdFromFutureAdId(CompletableFuture<String> adIdFuture) {

        if (adIdFuture == null) {
            throw new IllegalArgumentException("ad id future cannot be null");
        }

        CompletableFuture<Ad.AdBuilder> result = new CompletableFuture<>();

        //once the ad id is available we query the right ad to get its detailed fields
        adIdFuture.thenAccept(adId -> db.collection(FirebaseLayout.ADS_DIRECTORY).document(adId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();

                String title = (String) documentSnapshot.get(AdLayout.TITLE);
                long price = (long) documentSnapshot.get(AdLayout.PRICE);
                PricePeriod pricePeriod = PricePeriod.ALL.get(Math.toIntExact((long) documentSnapshot.get(AdLayout.PRICE_PERIOD)));
                String street = (String) documentSnapshot.get(AdLayout.STREET);
                String city = (String) documentSnapshot.get(AdLayout.CITY);
                String advertiserId = (String) documentSnapshot.get(AdLayout.ADVERTISER_ID);
                String description = (String) documentSnapshot.get(AdLayout.DESCRIPTION);
                boolean hasVTour = (boolean) documentSnapshot.get(AdLayout.VR_TOUR);

                Ad.AdBuilder builder = new Ad.AdBuilder()
                        .withTitle(title)
                        .withPrice(price)
                        .withPricePeriod(pricePeriod)
                        .withStreet(street)
                        .withCity(city)
                        .withAdvertiserId(advertiserId)
                        .withDescription(description)
                        .hasVRTour(hasVTour);

                result.complete(builder);
            } else {
                result.completeExceptionally(new DatabaseServiceException(task.getException().getMessage()));
            }
        }));

        adIdFuture.exceptionally(exception -> {
            result.completeExceptionally(exception);
            return null;
        });

        return result;
    }

    /**
     * Once the partialAd is retrieved we will retrieve the related user to get information about
     * it.
     *
     * @param partialAdFuture a CompletableFuture<PartialAd> that will hold the id of the card when completed.
     *                        This Future can also complete Exceptionally with a DatabaseServiceException.
     * @return a CompletableFuture<ContactInfo> that will hold the ContactInfo. The Future can complete
     * exceptionally with a DatabaseServiceException if the request was unsuccessful.
     * @throws IllegalArgumentException if partialAdFuture is null
     */
    private CompletableFuture<ContactInfo> getContactInfoFromFuturePartialAd(CompletableFuture<Ad.AdBuilder> partialAdFuture) {

        if (partialAdFuture == null) {
            throw new IllegalArgumentException("partial ad future cannot be null");
        }
        CompletableFuture<ContactInfo> result = new CompletableFuture<>();
        //once the partial ad has be retrieve we query the user that is providing the ad
        partialAdFuture.thenAccept(partialAd -> db.collection(FirebaseLayout.USERS_DIRECTORY).document(partialAd.getAdvertiserId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                /*DocumentSnapshot documentSnapshot = task.getResult();
                String userEmail = (String) documentSnapshot.get(UserLayout.EMAIL);
                String userPhoneNumber = (String) documentSnapshot.get(UserLayout.PHONE);
                String name = (String) documentSnapshot.get(UserLayout.NAME);
                ContactInfo contactInfo = new ContactInfo(userEmail, userPhoneNumber, name);*/
                result.complete(null);
            } else {
                result.completeExceptionally(new DatabaseServiceException(task.getException().getMessage()));
            }
        }));

        partialAdFuture.exceptionally(exception -> {
            result.completeExceptionally(exception);
            return null;
        });

        return result;
    }

    private Map<String, Object> extractAdInfo(Ad ad) {
        Map<String, Object> adData = new HashMap<>();
        adData.put(AdLayout.ADVERTISER_ID, ad.getAdvertiserId());
        adData.put(AdLayout.CITY, ad.getCity());
        adData.put(AdLayout.DESCRIPTION, ad.getDescription());
        adData.put(AdLayout.VR_TOUR, ad.hasVRTour());
        adData.put(AdLayout.PRICE, ad.getPrice());
        adData.put(AdLayout.PRICE_PERIOD, ad.getPricePeriod().ordinal());
        adData.put(AdLayout.STREET, ad.getStreet());
        adData.put(AdLayout.TITLE, ad.getTitle());
        return adData;
    }

    private Map<String, Object> extractUserInfo(User user) {
        Map<String, Object> docData = new HashMap<>();
        docData.put(UserLayout.AGE, user.getAge());
        docData.put(UserLayout.EMAIL, user.getUserEmail());
        docData.put(UserLayout.GENDER, user.getGender());
        docData.put(UserLayout.NAME, user.getName());
        docData.put(UserLayout.PHONE, user.getPhoneNumber());
        docData.put(UserLayout.PICTURE, user.getProfileImage());
        return docData;
    }

    /**
     * Sets up the use of an emulator for the Firebase authentication service.
     *
     * @param ip   the ip of the emulator
     * @param port the port that corresponds to the authentication service emulation
     */
    public void useEmulator(String ip, int port) {
        if (ip == null) throw new IllegalArgumentException();
        db.useEmulator(ip, port);
    }

}
