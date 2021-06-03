package ch.epfl.sdp.appart.user;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.utils.DatabaseSync;
import ch.epfl.sdp.appart.utils.StoragePathBuilder;
import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class UserViewModel extends ViewModel {

    private final MutableLiveData<Boolean> mPutUserConfirmed = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mUpdateUserConfirmed = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mUpdateImageConfirmed = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mDeleteImageConfirmed = new MutableLiveData<>();
    private final MutableLiveData<User> mUser = new MutableLiveData<>();

    private Uri profileImageUri;

    final DatabaseService db;
    final LoginService ls;
    final LocalDatabaseService localdb;

    @Inject
    public UserViewModel(DatabaseService database, LoginService loginService,
                         LocalDatabaseService localdb) {

        this.db = database;
        this.ls = loginService;
        this.localdb = localdb;
    }

    /**
     * Puts the user in the database and updates the LiveData
     *
     * @param user the user to store in database
     */
    public void putUser(User user) {
        CompletableFuture<Boolean> putUser = db.putUser(user);
        putUser.exceptionally(e -> {
            Log.d("PUT USER", "DATABASE FAIL");
            return null;
        });
        putUser.thenAccept(mPutUserConfirmed::setValue);
    }

    /**
     * Update the user in the database and updates the LiveData
     *
     * @param user the user to update in database
     */
    public void updateUser(User user) {
        CompletableFuture<Boolean> updateUser = db.updateUser(user);
        updateUser.exceptionally(e -> {
            Log.d("UPDATE USER", "DATABASE FAIL");
            return null;
        });
        updateUser.thenAccept(mUpdateUserConfirmed::setValue);
    }

    /**
     * Update the user image the database and updates the LiveData
     * the uri for the image is stored in profileImageUri attribute above
     *
     * @param userId the id of the user
     */
    public void updateImage(String userId) {
        String imagePathAndName = new StoragePathBuilder()
                .toUsersStorageDirectory()
                .toDirectory(userId)
                .withFile(FirebaseLayout.PROFILE_IMAGE_NAME +
                        System.currentTimeMillis() +
                        FirebaseLayout.JPEG);

        CompletableFuture<Boolean> updateImage = db.putImage(profileImageUri, imagePathAndName);
        updateImage.exceptionally(e -> {
            Log.d("UPDATE IMAGE", "DATABASE FAIL");
            return null;
        });
        updateImage.thenAccept(mUpdateImageConfirmed::setValue);
    }

    /**
     * Deletes the user image the database and updates the LiveData
     * !! USE - user.getProfileImage() when calling this method
     *
     * @param profilePicture this is the complete path for the user's image: user.getProfileImage()
     */
    public void deleteImage(String profilePicture) {
        CompletableFuture<Boolean> deleteImage = db.deleteImage(profilePicture);
        deleteImage.exceptionally(e -> {
            Log.d("DELETE IMAGE", "DATABASE FAIL");
            return null;
        });
        deleteImage.thenAccept(mDeleteImageConfirmed::setValue);
    }

    /**
     * Get the user from the database and updates the LiveData.
     *
     * @param userId the unique Id of the user to retrieve from database
     * @return a completable future telling whether the operation was successful
     */
    public CompletableFuture<Void> getUser(String userId) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        CompletableFuture<User> localUserRes = localdb.getUser(userId);
        localUserRes.exceptionally(e -> {
            getFromDBAndSetUser(userId, result);
            return null;
        });
        localUserRes.thenAccept(u -> {
            if (u != null) mUser.setValue(u);
            getFromDBAndSetUser(userId, result);
        });
        return result;
    }

    /**
     * Gets the current user and updates the values with the user info.
     * <p>
     * It first tries to load the user from the local database. Independently from the result,
     * it then tries to fetch the user from the database.
     *
     * @return a completable future telling if the server fetch was successful
     */
    public CompletableFuture<Void> getCurrentUser() {
        CompletableFuture<Void> result = new CompletableFuture<>();
        User currentUser;
        try {
            currentUser = localdb.getCurrentUser();
        } catch (IllegalStateException e){
            e.printStackTrace();
            currentUser = null;
        }

        if (currentUser != null)
            mUser.setValue(currentUser);

        currentUser = ls.getCurrentUser();
        if (currentUser != null) {
            getFromDBAndSetUser(currentUser.getUserId(), result);
        } else
            result.completeExceptionally(new IllegalStateException("Current user cannot be null!"));

        return result;
    }

    private void getFromDBAndSetUser(String userId, CompletableFuture<Void> result) {
        CompletableFuture<User> userRes = db.getUser(userId);
        userRes.exceptionally(e -> {
            Log.d("USER_VM", "Failed to fetch user from DB");
            result.completeExceptionally(e);
            return null;
        });
        userRes.thenAccept(cu -> {
            mUser.setValue(cu);
            result.complete(null);
        });
    }

    /*
     * Getters for MutableLiveData instances declared above
     */
    public MutableLiveData<Boolean> getPutUserConfirmed() {
        return mPutUserConfirmed;
    }

    public MutableLiveData<Boolean> getUpdateUserConfirmed() {
        return mUpdateUserConfirmed;
    }

    public MutableLiveData<Boolean> getUpdateImageConfirmed() {
        return mUpdateImageConfirmed;
    }

    public MutableLiveData<Boolean> getDeleteImageConfirmed() {
        return mDeleteImageConfirmed;
    }

    public MutableLiveData<User> getUser() {
        return mUser;
    }

    /*
     * Setters
     */
    public void setUri(Uri uri) {
        profileImageUri = uri;
    }

    /*
     * Getters
     */
    public Uri getUri() {
        return profileImageUri;
    }

}
