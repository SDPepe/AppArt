package ch.epfl.sdp.appart;

import javax.annotation.Nullable;

import ch.epfl.sdp.appart.user.User;

public class Card {
    private final String id;
    private final User owner;
    private String city;
    private int price;
    private String imageUrl;

    public Card(User owner, String city, int price, String imageUrl) {
        if (owner == null || city == null || imageUrl == null)
            throw new IllegalArgumentException("Argument is null!");

        this.id = null;
        this.owner = owner;
        this.city = city;
        this.price = price;
        this.imageUrl = imageUrl;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return owner.getUserId();
    }

    public void setImageUrl(String imageUrl) {
        nullChecker(imageUrl);
        this.imageUrl = imageUrl;
    }

    private void nullChecker(Object o) {
        if (o == null)
            throw new IllegalArgumentException("Argument is null!");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Card)) return false;
        Card other = (Card) o;
        if (this.id.equals(other.id)) return true;
        return false;
    }
}
