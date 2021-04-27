package ch.epfl.sdp.appart.database.firestoreservicehelpers;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;
import ch.epfl.sdp.appart.database.firebaselayout.CardLayout;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.scrolling.card.Card;

/**
 * Helper class to add ads to and retrieve them from Firestore.
 */
public class FirestoreAdHelper {

    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final FirestoreImageHelper imageHelper;
    private final FirestoreCardHelper cardHelper;
    private final String adsPath;

    @Inject
    public FirestoreAdHelper() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        imageHelper = new FirestoreImageHelper();
        cardHelper = new FirestoreCardHelper();
        adsPath = FirebaseLayout.ADS_DIRECTORY + FirebaseLayout.SEPARATOR;
    }

    // TODO rewrite getAd so that it takes the ad id in argument
    @NotNull
    @NonNull
    public CompletableFuture<Ad> getAd(String adId) {
        CompletableFuture<Ad> result = new CompletableFuture<>();
        result.complete(null);
        return result;
    }

    @NotNull
    @NonNull
    public CompletableFuture<String> putAd(Ad ad, List<Uri> uriList) {
        CompletableFuture<String> result = new CompletableFuture<>();
        CompletableFuture<Void> imagesResult = new CompletableFuture<>();
        CompletableFuture<Void> adResult = new CompletableFuture<>();
        CompletableFuture<Void> cardResult = new CompletableFuture<>();
        DocumentReference newAdRef = db.collection(FirebaseLayout.ADS_DIRECTORY).document();
        DocumentReference cardRef = db.collection(FirebaseLayout.CARDS_DIRECTORY).document();
        String storagePath = adsPath + newAdRef.getId();

        // upload photos
        List<String> actualRefs = new ArrayList<>();
        List<CompletableFuture<Boolean>> imagesUploadResults = new ArrayList<>();
        Log.d("URI", "size" + uriList.size());
        for (int i = 0; i < uriList.size(); i++) {
            String name = FirebaseLayout.PHOTO_NAME + i + ".jpeg"; // TODO modify to support other extensions
            actualRefs.add(name);
            imagesUploadResults.add(imageHelper.putImage(uriList.get(i), name, storagePath));
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

    /** <--- getAd private methods --->*/

    /** <--- putAd private methods --->*/

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

    /**
     * Checks whether the upload of ad information to FIrestore completed successfully. If it didn't,
     * cleans up and completes exceptionally.
     */
    private void checkAdUpload(CompletableFuture<Void> result, Ad ad, DocumentReference adRef,
                               DocumentReference cardRef, StorageReference imagesRef,
                               List<String> imagesRefsList) {
        adRef.set(extractAdInfo(ad)).addOnCompleteListener(
                task -> {
                    cleanUpIfFailed(task.isSuccessful(), result, adRef, cardRef, imagesRef);
                    result.complete(null);
                });
        // TODO is this the right place to do this?
        setPhotosReferencesForAd(imagesRefsList, adRef, cardRef, imagesRef);
    }

    /**
     * Checks whether the upload of card information to Firestore completed successfully. If it
     * didn't, cleans up and completes exceptionally.
     */
    private void checkCardUpload(CompletableFuture<Void> result, Ad ad, DocumentReference adRef,
                                 DocumentReference cardRef, StorageReference imagesRef,
                                 String firstImageRef) {
        Card c = new Card(cardRef.getId(), adRef.getId(), ad.getAdvertiserId(), ad.getCity(),
                ad.getPrice(), firstImageRef, ad.hasVRTour());
        cardHelper.putCard(c, cardRef).thenAccept(successful -> {
            cleanUpIfFailed(successful, result, adRef, cardRef, imagesRef);
            result.complete(null);
        });
    }

    /**
     * Checks that every part of the ad upload completed successfully, completes exceptionally if at
     * least one Future completed exceptionally.
     */
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
                    new DatabaseServiceException("Ad upload failed!"));
        }
    }

    /**
     * Serializes ad information for upload on Firestore
     */
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

    /**
     * Serializes card information for upload on Firestore
     */
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
