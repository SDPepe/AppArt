package ch.epfl.sdp.appart.user;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.utils.StoragePathBuilder;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.grpc.Context;


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

    @Inject
    public UserViewModel(DatabaseService database, LoginService loginService) {

        this.db = database;
        this.ls = loginService;
    }

    // TODO is this needed?
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
        updateUser.thenAccept(b -> {
            CompletableFuture<Void> localSaveResult = new CompletableFuture<>();
            // TODO call localDB API to save new user info, setValue according to result
            mUpdateUserConfirmed.setValue(b);
        });
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
        updateImage.thenAccept(b -> {
            // TODO use localDB API to save image locally, then setValue according to result
            mUpdateImageConfirmed.setValue(b);
        });
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
        deleteImage.thenAccept(b -> {
            // TODO use localDB API to delete local image, then setValue according to result
            mDeleteImageConfirmed.setValue(b);
        });
    }

    /**
     * Get the user from the database and updates the LiveData
     *
     * @param userId the unique Id of the user to retrieve from database
     */
    public void getUser(String userId) {
        User user = /*localDB.getUser(userId)*/ new AppUser("replace", "this");
        if (user != null) mUser.setValue(user);

        CompletableFuture<User> getUser = db.getUser(userId);
        getUser.exceptionally(e -> {
            Log.d("GET USER", "DATABASE FAIL");
            // if no user in localDB and server fetch failed, set value to null
            if (user == null) mUser.setValue(null);
            return null;
        });
        // if server fetch worked, update user in localDB
        getUser.thenAccept( u -> {
            // TODO use localDB API to update user stored locally
            mUser.setValue(u);
        });
    }

    /**
     * Get the current user from the database and updates the LiveData
     */
    public void getCurrentUser() {
        User user = /*localDB.getCurrentUser()*/ new AppUser("replace", "this");
        if (user != null) mUser.setValue(user);

        CompletableFuture<User> getCurrentUser = db.getUser(ls.getCurrentUser().getUserId());
        getCurrentUser.exceptionally(e -> {
            Log.d("GET USER", "DATABASE FAIL");
            // if no currentUser in localDB and server fetch failed, set value to null
            if (user == null) mUser.setValue(null);
            return null;
        });
        // if server fetch worked, update user in localDB
        getCurrentUser.thenAccept( cu -> {
            // TODO use localDB API to update current user stored locally
            mUser.setValue(cu);
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
