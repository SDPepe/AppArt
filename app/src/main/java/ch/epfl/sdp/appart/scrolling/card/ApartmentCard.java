package ch.epfl.sdp.appart.scrolling.card;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;

public class ApartmentCard {


    private String city;
    private int price;
    private int imageId;

    public ApartmentCard(int imageId, String city, int price) {

        if (city == null) {
            throw new IllegalArgumentException("city cannot be null");
        }

        if (price < 1) {
            throw new IllegalArgumentException("price has to be grater than 0");
        }

        this.imageId = imageId;
        this.city = city;
        this.price = price;

    }


    public String getCity() {
        return city;
    }

    public int getPrice() {
        return price;
    }

    public int getImageId() {
        return imageId;
    }

}
