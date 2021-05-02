package ch.epfl.sdp.appart.database.firestoreservicehelpers;

import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;
import ch.epfl.sdp.appart.database.firebaselayout.CardLayout;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.utils.serializers.AdSerializer;

/**
 * Helper class to add ads to and retrieve them from Firestore.
 */
public class FirestoreAdHelper {

    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final FirestoreImageHelper imageHelper;
    private final FirestoreCardHelper cardHelper;
    private final String adsPath;
    private final AdSerializer serializer;

    public FirestoreAdHelper() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        imageHelper = new FirestoreImageHelper();
        cardHelper = new FirestoreCardHelper();
        adsPath = FirebaseLayout.ADS_DIRECTORY;
        serializer = new AdSerializer();
    }

    @NotNull
    @NonNull
    public CompletableFuture<Ad> getAd(String adId) {
        CompletableFuture<Ad> result = new CompletableFuture<>();
        CompletableFuture<Ad.AdBuilder> partialAdResult = new CompletableFuture<>();
        CompletableFuture<List<String>> imagesIdsResult = new CompletableFuture<>();

        // get images, if successful try to retrieve the other ad fields
        getAndCheckImagesIds(imagesIdsResult, adId);
        imagesIdsResult.exceptionally(e -> {
            result.completeExceptionally(e);
            return null;
        });
        imagesIdsResult.thenAccept(imagesIds -> {
            // get remaining ad fields, if successful add images ids to the ad and build it
            getAndCheckPartialAd(partialAdResult, adId);
            partialAdResult.exceptionally(e -> {
                result.completeExceptionally(e);
                return null;
            });
            partialAdResult.thenAccept(adBuilder -> {
                adBuilder.withPicturesReferences(imagesIds);
                result.complete(adBuilder.build());
            });

        });

        return result;
    }

    private Pair<List<String>, List<CompletableFuture<Boolean>>> uploadIndexedImages(List<Uri> uris, String prefix, String path) {
        List<String> references = new ArrayList<>();
        List<CompletableFuture<Boolean>> imagesUploadFutures = new ArrayList<>();

        for (int i = 0; i < uris.size(); i++) {
            String name = FirebaseLayout.PHOTO_NAME + i + ".jpeg"; // TODO modify to support other extensions
            references.add(name);
            imagesUploadFutures.add(imageHelper.putImage(uris.get(i), name, path));
        }
        return new Pair<>(references, imagesUploadFutures);
    }

    @NotNull
    @NonNull
    public CompletableFuture<String> putAd(Ad ad, List<Uri> picturesUris, List<Uri> panoramaUris) {

        DocumentReference newAdRef = db.collection(FirebaseLayout.ADS_DIRECTORY).document();
        DocumentReference cardRef = db.collection(FirebaseLayout.CARDS_DIRECTORY).document();
        String storagePath = adsPath + FirebaseLayout.SEPARATOR + newAdRef.getId();

        Pair<List<String>, List<CompletableFuture<Boolean>>> uploadPicturesResultPair =
                uploadIndexedImages(picturesUris, FirebaseLayout.PHOTO_NAME, storagePath);

        List<String> picturesReferences = uploadPicturesResultPair.first;
        List<CompletableFuture<Boolean>> uploadImagesFutures = uploadPicturesResultPair.second;

        // check whether any of the uploads failed
        CompletableFuture<Void> picturesCheckFuture =
                checkPhotosUpload(uploadImagesFutures, newAdRef, cardRef, storage.getReference(storagePath));

        //upload the panoramas
        Pair<List<String>, List<CompletableFuture<Boolean>>> uploadPanoramasResultPair =
                uploadIndexedImages(picturesUris, FirebaseLayout.PANORAMA_NAME, storagePath);
        List<String> panoramasReferences = uploadPanoramasResultPair.first;
        List<CompletableFuture<Boolean>> uploadPanoramasFutures = uploadPanoramasResultPair.second;

        // check whether any of the uploads failed
        CompletableFuture<Void> panoramasCheckFuture =
                checkPhotosUpload(uploadPanoramasFutures, newAdRef, cardRef, storage.getReference(storagePath));

        // build and send card / ad
        CompletableFuture<Void> cardCheckFuture =
                checkCardUpload(ad, newAdRef, cardRef, null, picturesReferences.get(0));
        CompletableFuture<Void> adCheckFuture =
                checkAdUpload(ad, newAdRef, cardRef, null, picturesReferences);

        // check if everything succeeded
        return finalizeAdUpload(newAdRef, picturesCheckFuture, panoramasCheckFuture, cardCheckFuture, adCheckFuture);
    }

    /* <--- getAd private methods --->*/

    private void getAndCheckImagesIds(CompletableFuture<List<String>> result,
                                      String adId) {
        db.collection(adsPath).document(adId).collection(AdLayout.PICTURES_DIRECTORY)
                .get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                result.completeExceptionally(new DatabaseServiceException("Failed to get pictures ids."));
            } else {
                result.complete(task.getResult().getDocuments().stream().map(documentSnapshot ->
                        (String) documentSnapshot.get("id")).collect(Collectors.toList()));
            }
        });
    }

    private void getAndCheckPartialAd(CompletableFuture<Ad.AdBuilder> result,
                                      String adId) {
        db.collection(adsPath).document(adId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();

                Ad.AdBuilder builder = new Ad.AdBuilder()
                        .withTitle((String) documentSnapshot.get(AdLayout.TITLE))
                        .withPrice((long) documentSnapshot.get(AdLayout.PRICE))
                        .withPricePeriod(PricePeriod.ALL.get(
                                Math.toIntExact((long) documentSnapshot.get(AdLayout.PRICE_PERIOD))))
                        .withStreet((String) documentSnapshot.get(AdLayout.STREET))
                        .withCity((String) documentSnapshot.get(AdLayout.CITY))
                        .withAdvertiserId((String) documentSnapshot.get(AdLayout.ADVERTISER_ID))
                        .withDescription((String) documentSnapshot.get(AdLayout.DESCRIPTION))
                        .hasVRTour((boolean) documentSnapshot.get(AdLayout.VR_TOUR));

                result.complete(builder);
            } else {
                result.completeExceptionally(new DatabaseServiceException(Objects.requireNonNull(
                        task.getException()).getMessage()));
            }
        });
    }


    /**
     * Adds to the ad a collection with the ids of the images. Cleans up if it fails.
     */
    private void setPhotosReferencesForAd(CompletableFuture<Void> result, List<String> actualRefs,
                                          DocumentReference newAdRef) {
        List<CompletableFuture<Void>> photosIdResults = new ArrayList<>();
        for (int i = 0; i < actualRefs.size(); i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", actualRefs.get(i));
            DocumentReference photoRefDocReference = newAdRef.collection(AdLayout.PICTURES_DIRECTORY)
                    .document();
            CompletableFuture<Void> photoIdResult = new CompletableFuture<>();
            photosIdResults.add(photoIdResult);
            photoRefDocReference.set(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    photoIdResult.complete(null);
                } else {
                    photoIdResult.completeExceptionally(new DatabaseServiceException(
                            Objects.requireNonNull(task.getException()).getMessage()));
                }
            });
            CompletableFuture<Void>[] resultsArray = new CompletableFuture[photosIdResults.size()];
            photosIdResults.toArray(resultsArray);
            CompletableFuture.allOf(resultsArray).thenAccept(res -> result.complete(null))
                    .exceptionally(e -> {
                        result.completeExceptionally(e);
                        return null;
            });
        }
    }
    /* <--- putAd private methods --->*/

    /**
     * Checks whether all image uploads completed successfully. If they didn't, cleans up and
     * completes exceptionally
     */
    private CompletableFuture<Void> checkPhotosUpload(List<CompletableFuture<Boolean>> futures, DocumentReference adRef,
                                   DocumentReference cardRef, StorageReference imagesRef) {

        CompletableFuture<Void> result = new CompletableFuture<>();
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
        return result;
    }

    /**
     * Checks whether the upload of ad information to FIrestore completed successfully. If it didn't,
     * cleans up and completes exceptionally.
     */
    private CompletableFuture<Void> checkAdUpload(Ad ad, DocumentReference adRef,
                               DocumentReference cardRef, StorageReference imagesRef,
                               List<String> imagesRefsList) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        CompletableFuture<Void> infoUpload = new CompletableFuture<>();
        CompletableFuture<Void> idsUpload = new CompletableFuture<>();
        adRef.set(serializer.serialize(ad)).addOnCompleteListener(
                task -> {
                    cleanUpIfFailed(task.isSuccessful(), result, adRef, cardRef, imagesRef);
                    infoUpload.complete(null);
                });
        setPhotosReferencesForAd(idsUpload, imagesRefsList, adRef);
        CompletableFuture.allOf(infoUpload, idsUpload)
                .thenAccept(res -> result.complete(null))
                .exceptionally(e -> {
                    result.completeExceptionally(e);
                    return null;
                });
        return result;
    }

    /**
     * Checks whether the upload of card information to Firestore completed successfully. If it
     * didn't, cleans up and completes exceptionally.
     */
    private CompletableFuture<Void> checkCardUpload(Ad ad, DocumentReference adRef,
                                 DocumentReference cardRef, StorageReference imagesRef,
                                 String firstImageRef) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        Card c = new Card(cardRef.getId(), adRef.getId(), ad.getAdvertiserId(), ad.getCity(),
                ad.getPrice(), firstImageRef, ad.hasVRTour());
        cardHelper.putCard(c, cardRef).thenAccept(successful -> {
            cleanUpIfFailed(successful, result, adRef, cardRef, imagesRef);
            result.complete(null);
        });
        return result;
    }

    /**
     * Checks that every part of the ad upload completed successfully, completes exceptionally if at
     * least one Future completed exceptionally.
     */
    private CompletableFuture<String> finalizeAdUpload(DocumentReference adRef, CompletableFuture<Void>... futures) {
        CompletableFuture<String> result = new CompletableFuture<>();
        CompletableFuture.allOf(futures)
                .thenAccept(res -> result.complete(adRef.getId()))
                .exceptionally(e -> {
            result.completeExceptionally(e);
            return null;
        });
        return result;
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

}
