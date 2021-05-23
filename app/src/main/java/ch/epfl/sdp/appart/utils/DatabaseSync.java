package ch.epfl.sdp.appart.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.concurrent.CompletableFuture;

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
     * @param context the context to use with Glide
     * @param db database service from which the user and their profile image is fetched
     * @param ldb local database service to which the info is saved
     * @param userId id of the user to save
     * @return a completable future telling whether the operation was successful
     */
    public static CompletableFuture<Void> saveCurrentUserToLocalDB(Context context,
                                                                   DatabaseService db,
                                                                   LocalDatabaseService ldb,
                                                                   String userId){
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
                    return null;
                });
                saveRes.thenAccept(res -> {
                    result.complete(null);
                });
            });
        });
        return result;
    }
}
