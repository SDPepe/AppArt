package ch.epfl.sdp.appart.ad;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import dagger.hilt.android.lifecycle.HiltViewModel;


/**
 * ViewModel for the AdActivity.
 * <p>
 * It contains LiveData for the information showed in an Ad page.
 */
@HiltViewModel
public class AdViewModel extends ViewModel {

    final DatabaseService db;
    private final MutableLiveData<String> adTitle = new MutableLiveData<>();
    private final MutableLiveData<String> adAddress = new MutableLiveData<>();
    private final MutableLiveData<String> adPrice = new MutableLiveData<>();
    private final MutableLiveData<String> adDescription = new MutableLiveData<>();
    private final MutableLiveData<String> adAdvertiserName = new MutableLiveData<>();
    private final MutableLiveData<String> adAdvertiserId = new MutableLiveData<>();
    private final MutableLiveData<List<String>> adPhotosRefs = new MutableLiveData<>();
    private final MutableLiveData<List<String>> panoramasReferences = new MutableLiveData<>();

    @Inject
    public AdViewModel(DatabaseService db) {
        this.db = db;
    }

    /**
     * Fetches the ad info from the database and sets the information to the LiveData fields.
     *
     * @param id the unique ID of the ad in the database
     */
    public void initAd(String id) {

        CompletableFuture<Ad> futureAd = db.getAd(id);
        futureAd.exceptionally(e -> {
            Log.d("ANNOUNCE", "DATABASE FAIL");
            return null;
        });
        futureAd.thenAccept(ad -> {
            this.adAddress.setValue(ad.getStreet() + ", " + ad.getCity());
            this.adTitle.setValue(ad.getTitle());
            this.adPrice.setValue(ad.getPrice() + " / " + ad.getPricePeriod().toString());
            this.adDescription.setValue(ad.getDescription());
            this.adAdvertiserName.setValue(ad.getAdvertiserName());
            this.adAdvertiserId.setValue(ad.getAdvertiserId());

            this.adPhotosRefs.setValue(ad.getPhotosRefs());
            this.panoramasReferences.setValue(ad.getPanoramaReferences());
        });
    }

    public <T> void observePanoramasReferences(LifecycleOwner owner, @NonNull Observer<? super List<String>> observer) {
        panoramasReferences.observe(owner, observer);
    }
    // Getters
    public LiveData<String> getTitle() { return adTitle; }

    public LiveData<List<String>> getPhotosRefs() { return adPhotosRefs; }

    public LiveData<String> getAddress() { return adAddress; }

    public LiveData<String> getPrice() { return adPrice; }

    public LiveData<String> getDescription() { return adDescription; }

    public LiveData<String> getAdAdvertiserName() { return adAdvertiserName; }

    public LiveData<String> getAdvertiserId() { return adAdvertiserId; }

}
