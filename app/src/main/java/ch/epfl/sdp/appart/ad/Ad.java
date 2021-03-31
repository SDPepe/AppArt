package ch.epfl.sdp.appart.ad;

import java.util.List;

import ch.epfl.sdp.appart.scrolling.PricePeriod;

public class Ad {

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

    public Ad(String title, long price, PricePeriod pricePeriod, String street, String city,
              String advertiserId, String description, List<String> photosRefs, boolean hasVRTour,
              ContactInfo contactInfo) {
        if (title == null || pricePeriod == null || street == null || city == null ||
                advertiserId == null || description == null || photosRefs == null ||
                contactInfo == null)
            throw new IllegalArgumentException("An argument is null!");

        this.title = title;
        this.price = price;
        this.pricePeriod = pricePeriod;
        this.advertiserId = advertiserId;
        this.street = street;
        this.city = city;
        this.description = description;
        this.photosRefs = photosRefs;
        this.hasVRTour = hasVRTour;
        this.contactInfo = contactInfo;
    }

    public String getTitle() {
        return title;
    }

    public long getPrice() {
        return price;
    }

    public PricePeriod getPricePeriod() {
        return pricePeriod;
    }

    public String getAdvertiserId() {
        return advertiserId;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getPhotosRefs() {
        return photosRefs;
    }

    public boolean hasVRTour() {
        return hasVRTour;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }
}
