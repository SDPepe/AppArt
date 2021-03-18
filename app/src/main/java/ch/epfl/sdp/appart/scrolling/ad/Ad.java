package ch.epfl.sdp.appart.scrolling.ad;

import java.util.List;

public class Ad {

    String title;
    String price;
    String address;
    String advertiserId;
    String description;
    List<String> photosRefs;

    public Ad(String title, String price, String address,
        String advertiserId, String description, List<String> photosRefs){
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
    }

    public String getTitle()            { return title; }
    public String getPrice()            { return price; }
    public String getAddress()          { return address; }
    public String getAdvertiserId()     { return advertiserId; }
    public String getDescription()      { return description; }
    public List<String> getPhotosRefs() { return photosRefs; }
}
