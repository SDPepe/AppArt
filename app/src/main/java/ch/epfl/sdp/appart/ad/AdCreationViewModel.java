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
    private List<Uri> panoramasUris;

    final DatabaseService db;
    final LoginService ls;

    @Inject
    public AdCreationViewModel(DatabaseService db, LoginService ls) {
        this.db = db;
        this.ls = ls;
        photosUri = new ArrayList<>();
        panoramasUris = new ArrayList<>();
    }

    /**
     * function to use when confirming the creation of the ad
     *
     * @return a completable future that completes normally if the creation is successful, and
     * exceptionally otherwise
     */
    public CompletableFuture<Void> confirmCreation() {
        User user = ls.getCurrentUser();
        Ad ad = new Ad(title, price, pricePeriod, street, city, user.getName(), user.getUserId(), description,
                new ArrayList<>(), new ArrayList<>(), VRTourEnable);
        CompletableFuture<String> result = db.putAd(ad, photosUri, panoramasUris);

        return result.thenApply(s -> {
            user.addAdId(s);
            return null;
        });
    }

    //getters
    public List<Uri> getUri() {
        return photosUri;
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

    public void setPanoramaUri(List<Uri> uris) { panoramasUris = uris; }

    public boolean hasPhotos() {
        return photosUri != null && photosUri.size() >= 1;
    }


}
