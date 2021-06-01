package ch.epfl.sdp.appart.ad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for the AdActivity.
 * <p>
 * It contains LiveData for the information showed in an Ad page.
 */
@HiltViewModel
public class AdViewModel extends ViewModel {

    final DatabaseService db;
    final LocalDatabaseService localdb;

    private Ad ad;
    private final MutableLiveData<String> adTitle = new MutableLiveData<>();
    private final MutableLiveData<String> adAddress = new MutableLiveData<>();
    private final MutableLiveData<String> adPrice = new MutableLiveData<>();
    private final MutableLiveData<String> adDescription =
            new MutableLiveData<>();
    private final MutableLiveData<String> adAdvertiserName =
            new MutableLiveData<>();
    private final MutableLiveData<String> adAdvertiserId =
            new MutableLiveData<>();
    private final MutableLiveData<List<String>> adPhotosReferences =
            new MutableLiveData<>();
    private final MutableLiveData<List<String>> panoramasReferences =
            new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasVTour = new MutableLiveData<>();

    @Inject
    public AdViewModel(DatabaseService db, LocalDatabaseService localdb) {
        this.db = db;
        this.localdb = localdb;
    }

    /**
     * Loads and sets ad info from the local database. It then tries to fetch
     * the ad info from
     * the database. If successful, sets the ad info with the new data form
     * the server.
     *
     * @param id the unique ID of the ad in the database
     * @return a completable future that is normally completed if the server
     * fetch was successful
     */
    public CompletableFuture<Void> initAd(String id) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        localLoad(id).whenComplete((res, e) -> fetchAndSet(result, id));
        return result;
    }

    public <T> void observePanoramasReferences(LifecycleOwner owner,
                                               @NonNull Observer<?
                                                       super List<String>> observer) {
        panoramasReferences.observe(owner, observer);
    }

    // Getters
    public LiveData<String> getTitle() {
        return adTitle;
    }

    public LiveData<List<String>> getPhotosRefs() {
        return adPhotosReferences;
    }

    public LiveData<String> getAddress() {
        return adAddress;
    }

    public LiveData<String> getPrice() {
        return adPrice;
    }

    public LiveData<String> getDescription() {
        return adDescription;
    }

    public LiveData<String> getAdAdvertiserName() {
        return adAdvertiserName;
    }

    public LiveData<String> getAdvertiserId() {
        return adAdvertiserId;
    }

    public LiveData<Boolean> getHasVTour() {
        return hasVTour;
    }

    @Nullable
    public Ad getAd() {
        return ad;
    }

    /**
     * Loads the ad data from the local database and sets the values to
     * mutablelivedata fields.
     */
    private CompletableFuture<Void> localLoad(String adId) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        setAdValues(result, localdb.getAd(adId));
        return result;
    }

    /**
     * Fetches ad data from the server
     */
    private void fetchAndSet(CompletableFuture<Void> result, String adId) {
        setAdValues(result, db.getAd(adId));
    }

    /**
     * Helper to set values from an ad
     */
    private void setAdValues(CompletableFuture<Void> result,
                             CompletableFuture<Ad> adRes) {
        adRes.exceptionally(e -> {
            result.completeExceptionally(e);
            return null;
        });
        adRes.thenAccept(ad -> {
            this.ad = ad;
            this.adAddress.setValue(addressFrom(ad.getStreet(), ad.getCity()));
            this.adTitle.setValue(ad.getTitle());
            this.adPrice.setValue(priceFrom(ad.getPrice(),
                    ad.getPricePeriod()));
            this.adDescription.setValue(ad.getDescription());
            this.adAdvertiserName.setValue(ad.getAdvertiserName());
            this.adAdvertiserId.setValue(ad.getAdvertiserId());
            this.adPhotosReferences.setValue(ad.getPhotosRefs());
            this.panoramasReferences.setValue(ad.getPanoramaReferences());
            /*
                This is not exactly set as the equivalent of the hasVRTour
                attribute. However, this modification prevent some crashes in
                 the PanoramaActivity.
                 Some ads have the boolean hasVTour at false, while they have
                  panoramaReferences.
                 I think this is because while building an ad you have to
                 explicitly set the hasVTour and this can be forgotten.
                 Maybe here we can only rely on the panoramaReferences size.
             */
            this.hasVTour.setValue(ad.getPanoramaReferences().size() > 0);
            result.complete(null);
        });
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
