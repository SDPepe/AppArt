package ch.epfl.sdp.appart.scrolling.card;

import javax.annotation.Nullable;

/**
 * Object to represent a tile in the scrolling activity.
 * <p>
 * It contains a subset of information related to an ad. The card ID refers to the ID of the
 * Firestore document containing data of the card.
 */
public class Card {
    private final String adId;
    private final String id;
    private final String ownerId;
    private String city;
    private long price;
    private String imageUrl;
    private boolean hasVRTour;

    /**
     * Constructor of a Card.
     * <p>
     * The id is nullable because a locally created card will need the upload to Firestore to
     * receive its unique ID.
     *
     * @param id        the unique identifier of the card using by Firestore
     * @param ownerId   the unique id of the user that created the ad this card refers to
     * @param city      the city where lies the apartment this card refers to
     * @param price     the price of the apartment this card refers to
     * @param imageUrl  the reference to the Firebase Storage image of this card
     * @param hasVRTour whether the apartment this card refers to offers a virtual tour
     */
    public Card(@Nullable String id, String adId, String ownerId, String city, long price, String imageUrl,
                boolean hasVRTour) {
        if (ownerId == null || city == null || imageUrl == null)
            throw new IllegalArgumentException("Argument is null!");

        this.id = id;
        this.ownerId = ownerId;
        this.city = city;
        this.price = price;
        this.imageUrl = imageUrl;
        this.hasVRTour = hasVRTour;
        this.adId = adId;
    }

    public Card(@Nullable String id, String adId, String ownerId, String city, long price, String imageUrl) {
        this(id, adId, ownerId, city, price, imageUrl, false);
    }

    // Getters
    @Nullable
    public String getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public long getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return ownerId;
    }

    public String getAdId() {
        return adId;
    }

    public boolean hasVRTour() {
        return hasVRTour;
    }

    // Setters
    public void setCity(String city) {
        nullChecker(city);
        this.city = city;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        nullChecker(imageUrl);
        this.imageUrl = imageUrl;
    }

    public void setVRTour(boolean b) {
        hasVRTour = b;
    }

    private void nullChecker(Object o) {
        if (o == null)
            throw new IllegalArgumentException("Argument is null!");
    }

    @Override
    public boolean equals(Object o) {
        if (this.id == null) return false;
        if (o == null || !(o instanceof Card)) return false;
        Card other = (Card) o;
        return this.id.equals(other.id);
    }
}
