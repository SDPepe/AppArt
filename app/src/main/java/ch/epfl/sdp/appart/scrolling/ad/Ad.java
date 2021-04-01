package ch.epfl.sdp.appart.scrolling.ad;

import java.util.List;

import ch.epfl.sdp.appart.scrolling.PricePeriod;

/**
 * Object representing an ad of an apartment.
 * <p>
 * It contains all the apartment info and some info about the user that created the ad. For the
 * moment an ad cannot be modified after creation, so the class does not offer setters.
 */
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

    /**
     * Constructor of an ad.
     *
     * @param title        the title of the ad
     * @param price        the price of the apartment
     * @param pricePeriod  the time period the price refers to
     * @param street       the street address of the apartment
     * @param city         the city address of the apartment
     * @param advertiserId the unique ID of the user that created the ad
     * @param description  the description of the ad
     * @param photosRefs   the list of references to the ad images
     * @param hasVRTour    whether the apartment offers a VR tour
     * @param contactInfo  name, phone number and email address of the user that created the ad
     */
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

    // Getters
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
