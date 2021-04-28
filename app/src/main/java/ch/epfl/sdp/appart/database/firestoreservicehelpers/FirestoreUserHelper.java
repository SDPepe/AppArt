package ch.epfl.sdp.appart.database.firestoreservicehelpers;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.database.firebaselayout.UserLayout;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

/**
 * Helper class to add users to and retrieve them from Firestore.
 */
public class FirestoreUserHelper {

    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final FirestoreImageHelper imageHelper;
    private final String usersPath;

    public FirestoreUserHelper() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        imageHelper = new FirestoreImageHelper();
        usersPath = FirebaseLayout.USERS_DIRECTORY + FirebaseLayout.SEPARATOR;
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
                        //TODO: Handle case where the string does not match a gender
                        AppUser user = new AppUser(userId, (String) data.get(UserLayout.EMAIL));
                        user.setUserEmail((String) data.get(UserLayout.EMAIL));

                        Object rawAge = data.get(UserLayout.AGE);
                        if (rawAge != null) user.setAge((long) rawAge);
                        Object rawGender = data.get(UserLayout.GENDER);
                        if (rawGender != null) user.setGender((String) rawGender);
                        Object rawName = data.get(UserLayout.NAME);
                        if (rawName != null) user.setName((String) rawName);
                        Object rawPhoneNumber = data.get(UserLayout.PHONE);
                        if (rawPhoneNumber != null) user.setPhoneNumber((String) rawPhoneNumber);
                        Object rawPfpRef = data.get(UserLayout.PICTURE);
                        if (rawPfpRef != null)
                            user.setProfileImage((String) rawPfpRef); //WARNING WAS "profilePicture" before not matching our actual

                        result.complete(user);
                    } else {
                        result.completeExceptionally(
                                new DatabaseServiceException(task.getException().getMessage())
                        );
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
                .set(extractUserInfo(user)).addOnCompleteListener(task -> {
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
    public CompletableFuture<Boolean> updateUser(User user, Uri uri) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        return updateUserDb(result, user);
    }

    /* <--- updateUser private methods ---> */

    private CompletableFuture<Boolean> updateUserDb(CompletableFuture<Boolean> res, User user) {
        db.collection(FirebaseLayout.USERS_DIRECTORY)
                .document(user.getUserId())
                .set(extractUserInfo(user))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        res.complete(task.isSuccessful());
                    } else {
                        res.completeExceptionally(new DatabaseServiceException(task.getException().getMessage()));
                    }
                });
        return res;
    }

    /* <--- general util private methods ---> */

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
}
