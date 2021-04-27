package ch.epfl.sdp.appart.user;

import android.net.Uri;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.ContactInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.login.LoginService;
import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class UserViewModel extends ViewModel {

    private final MutableLiveData<Boolean> mPutCardConfirmed = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mUpdateCardConfirmed = new MutableLiveData<>();
    private final MutableLiveData<User> mUser = new MutableLiveData<>();

    private final MutableLiveData<Uri> mUri = new MutableLiveData<>();

    final DatabaseService db;
    final LoginService ls;

    @Inject
    public UserViewModel(DatabaseService database, LoginService loginService) {

        this.db = database;
        this.ls = loginService;
    }


    /**
     * Puts the user in the database and updates the LiveData
     *
     * @param user the user to store in database
     */
    public void putUser(User user) {
        CompletableFuture<Boolean> putUser = db.putUser(user);
        putUser.thenAccept(mPutCardConfirmed::setValue);
    }

    /**
     * Update the user in the database and updates the LiveData
     *
     * @param user the user to update in database
     */
    public void updateUser(User user) {
        CompletableFuture<Boolean> updateUser = db.updateUser(user, mUri.getValue());
        updateUser.thenAccept(mUpdateCardConfirmed::setValue);
    }

    /**
     * Get the user from the database and updates the LiveData
     *
     * @param userId the unique Id of the user to retrieve from database
     */
    public void getUser(String userId) {
        CompletableFuture<User> getUser = db.getUser(userId);
        getUser.thenAccept(mUser::setValue);
    }

    /**
     * Get the current user from the database and updates the LiveData
     */
    public void getCurrentUser() {
        CompletableFuture<User> getCurrentUser = db.getUser(ls.getCurrentUser().getUserId());
        getCurrentUser.thenAccept(mUser::setValue);
    }
    /*
     * Setters
     */
    public void setUri(Uri uri ){
      mUri.setValue(uri);
    }

    /*
     * Getters for MutableLiveData instances declared above
     */
    public MutableLiveData<Boolean> getPutCardConfirmed() {
        return mPutCardConfirmed;
    }

    public MutableLiveData<Boolean> getUpdateCardConfirmed() {
        return mUpdateCardConfirmed;
    }

    public MutableLiveData<User> getUser() {
        return mUser;
    }

    public MutableLiveData<Uri> getUri() {
        return mUri;
    }

}
