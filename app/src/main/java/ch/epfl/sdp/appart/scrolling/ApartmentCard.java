package ch.epfl.sdp.appart.scrolling;

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
