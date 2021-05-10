package ch.epfl.sdp.appart.utils.serializers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;

public class AdSerializer {

    //To prevent construction
    private AdSerializer() {
    }

    public final static String ADVERTISER_NAME = "advertiser_name"; //price_period

    public static Map<String, Object> serialize(Ad data) {
        Map<String, Object> adData = new HashMap<>();
        adData.put(AdLayout.ADVERTISER_ID, data.getAdvertiserId());
        adData.put(AdLayout.CITY, data.getCity());
        adData.put(AdLayout.DESCRIPTION, data.getDescription());
        adData.put(AdLayout.VR_TOUR, data.hasVRTour());
        adData.put(AdLayout.PRICE, data.getPrice());
        adData.put(AdLayout.PRICE_PERIOD, data.getPricePeriod());
        adData.put(AdLayout.STREET, data.getStreet());
        adData.put(AdLayout.TITLE, data.getTitle());
        adData.put(ADVERTISER_NAME, data.getAdvertiserName());
        return adData;
    }

    /**
     * The ad returned by this function doesn't have a valid photoRefs list.
     * It is empty.
     *
     * @param serializedData
     * @return
     */
    public static Ad deserialize(Map<String, Object> serializedData) {
        return new Ad((String) serializedData.get(AdLayout.TITLE),
                (long) serializedData.get(AdLayout.PRICE),
                (PricePeriod) serializedData.get(AdLayout.PRICE_PERIOD),
                (String) serializedData.get(AdLayout.STREET),
                (String) serializedData.get(AdLayout.CITY),
                (String)serializedData.get(ADVERTISER_NAME),
                (String) serializedData.get(AdLayout.ADVERTISER_ID),
                (String) serializedData.get(AdLayout.DESCRIPTION),
                new ArrayList<>(),
                (boolean) serializedData.get(AdLayout.VR_TOUR));
    }
}
