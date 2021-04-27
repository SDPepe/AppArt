package ch.epfl.sdp.appart.database;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import ch.epfl.sdp.appart.database.firestoreservicehelpers.FirestoreAdHelper;
import ch.epfl.sdp.appart.database.firestoreservicehelpers.FirestoreCardHelper;
import ch.epfl.sdp.appart.database.firestoreservicehelpers.FirestoreImageHelper;
import ch.epfl.sdp.appart.database.firestoreservicehelpers.FirestoreUserHelper;
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
    private final FirestoreAdHelper adHelper;
    private final FirestoreImageHelper imageHelper;
    private final FirestoreUserHelper userHelper;
    private final FirestoreCardHelper cardHelper;

    @Inject
    public FirestoreDatabaseService() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        adHelper = new FirestoreAdHelper();
        imageHelper = new FirestoreImageHelper();
        userHelper = new FirestoreUserHelper();
        cardHelper = new FirestoreCardHelper();
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<List<Card>> getCards() {
        return cardHelper.getCards();
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<Boolean> updateCard(@NotNull @NonNull Card card) {
        return cardHelper.updateCard(card);
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<User> getUser(@NonNull String userId) {
        return userHelper.getUser(userId);
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<Boolean> putUser(@NonNull User user) {
        return userHelper.putUser(user);
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<Boolean> updateUser(@NonNull User user, Uri uri) {
        return userHelper.updateUser(user, uri);
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
        return adHelper.putAd(ad, uriList);
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<Boolean> putImage(Uri uri, String name, String path) {
        return imageHelper.putImage(uri, name, path);
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
