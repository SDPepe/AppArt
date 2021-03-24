package ch.epfl.sdp.appart.scrolling.ad;

import java.util.List;

public class Ad {

    private String title;
    private String price;
    private String address;
    private String advertiserId;
    private String description;
    private List<String> photosRefs;
    private boolean hasVRTour;
    private ContactInfo contactInfo;

    public Ad(String title, String price, String address,
              String advertiserId, String description, List<String> photosRefs, boolean hasVRTour) {

        if (title == null || price == null || address == null)
            throw new IllegalArgumentException("An argument is null!");
        if (advertiserId == null || description == null || photosRefs == null)
            throw new IllegalArgumentException("An argument is null!");

        this.title = title;
        this.price = price;
        this.advertiserId = advertiserId;
        this.address = address;
        this.description = description;
        this.photosRefs = photosRefs;
        this.hasVRTour = hasVRTour;
    }

    public Ad(String title, String price, String address,
              String advertiserId, String description, List<String> photosRefs) {
        this(title, price, address, advertiserId, description, photosRefs, false);
    }

    public Ad(String title, String price, String address,
              String advertiserId, String description, List<String> photosRefs, boolean hasVRTour, ContactInfo contactInfo) {

        if (title == null || price == null || address == null)
            throw new IllegalArgumentException("An argument is null!");
        if (advertiserId == null || description == null || photosRefs == null)
            throw new IllegalArgumentException("An argument is null!");
        if(contactInfo == null) {
            throw new IllegalArgumentException();
        }

        this.title = title;
        this.price = price;
        this.advertiserId = advertiserId;
        this.address = address;
        this.description = description;
        this.photosRefs = photosRefs;
        this.hasVRTour = hasVRTour;
        this.contactInfo = contactInfo;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }

    public String getAdvertiserId() {
        return advertiserId;
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
