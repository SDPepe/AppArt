package ch.epfl.sdp.appart.ad;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.ContactInfo;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AdCreationViewModel extends ViewModel {

    private final MutableLiveData<String> title = new MutableLiveData<>();
    private final MutableLiveData<String> street = new MutableLiveData<>();
    private final MutableLiveData<String> city = new MutableLiveData<>();
    private final MutableLiveData<Long> price = new MutableLiveData<>();
    private final MutableLiveData<PricePeriod> pricePeriod = new MutableLiveData<>();
    private final MutableLiveData<String> description = new MutableLiveData<>();
    // refs to files stored on the device. FirebaseDB will take care of loading them and uploading
    private final MutableLiveData<List<String>> photosRefs = new MutableLiveData<>();
    private final MutableLiveData<Boolean> VRTourEnable = new MutableLiveData<>();

    final DatabaseService db;
    final LoginService ls;

    @Inject
    public AdCreationViewModel(DatabaseService db, LoginService ls) {
        this.db = db;
        this.ls = ls;
    }

    /**
     * function to use when confirming the creation of the ad
     * @return a completable future with true if the ad has been successfully added to the database,
     *  false if an exception was thrown
     */
    public CompletableFuture<Boolean> confirmCreation() {
        User user = ls.getCurrentUser();
        ContactInfo ci = new ContactInfo(user.getUserEmail(), user.getPhoneNumber(), user.getName());
        Ad ad = new Ad(title.getValue(), price.getValue(), pricePeriod.getValue(), street.getValue(),
                city.getValue(), user.getUserId(), description.getValue(), photosRefs.getValue(),
                VRTourEnable.getValue(), ci);
        CompletableFuture<String> result = db.putAd(ad);
        return result.thenApply(s -> {
            user.addAdId(s);
            return true;
        }).exceptionally(e -> false);

    }

    public void setTitle(String s) {
        title.postValue(s);
    }

    public void setStreet(String s) {
        street.postValue(s);
    }

    public void setCity(String s) {
        city.postValue(s);
    }

    public void setPrice(Long l) {
        price.postValue(l);
    }

    public void setPricePeriod(PricePeriod p) {
        pricePeriod.postValue(p);
    }

    public void setDescription(String s) {
        description.postValue(s);
    }

    public void setPhotosRefs(List<String> ls) {
        photosRefs.postValue(ls);
    }

    public void setVRTourEnable(boolean b) { VRTourEnable.postValue(Boolean.valueOf(b)); }

}
