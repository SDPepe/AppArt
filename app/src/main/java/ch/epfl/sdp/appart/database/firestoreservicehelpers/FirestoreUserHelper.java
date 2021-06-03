package ch.epfl.sdp.appart.database.firestoreservicehelpers;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.serializers.UserSerializer;

/**
 * Helper class to add users to and retrieve them from Firestore.
 */
public class FirestoreUserHelper {

    private final FirebaseFirestore db;

    public FirestoreUserHelper() {
        db = FirebaseFirestore.getInstance();
    }

    @NotNull
    @NonNull
    public CompletableFuture<User> getUser(String userId) {
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
                        result.complete(UserSerializer.deserialize(userId, data));
                    } else {
                        result.completeExceptionally(new DatabaseServiceException(
                                task.getException().getMessage()));
                    }
                }
        );
        return result;
    }

    @NotNull
    @NonNull
    public CompletableFuture<Boolean> putUser(User user) {
        CompletableFuture<Boolean> isFinishedFuture = new CompletableFuture<>();
        db.collection(FirebaseLayout.USERS_DIRECTORY)
                .document(user.getUserId())
                .set(UserSerializer.serialize(user)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                isFinishedFuture.complete(true);
            } else {
                isFinishedFuture.completeExceptionally(new DatabaseServiceException(task.getException().getMessage()));
            }
        });
        return isFinishedFuture;
    }

    @NotNull
    @NonNull
    public CompletableFuture<Boolean> updateUser(User user) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        return updateUserDb(result, user);
    }

    /* <--- updateUser private methods ---> */

    private CompletableFuture<Boolean> updateUserDb(CompletableFuture<Boolean> res, User user) {
        db.collection(FirebaseLayout.USERS_DIRECTORY)
                .document(user.getUserId())
                .set(UserSerializer.serialize(user))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        res.complete(task.isSuccessful());
                    } else {
                        res.completeExceptionally(new DatabaseServiceException(task.getException().getMessage()));
                    }
                });
        return res;
    }

}
