package ch.epfl.sdp.appart.ad;

import android.util.Pair;

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
    private final MutableLiveData<Pair<List<String>, Boolean>> adPhotosReferences =
            new MutableLiveData<>();
    private final MutableLiveData<Pair<List<String>, Boolean>> panoramasReferences =
            new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasVTour = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasLoaded = new MutableLiveData<>();

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
                                                       super Pair<List<String>, Boolean>> observer) {
        panoramasReferences.observe(owner, observer);
    }

    // Getters
    public LiveData<String> getTitle() {
        return adTitle;
    }

    public LiveData<Pair<List<String>, Boolean>> getPhotosRefs() {
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
    public LiveData<Boolean> getHasLoaded() {
        return hasLoaded;
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
        setAdValues(result, localdb.getAd(adId), true);
        return result;
    }

    /**
     * Fetches ad data from the server
     */
    private void fetchAndSet(CompletableFuture<Void> result, String adId) {
        setAdValues(result, db.getAd(adId), false);
    }

    /**
     * Helper to set values from an ad
     */
    private void setAdValues(CompletableFuture<Void> result,
                             CompletableFuture<Ad> adRes, boolean isLocal) {
        adRes.exceptionally(e -> {
            result.completeExceptionally(e);
            return null;
        });
        adRes.thenAccept(ad -> {
            /*
            If the ad is null then the ad isn't stored locally --> it is
            not on disk
            Having a null ad here probably caused a NullPointerException, and
             since this is executed in another
            thread, it just disappeared... And therefore, the future was
            never completed,
            which resulted in fetchAndSet never being called.
            If an ad is null, we can just complete with null directly.
            We could also modify the behavior of the get in the local
            database to maybe
            complete exceptionally when an id is not present in the data
            structure.
            However, this is probably the simplest solution.
            */
            if (ad == null) {
                result.complete(null);
                return;
            }
            this.ad = ad;
            this.adAddress.setValue(addressFrom(ad.getStreet(), ad.getCity()));
            this.adTitle.setValue(ad.getTitle());
            this.adPrice.setValue(priceFrom(ad.getPrice(),
                    ad.getPricePeriod()));
            this.adDescription.setValue(ad.getDescription());
            this.adAdvertiserName.setValue(ad.getAdvertiserName());
            this.adAdvertiserId.setValue(ad.getAdvertiserId());
            this.adPhotosReferences.setValue(new Pair(ad.getPhotosRefs(), isLocal));
            this.panoramasReferences.setValue(new Pair(ad.getPanoramaReferences(), isLocal));
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
            this.hasLoaded.setValue(true);
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
