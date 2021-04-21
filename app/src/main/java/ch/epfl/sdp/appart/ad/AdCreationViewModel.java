package ch.epfl.sdp.appart.ad;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for the ad creation activity.
 * <p>
 * It adds to the database a new ad with the information provided by the AdCreationActivity.
 */
@HiltViewModel
public class AdCreationViewModel extends ViewModel {

    private String title;
    private String street;
    private String city;
    private long price;
    private PricePeriod pricePeriod;
    private String description;
    private boolean VRTourEnable;
    private List<Uri> photosUri;

    final DatabaseService db;
    final LoginService ls;

    @Inject
    public AdCreationViewModel(DatabaseService db, LoginService ls) {
        this.db = db;
        this.ls = ls;
        photosUri = new ArrayList<>();
    }

    /**
     * function to use when confirming the creation of the ad
     *
     * @return a completable future with true if the ad has been successfully added to the database,
     * false if an exception was thrown
     */
    public CompletableFuture<Boolean> confirmCreation() {
        User user = ls.getCurrentUser();
        ContactInfo ci = new ContactInfo(user.getUserEmail(), user.getPhoneNumber(), user.getName());
        Ad ad = new Ad(title, price, pricePeriod, street, city, user.getUserId(), description,
                new ArrayList<>(), VRTourEnable, ci);
        CompletableFuture<String> result = db.putAd(ad, photosUri);
        return result.thenApply(s -> {
            user.addAdId(s);
            return true;
        }).exceptionally(e -> false);

    }
    //getters
    public Uri getUri(){
        if (photosUri != null && photosUri.size() > 1) {
            return photosUri.get(0);
        } else {
            return null;}
    }

    // setters
    public void setTitle(String s) {
        title = s;
    }

    public void setStreet(String s) {
        street = s;
    }

    public void setCity(String s) {
        city = s;
    }

    public void setPrice(long l) {
        price = l;
    }

    public void setPricePeriod(PricePeriod p) {
        pricePeriod = p;
    }

    public void setDescription(String s) {
        description = s;
    }

    public void setVRTourEnable(boolean b) {
        VRTourEnable = b;
    }

    public void setUri(List<Uri> uri) {
        photosUri = uri;
    }

    public boolean hasPhotos() {
        return photosUri != null && photosUri.size() >= 1;
    }



}
