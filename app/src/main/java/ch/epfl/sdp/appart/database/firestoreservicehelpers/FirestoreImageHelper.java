package ch.epfl.sdp.appart.database.firestoreservicehelpers;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;

public class FirestoreImageHelper {

    private final FirebaseStorage storage;

    public FirestoreImageHelper() {
        storage = FirebaseStorage.getInstance();
    }

    @NotNull
    @NonNull
    public CompletableFuture<Boolean> putImage(Uri uri, String name, String path) {
        if (uri == null || name == null) {
            throw new IllegalArgumentException("parameters cannot be null");
        }
        CompletableFuture<Boolean> isFinishedFuture = new CompletableFuture<>();
        StorageReference fileReference = storage.getReference(path).child(name);
        fileReference.putFile(uri).addOnCompleteListener(
                task -> isFinishedFuture.complete(task.isSuccessful()));
        return isFinishedFuture;
    }
}
