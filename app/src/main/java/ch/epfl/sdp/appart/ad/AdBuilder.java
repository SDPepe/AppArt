package ch.epfl.sdp.appart.ad;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.appart.scrolling.PricePeriod;

public final class AdBuilder {

    private String title;
    private long price;
    private PricePeriod pricePeriod;
    private String street;
    private String city;
    private String advertiserId;
    private String description;
    private List<String> photosRefs;
    private boolean hasVRTour;
    private ContactInfo contactInfo;

    public AdBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public AdBuilder withPrice(long price) {
        this.price = price;
        return this;
    }

    public AdBuilder withPricePeriod(PricePeriod period) {
        this.pricePeriod = pricePeriod;
        return this;
    }

    public AdBuilder withStreet(String street) {
        this.street = street;
        return this;
    }

    public AdBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public AdBuilder withAdvertiserId(String advertiserId) {
        this.advertiserId = advertiserId;
        return this;
    }

    public AdBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public AdBuilder withPhotosIds(List<String> photosIds) {
        this.photosRefs = new ArrayList<>();
        this.photosRefs.addAll(photosIds);
        return this;
    }

    public AdBuilder hasVRTour(boolean hasVRTour) {
        this.hasVRTour = hasVRTour;
        return this;
    }

    public AdBuilder withContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
        return this;
    }


}
