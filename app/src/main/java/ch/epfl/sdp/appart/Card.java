package ch.epfl.sdp.appart;

import android.media.Image;

import ch.epfl.sdp.appart.user.User;

public class Card {
    private final int id;
    private final User owner;
    private String city;
    private String price;
    private Image image;

    public Card(int id, User owner, String city, String price, Image image) {
        if (owner == null || city == null || price == null /*|| image == null*/)
            throw new IllegalArgumentException("Argument is null!");

        this.id = id;
        this.owner = owner;
        this.city = city;
        this.price = price;
        this.image = image;
    }

    // Getter Setter
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        nullChecker(city);
        this.city = city;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        nullChecker(price);
        this.price = price;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        nullChecker(image);
        this.image = image;
    }

    private void nullChecker(Object o) {
        if (o == null)
            throw new IllegalArgumentException("Argument is null!");
    }
}
