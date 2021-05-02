package ch.epfl.sdp.appart.ad;

import java.util.ArrayList;
import java.util.List;

/**
 * Object representing an ad of an apartment.
 * <p>
 * It contains all the apartment info and some info about the user that created the ad. For the
 * moment an ad cannot be modified after creation, so the class does not offer setters.
 */
public class Ad {

    private final String title;
    private final long price;
    private final PricePeriod pricePeriod;
    private final String street;
    private final String city;
    private final String advertiserId;
    private final String description;
    private final List<String> photosReferences;
    private final List<String> panoramaReferences;
    private final boolean hasVRTour;

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
     */
    public Ad(String title, long price, PricePeriod pricePeriod, String street, String city,
              String advertiserId, String description, List<String> photosRefs, List<String> panoramaReferences, boolean hasVRTour) {
        if (title == null || pricePeriod == null || street == null || city == null ||
                advertiserId == null || description == null || photosRefs == null)
            throw new IllegalArgumentException("An argument is null!");

        this.title = title;
        this.price = price;
        this.pricePeriod = pricePeriod;
        this.advertiserId = advertiserId;
        this.street = street;
        this.city = city;
        this.description = description;
        this.photosReferences = photosRefs;
        this.panoramaReferences = panoramaReferences;
        this.hasVRTour = hasVRTour;
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
        return photosReferences;
    }

    public boolean hasVRTour() {
        return hasVRTour;
    }


    /**
     * A inner Builder class to avoid the creation of an Ad
     * over a period of time and not only instantaneously.
     * It also makes the ad creation less painful.
     */
    public static class AdBuilder {

        private String title;
        private long price;
        private PricePeriod pricePeriod;
        private String street;
        private String city;
        private String advertiserId;
        private String description;
        private List<String> photosRefs;
        private List<String> panoramaReferences;
        private boolean hasVRTour;

        public AdBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public AdBuilder withPrice(long price) {
            this.price = price;
            return this;
        }

        public AdBuilder withPricePeriod(PricePeriod pricePeriod) {
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


        public AdBuilder withPicturesReferences(List<String> photosIds) {
            this.photosRefs = new ArrayList<>();
            this.photosRefs.addAll(photosIds);
            return this;
        }

        public AdBuilder withPanoramaReferences(List<String> panoramaReferences) {
            this.panoramaReferences = new ArrayList<>();
            this.panoramaReferences.addAll(panoramaReferences);
            return this;
        }

        public AdBuilder hasVRTour(boolean hasVRTour) {
            this.hasVRTour = hasVRTour;
            return this;
        }

        public Ad build() {

            return new Ad(
                    title,
                    price,
                    pricePeriod,
                    street,
                    city,
                    advertiserId,
                    description,
                    photosRefs,
                    panoramaReferences,
                    hasVRTour
            );
        }

        public String getAdvertiserId() {
            return advertiserId;
        }

    }



}
