package ch.epfl.sdp.appart.database.firestoreservicehelpers;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;


public class FirestoreImageHelper {

    private final FirebaseStorage storage;

    private static final String DEFAULT_IMAGE_PATH = "users/default/";

    public FirestoreImageHelper() {
        storage = FirebaseStorage.getInstance();
    }

    @NotNull
    @NonNull
    public CompletableFuture<Boolean> putImage(Uri uri, String imagePathAndName) {
        if (uri == null || imagePathAndName == null) {
            throw new IllegalArgumentException("parameters cannot be null");
        }
        CompletableFuture<Boolean> isFinishedFuture = new CompletableFuture<>();
        StorageReference fileReference = storage.getReference(imagePathAndName);
        fileReference.putFile(uri).addOnCompleteListener(
                task -> isFinishedFuture.complete(task.isSuccessful()));
        return isFinishedFuture;
    }

    /**
     *  deletes an image from the Firebase Storage
     * @param imagePathAndName this the complete path of the image (e.g. users/default/photo.jpeg)
     */
    @NotNull
    @NonNull
    public CompletableFuture<Boolean> deleteImage(String imagePathAndName) {
        if (imagePathAndName == null) {
            throw new IllegalArgumentException("deleteImage: parameters cannot be null");
        }

        CompletableFuture<Boolean> isFinishedFuture = new CompletableFuture<>();

         /* The reason for this check is when a user has DEFAULT user icon (thus no image in storage)
            The activity asks to delete previous profile image, which is not present, thus returns true directly */
        if (imagePathAndName.contains(DEFAULT_IMAGE_PATH)) {
            isFinishedFuture.complete(true);
            return isFinishedFuture;
        }

        StorageReference storeRef = storage.getReference(imagePathAndName);

        storeRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                isFinishedFuture.complete(true);
            } else {
                isFinishedFuture.completeExceptionally(
                        new DatabaseServiceException(task.getException().getMessage()));
            }
        }
        );

        return isFinishedFuture;
    }
}
