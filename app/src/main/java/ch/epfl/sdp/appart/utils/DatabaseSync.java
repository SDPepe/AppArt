package ch.epfl.sdp.appart.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoader;
import ch.epfl.sdp.appart.user.User;

/**
 * Util class used for operations commonly needed when dealing with synchronization of the local
 * database with the server.
 */
public class DatabaseSync {

    /**
     * Fetches the user's profile picture from the server
     *
     * @param context the context to use with Glide
     * @param db      database service from which we fetch the image
     * @return a completable future with the image as bitmap
     */
    public static CompletableFuture<Bitmap> getUserProfilePicture(Context context,
                                                                  DatabaseService db,
                                                                  String userImgPath) {
        CompletableFuture<Bitmap> imgRes = new CompletableFuture<>();
        db.accept(new GlideBitmapLoader(context, imgRes, userImgPath));
        return imgRes;
    }

    /**
     * Fetches the user and their profile picture from the server, saves the info to the local
     * database.
     *
     * @param context the context to use with Glide
     * @param db      database service from which the user and their profile image is fetched
     * @param ldb     local database service to which the info is saved
     * @param userId  id of the user to save
     * @return a completable future telling whether the operation was successful
     */
    public static CompletableFuture<Void> saveCurrentUserToLocalDB(Context context,
                                                                   DatabaseService db,
                                                                   LocalDatabaseService ldb,
                                                                   String userId) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        CompletableFuture<User> userRes = db.getUser(userId);
        userRes.exceptionally(e -> {
            result.completeExceptionally(e);
            return null;
        });
        userRes.thenAccept(u -> {
            CompletableFuture<Bitmap> pfpRes = getUserProfilePicture(context,
                    db, u.getProfileImagePathAndName());
            pfpRes.exceptionally(e -> {
                result.completeExceptionally(e);
                return null;
            });
            pfpRes.thenAccept(img -> {
                CompletableFuture<Void> saveRes = ldb.setCurrentUser(u, img);
                saveRes.exceptionally(e -> {
                    result.completeExceptionally(e);
                    Log.d("SYNC", "Failed to save locally");
                    return null;
                });
                saveRes.thenAccept(res -> {
                    Log.d("SYNC", "user saved locally");
                    result.complete(null);
                });
            });
        });
        return result;
    }

    /**
     * Fetches the user and the images of this ad from the server and saves all ad related info
     * to the local database.
     *
     * @param context the context to use with Glide
     * @param cardId  the id of the card for the ad to save
     * @param adId    the id of the ad to save
     * @param ad      the ad to save
     * @param images  list of ad images
     * @return a completable future telling whether the operation was successful
     */
    public static CompletableFuture<Void> saveFavoriteAd(Context context,
                                                           DatabaseService db,
                                                           LocalDatabaseService ldb,
                                                           String cardId, String adId,
                                                           Ad ad, List<Bitmap> images) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        CompletableFuture<User> userRes = db.getUser(ad.getAdvertiserId());
        List<CompletableFuture<Bitmap>> panoramasBitmaps = fetchImages(context, db, adId,
                ad.getPanoramaReferences());
        CompletableFuture<Void> allOfPanoramas =
                CompletableFuture.allOf(panoramasBitmaps
                        .toArray(new CompletableFuture[panoramasBitmaps.size()]));

        userRes.exceptionally(e -> {
            result.completeExceptionally(e);
            return null;
        });
        userRes.thenAccept(u -> {
            getUserProfilePicture(context, db, u.getProfileImagePathAndName())
                    .thenAcceptBoth(allOfPanoramas, (pfp, ignoredRes) -> {
                        List<Bitmap> panoramas = panoramasBitmaps.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList());
                        CompletableFuture<Void> writeRes =
                                ldb.writeCompleteAd(adId, cardId, ad, u, images, panoramas, pfp);
                        writeRes.thenAccept(res -> {
                            Log.d("DATASYNC", "local write ok");
                            result.complete(null);
                        });
                        writeRes.exceptionally(e -> {
                            result.completeExceptionally(e);
                            return null;
                        });
                    })
                    .exceptionally(e -> {
                        result.completeExceptionally(e);
                        return null;
                    });
        });

        return result;
    }

    /**
     * Fetches from the server the images from the given references.
     *
     * @param context    the context to use with Glide
     * @param db         the database we fetch the images from
     * @param adID       the id of the ad
     * @param references the list of image ids
     * @return a list of completablefutures of bitmaps containing the images
     */
    public static List<CompletableFuture<Bitmap>> fetchImages(Context context,
                                                              DatabaseService db,
                                                              String adID,
                                                              List<String> references) {
        List<CompletableFuture<Bitmap>> futures = new ArrayList<>();
        for (int i = 0; i < references.size(); i++) {
            String ref = new StoragePathBuilder()
                    .toAdsStorageDirectory()
                    .toDirectory(adID)
                    .withFile(references.get(i));
            CompletableFuture<Bitmap> bitmapRes = new CompletableFuture<>();
            db.accept(new GlideBitmapLoader(context, bitmapRes, ref));
            futures.add(bitmapRes);
        }

        return futures;
    }

}
