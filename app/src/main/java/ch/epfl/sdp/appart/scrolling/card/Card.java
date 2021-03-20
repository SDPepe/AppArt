package ch.epfl.sdp.appart.scrolling.card;

import javax.annotation.Nullable;

public class Card {
    private final String id;
    private final String ownerId;
    private String city;
    private long price;
    private String imageUrl;
    private boolean hasVTour;

    /*
     * A newly created card will be local, hence the id won't be assigned (null). At the first
     * upload to Firestore, a new unique id will be generated and assigned to the card.
     */
    public Card(@Nullable String id, String ownerId, String city, long price, String imageUrl,
                boolean hasVTour) {
        if (ownerId == null || city == null || imageUrl == null)
            throw new IllegalArgumentException("Argument is null!");

        this.id = id;
        this.ownerId = ownerId;
        this.city = city;
        this.price = price;
        this.imageUrl = imageUrl;
        this.hasVTour = hasVTour;
    }

    public Card(@Nullable String id, String ownerId, String city, long price, String imageUrl) {
        this(id, ownerId, city, price, imageUrl, false);
    }

    // Getter Setter
    @Nullable
    public String getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        nullChecker(city);
        this.city = city;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        nullChecker(imageUrl);
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return ownerId;
    }

    public boolean hasVTour(){ return hasVTour; }

    public void setVTour(boolean b) { hasVTour = b; }

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
