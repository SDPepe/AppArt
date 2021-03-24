package ch.epfl.sdp.appart.scrolling.ad;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.database.Database;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AnnounceViewModel extends ViewModel {

    private final MutableLiveData<String> adTitle = new MutableLiveData<>();
    private final MutableLiveData<String> adAddress = new MutableLiveData<>();
    private final MutableLiveData<String> adPrice = new MutableLiveData<>();
    private final MutableLiveData<String> adDescription = new MutableLiveData<>();
    private final MutableLiveData<String> adAdvertiser = new MutableLiveData<>(); // name of user
    private final MutableLiveData<List<String>> adPhotosRefs = new MutableLiveData<>();
    private final MutableLiveData<String> userPhone = new MutableLiveData<>();
    private final MutableLiveData<String> userEmail = new MutableLiveData<>();

    Database db;

    @Inject
    public AnnounceViewModel(Database db) {
        this.db = db;
    }

    public void initAd(String id){

        CompletableFuture<Ad> futureAd = db.getAd(id);
        futureAd.exceptionally(e -> {
            Log.d("ANNOUNCE", "DATABASE FAIL");
            return null;
        });
        futureAd.thenAccept(ad -> {
            this.adAddress.setValue(ad.getAddress());
            this.adTitle.setValue(ad.getTitle());
            this.adPrice.setValue(ad.getPrice());
            this.adDescription.setValue(ad.getDescription());
            this.adAdvertiser.setValue(ad.getContactInfo().name);
            this.adPhotosRefs.setValue(ad.getPhotosRefs());
            this.userEmail.setValue(ad.getContactInfo().userEmail);
            this.userPhone.setValue(ad.getContactInfo().userPhoneNumber);
        });
    }

    public LiveData<String> getTitle(){ return adTitle; }

    public LiveData<List<String>> getPhotosRefs(){ return adPhotosRefs; }

    public LiveData<String> getAddress(){ return adAddress; }

    public LiveData<String> getPrice(){ return adPrice; }

    public LiveData<String> getDescription(){ return adDescription; }

    public LiveData<String> getAdvertiser() { return adAdvertiser; }

    public LiveData<String> getPhoneNumber() { return userPhone; }

    public LiveData<String> getEmailAddress() { return userEmail; }

}
