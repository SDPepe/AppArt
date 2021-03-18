package ch.epfl.sdp.appart.scrolling.ad;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.Database;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AnnounceViewModel extends ViewModel {

    private final MutableLiveData<String> adTitle = new MutableLiveData<>();
    private final MutableLiveData<String> adAddress = new MutableLiveData<>();
    private final MutableLiveData<String> adPrice = new MutableLiveData<>();
    private final MutableLiveData<String> adDescription = new MutableLiveData<>();
    private final MutableLiveData<String> adAdvertiser = new MutableLiveData<>(); // name of user

    Database db;

    @Inject
    public AnnounceViewModel(Database db) { this.db = db; }

    // TODO query db for ad and user data, and fill livedata
    public void initAd(){}

    public LiveData<String> getTitle(){ return adTitle; }

    public LiveData<String> getAddress(){ return adAddress; }

    public LiveData<String> getPrice(){ return adPrice; }

    public LiveData<String> getDescription(){ return adDescription; }

    public LiveData<String> getAdvertiser() { return adAdvertiser; }

}
