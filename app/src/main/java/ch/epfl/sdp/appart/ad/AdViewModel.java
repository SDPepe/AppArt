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
import ch.epfl.sdp.appart.database.local.LocalDatabase;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlin.NotImplementedError;


/**
 * ViewModel for the AdActivity.
 * <p>
 * It contains LiveData for the information showed in an Ad page.
 */
@HiltViewModel
public class AdViewModel extends ViewModel {

    final DatabaseService db;
    final LocalDatabase localdb;
    private final MutableLiveData<String> adTitle = new MutableLiveData<>();
    private final MutableLiveData<String> adAddress = new MutableLiveData<>();
    private final MutableLiveData<String> adPrice = new MutableLiveData<>();
    private final MutableLiveData<String> adDescription = new MutableLiveData<>();
    private final MutableLiveData<String> adAdvertiserName = new MutableLiveData<>();
    private final MutableLiveData<String> adAdvertiserId = new MutableLiveData<>();
    private final MutableLiveData<List<String>> adPhotosReferences = new MutableLiveData<>();
    private final MutableLiveData<List<String>> panoramasReferences = new MutableLiveData<>();

    @Inject
    public AdViewModel(DatabaseService db, LocalDatabase localdb) {
        this.db = db;
        this.localdb = localdb;
    }

    /**
     * Fetches the ad info from the database and sets the information to the LiveData fields. If the
     * activity was opened from the favorites page, load the data from the local db first and then
     * fetch from server to ensure latest data is shown.
     *
     * @param id the unique ID of the ad in the database
     * @param fromFavorites
     * @return a completable future to let the activity know if the action was successful
     */
    public CompletableFuture<Void> initAd(String id, Boolean fromFavorites) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        CompletableFuture<Void> loadResult = new CompletableFuture<>();
        if (fromFavorites)
            localLoad(loadResult, id);
        else
            loadResult.complete(null);

        // even if local load fails, try to load from server
        loadResult.whenComplete((e,res) -> fetchAndSet(result, id));
        return result;
    }

    public <T> void observePanoramasReferences(LifecycleOwner owner, @NonNull Observer<? super List<String>> observer) {
        panoramasReferences.observe(owner, observer);
    }
    // Getters
    public LiveData<String> getTitle() { return adTitle; }

    public LiveData<List<String>> getPhotosRefs() { return adPhotosReferences; }

    public LiveData<String> getAddress() { return adAddress; }

    public LiveData<String> getPrice() { return adPrice; }

    public LiveData<String> getDescription() { return adDescription; }

    public LiveData<String> getAdAdvertiserName() { return adAdvertiserName; }

    public LiveData<String> getAdvertiserId() { return adAdvertiserId; }

    /**
     * Loads data from the local DB
     */
    private void localLoad(CompletableFuture<Void> result, String adId){
        localdb.getAd(adId)
                .exceptionally( e -> {
                    result.completeExceptionally(e);
                    return null;
                })
                .thenAccept( ad -> {
                    setAdValues(ad);
                    result.complete(null);
                });
        throw new NotImplementedError();
    }

    /**
     * Fetches ad data from the server
     */
    private void fetchAndSet(CompletableFuture<Void> result, String adId) {
        db.getAd(adId)
                .exceptionally(e -> {
                    Log.d("ANNOUNCE", "DATABASE FAIL");
                    result.completeExceptionally(e);
                    return null;
                })
                .thenAccept(ad -> {
                    setAdValues(ad);
                    result.complete(null);
                });
    }

    /**
     * Helper to set values from an ad
     */
    private void setAdValues(Ad ad){
        this.adAddress.setValue(addressFrom(ad.getStreet(), ad.getCity()));
        this.adTitle.setValue(ad.getTitle());
        this.adPrice.setValue(priceFrom(ad.getPrice(), ad.getPricePeriod()));
        this.adDescription.setValue(ad.getDescription());
        this.adAdvertiserName.setValue(ad.getAdvertiserName());
        this.adAdvertiserId.setValue(ad.getAdvertiserId());
        this.adPhotosReferences.setValue(ad.getPhotosRefs());
        this.panoramasReferences.setValue(ad.getPanoramaReferences());
    }

    /**
     * Helper to concatenate an address string
     */
    private String addressFrom(String street, String city) {
        return street + ", " + city;
    }

    /**
     * Helper to concatenate a price
     */
    private String priceFrom(long price, PricePeriod period) {
        return price + " / " + period.toString();
    }
}
