package ch.epfl.sdp.appart.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoader;

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
}
