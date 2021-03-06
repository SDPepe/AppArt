package ch.epfl.sdp.appart.database.firestoreservicehelpers;

import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.utils.serializers.AdSerializer;
import ch.epfl.sdp.appart.utils.serializers.UserSerializer;

/**
 * Helper class to add ads to and retrieve them from Firestore.
 */
public class FirestoreAdHelper {

    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final FirestoreImageHelper imageHelper;
    private final FirestoreCardHelper cardHelper;
    private final String adsPath;

    public FirestoreAdHelper() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        imageHelper = new FirestoreImageHelper();
        cardHelper = new FirestoreCardHelper();
        adsPath = FirebaseLayout.ADS_DIRECTORY;
    }

    @NotNull
    @NonNull
    public CompletableFuture<Ad> getAd(String adId) {
        CompletableFuture<Ad> result = new CompletableFuture<>();

        // get images, if successful try to retrieve the other ad fields
        CompletableFuture<List<String>> picturesReferencesFutures =
                getAndCheckImagesIds(adId, AdLayout.PICTURES_DIRECTORY);
        picturesReferencesFutures.exceptionally(e -> {
            result.completeExceptionally(e);
            return null;
        });

        // get panoramas, if successful try to retrieve the other ad fields
        CompletableFuture<List<String>> panoramasReferencesFutures =
                getAndCheckImagesIds(adId, AdLayout.PANORAMA_DIRECTORY);
        panoramasReferencesFutures.exceptionally(e -> {
            result.completeExceptionally(e);
            return null;
        });

        //combine both pictures and panoramas ids futures
        CompletionStage<Pair<List<String>, List<String>>> combined =
                CompletableFuture.allOf(picturesReferencesFutures, panoramasReferencesFutures)
                        .thenApply(ignoredVoid ->
                                new Pair<>(picturesReferencesFutures.join(), panoramasReferencesFutures.join())
                        );

        combined.thenAccept(imagesAndPanoramasIds -> {
            // get remaining ad fields, if successful add images ids to the ad and build it
            CompletableFuture<Ad.AdBuilder> partialAdResult = getAndCheckPartialAd(adId);
            partialAdResult.exceptionally(e -> {
                result.completeExceptionally(e);
                return null;
            });
            partialAdResult.thenAccept(adBuilder -> {
                adBuilder.withPicturesReferences(imagesAndPanoramasIds.first);
                adBuilder.withPanoramaReferences(imagesAndPanoramasIds.second);
                result.complete(adBuilder.build());
            });

        });

        return result;
    }

    private Pair<List<String>, List<CompletableFuture<Boolean>>> uploadIndexedImages(List<Uri> uris, String prefix, String path) {
        List<String> references = new ArrayList<>();
        List<CompletableFuture<Boolean>> imagesUploadFutures = new ArrayList<>();

        for (int i = 0; i < uris.size(); i++) {
            String name = prefix + i + ".jpeg";
            references.add(name);
            imagesUploadFutures.add(imageHelper.putImage(uris.get(i), path + name));
        }
        return new Pair<>(references, imagesUploadFutures);
    }

    @NotNull
    @NonNull
    public CompletableFuture<String> putAd(Ad ad, List<Uri> picturesUris, List<Uri> panoramaUris) {

        //we create a new entry reference for an ad and for the related card
        DocumentReference newAdRef = db.collection(FirebaseLayout.ADS_DIRECTORY).document();
        DocumentReference cardRef = db.collection(FirebaseLayout.CARDS_DIRECTORY).document();

        //storage path is the the path pointing to the ads directory in a folder named with the ad id
        String storagePath = adsPath + FirebaseLayout.SEPARATOR + newAdRef.getId() + FirebaseLayout.SEPARATOR;

        //HEAD here
        //first we upload the pictures and check that the upload was successful
        Pair<List<String>, List<CompletableFuture<Boolean>>> uploadPicturesResultPair =
                uploadIndexedImages(picturesUris, FirebaseLayout.PHOTO_NAME, storagePath);
        List<String> picturesReferences = uploadPicturesResultPair.first;
        List<CompletableFuture<Boolean>> uploadImagesFutures = uploadPicturesResultPair.second;

        // check whether any of the uploads failed
        CompletableFuture<Void> picturesCheckFuture =
                checkPhotosUpload(uploadImagesFutures, newAdRef, cardRef, storage.getReference(storagePath));

        //We then upload the panoramas images in the ad folder with ad id name
        Pair<List<String>, List<CompletableFuture<Boolean>>> uploadPanoramasResultPair =
                uploadIndexedImages(panoramaUris, FirebaseLayout.PANORAMA_NAME, storagePath);
        List<String> panoramasReferences = uploadPanoramasResultPair.first;
        List<CompletableFuture<Boolean>> uploadPanoramasFutures = uploadPanoramasResultPair.second;

        // check whether any of the panoramas uploads failed. The check will squash everything if there was a problem.
        CompletableFuture<Void> panoramasCheckFuture =
                checkPhotosUpload(uploadPanoramasFutures, newAdRef, cardRef, storage.getReference(storagePath));

        //We upload the card with the first image as miniature
        CompletableFuture<Void> cardUploadFuture =
                uploadCardFromReferences(ad, newAdRef, cardRef, null, picturesReferences.get(0));

        //we upload the ad with the pictures and panoramas images. If a failure occurs we squash everything
        CompletableFuture<Void> adCheckFuture =
                uploadAdFromReferences(ad, newAdRef, cardRef, null, picturesReferences, panoramasReferences);

        //check if everything succeeded
        return finalizeAdUpload(newAdRef, picturesCheckFuture, panoramasCheckFuture, cardUploadFuture, adCheckFuture);
    }

    /* <--- getAd private methods --->*/

    private CompletableFuture<List<String>> getAndCheckImagesIds(String adId, String directory) {
        CompletableFuture<List<String>> result = new CompletableFuture<List<String>>();
        db.collection(adsPath).document(adId).collection(directory)
                .get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                result.completeExceptionally(new DatabaseServiceException("Failed to get pictures ids."));
            } else {
                result.complete(task.getResult().getDocuments().stream().map(documentSnapshot ->
                        (String) documentSnapshot.get(AdLayout.PICTURE_ELEMENT_ID_FIELD)).collect(Collectors.toList()));
            }
        });
        return result;
    }

    private CompletableFuture<Ad.AdBuilder> getAndCheckPartialAd(String adId) {

        CompletableFuture<Ad.AdBuilder> result = new CompletableFuture<>();
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

                db.collection(FirebaseLayout.USERS_DIRECTORY).document((String) documentSnapshot.get(AdLayout.ADVERTISER_ID)).get().addOnCompleteListener(
                        taskUser -> {
                    if (taskUser.isSuccessful()) {
                        String advertiserName = (String) taskUser.getResult().get("name");
                        builder.withAdvertiserName(advertiserName);
                        result.complete(builder);
                    } else {
                        result.completeExceptionally(new DatabaseServiceException(
                                taskUser.getException().getMessage()));
                    }
                });

            } else {
                result.completeExceptionally(new DatabaseServiceException(Objects.requireNonNull(
                        task.getException()).getMessage()));
            }
        });
        return result;
    }


    /**
     * Adds to the ad a collection with the ids of the images. Cleans up if it fails.
     */
    private CompletableFuture<Void> setPhotosReferencesForAd(List<String> actualRefs, CollectionReference collectionReference) {

        CompletableFuture<Void> result = new CompletableFuture<>();
        CompletableFuture<Void>[] photosIdResults = new CompletableFuture[actualRefs.size()];

        //iterate over all images references to set their reference in the firebase ad
        for (int i = 0; i < actualRefs.size(); i++) {

            //set the id of the current image reference
            Map<String, Object> data = new HashMap<>();
            data.put(AdLayout.PICTURE_ELEMENT_ID_FIELD, actualRefs.get(i));

            CompletableFuture<Void> photoIdResult = new CompletableFuture<>();
            photosIdResults[i] = photoIdResult;

            //set the data in firestore in the given collection by creating a new document in it
            collectionReference.document().set(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    photoIdResult.complete(null);
                } else {
                    photoIdResult.completeExceptionally(new DatabaseServiceException(
                            Objects.requireNonNull(task.getException()).getMessage()));
                }
            });

        }

        CompletableFuture.allOf(photosIdResults)
                .thenAccept(res -> result.complete(null))
                .exceptionally(e -> {
                    result.completeExceptionally(e);
                    return null;
                });

        return result;
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
     * Checks whether the upload of ad information to Firestore completed successfully. If it didn't,
     * cleans up and completes exceptionally.
     */
    private CompletableFuture<Void> uploadAdFromReferences(Ad ad, DocumentReference adRef,
                               DocumentReference cardRef, StorageReference imagesRef,
                               List<String> picturesReferences, List<String> panoramasReferences) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        CompletableFuture<Void> infoUpload = new CompletableFuture<>();
        //we first set the ad in the database by serializing it and if it fail we squash everything
        adRef.set(AdSerializer.serialize(ad)).addOnCompleteListener(
                task -> {
                    cleanUpIfFailed(task.isSuccessful(), result, adRef, cardRef, imagesRef);
                    infoUpload.complete(null);
                });

        //we then set the ids collection for the ad in firebase.
        CompletableFuture<Void> picturesIdsUploadFuture =
                setPhotosReferencesForAd(picturesReferences, adRef.collection(AdLayout.PICTURES_DIRECTORY));
        CompletableFuture<Void> panoramasIdsUploadFuture =
                setPhotosReferencesForAd(panoramasReferences, adRef.collection(AdLayout.PANORAMA_DIRECTORY));

        //we check how everything completed
        CompletableFuture.allOf(infoUpload, picturesIdsUploadFuture, panoramasIdsUploadFuture)
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
    private CompletableFuture<Void> uploadCardFromReferences(Ad ad, DocumentReference adRef,
                                 DocumentReference cardRef, StorageReference imagesRef,
                                 String firstImageRef) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        Card c = new Card(cardRef.getId(), adRef.getId(), ad.getAdvertiserId(), ad.getCity(),
                ad.getPrice(),ad.getPricePeriod(), firstImageRef, ad.hasVRTour());
        cardHelper.putCard(c, cardRef)
                .thenAccept(res -> result.complete(null))
                .exceptionally(e -> {
                    cleanUpIfFailed(false, result, adRef, cardRef, imagesRef);
                    result.completeExceptionally(e);
                    return null;
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

    public CompletableFuture<Boolean> deleteAd(String adId, String cardId) {
        CompletableFuture<Boolean> finalResult = new CompletableFuture<>();

        CompletableFuture<Boolean> result1 = new CompletableFuture<>();

        db.collection(adsPath).document(adId).delete().addOnCompleteListener(t -> {
            result1.complete(t.isSuccessful());
        });

        CompletableFuture<Boolean> result2 = cardHelper.deleteCard(cardId);

        CompletableFuture.allOf(result1, result2).thenAccept(res -> finalResult.complete(result1.join() && result2.join()));
        return finalResult;
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
            // imagesRef might be null if no image was given
            if (imagesRef != null) {
                imagesRef.listAll().addOnSuccessListener(listResult -> {
                    List<StorageReference> items = listResult.getItems();
                    for (StorageReference item : items) {
                        item.delete();
                    }
                }).addOnFailureListener(e -> Log.d("Ad upload", "Failed to cleanup after failed upload"));
            }

            result.completeExceptionally(
                    new DatabaseServiceException("Ad upload failed!"));
        }
    }

}
