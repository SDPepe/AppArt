package ch.epfl.sdp.appart.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ch.epfl.sdp.appart.database.Database;
import dagger.hilt.android.lifecycle.HiltViewModel;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;


@HiltViewModel
public class UserViewModel extends ViewModel {


    private final MutableLiveData<Boolean> mPutCardConfirmed = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mUpdateCardConfirmed = new MutableLiveData<>();
    private final MutableLiveData<User> mUser = new MutableLiveData<>();

    Database db;

    @Inject
    public UserViewModel(Database database) {
        this.db = database;
    }

    /*
     * Put the user in the database and updates the LiveData
     */
    public void putUser(User user) {
        CompletableFuture<Boolean> putUser = db.putUser(user);
        putUser.thenAccept(mPutCardConfirmed::setValue);
    }

    /*
     * Update the user in the database and updates the LiveData
     */
    public void updateUser(User user) {
        CompletableFuture<Boolean> updateUser = db.updateUser(user);
        updateUser.thenAccept(mUpdateCardConfirmed::setValue);
    }

    /*
     * Get the user from the database and updates the LiveData
     */
    public void getUser(String userId) {
        CompletableFuture<User> getUser = db.getUser(userId);
        getUser.thenAccept(mUser::setValue);
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

}
