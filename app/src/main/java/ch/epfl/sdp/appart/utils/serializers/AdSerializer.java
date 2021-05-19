package ch.epfl.sdp.appart.utils.serializers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;

public class AdSerializer {

    //To prevent construction
    private AdSerializer() {
    }

    public final static String ADVERTISER_NAME = "advertiser_name";
    //price_period

    public static Map<String, Object> serialize(Ad data) {
        Map<String, Object> adData = new HashMap<>();
        adData.put(AdLayout.ADVERTISER_ID, data.getAdvertiserId());
        adData.put(AdLayout.CITY, data.getCity());
        adData.put(AdLayout.DESCRIPTION, data.getDescription());
        adData.put(AdLayout.VR_TOUR, data.hasVRTour());
        adData.put(AdLayout.PRICE, data.getPrice());
        adData.put(AdLayout.PRICE_PERIOD, data.getPricePeriod().ordinal());
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
                PricePeriod.ALL.get((int) serializedData.get(AdLayout.PRICE_PERIOD)),
                (String) serializedData.get(AdLayout.STREET),
                (String) serializedData.get(AdLayout.CITY),
                (String) serializedData.get(ADVERTISER_NAME),
                (String) serializedData.get(AdLayout.ADVERTISER_ID),
                (String) serializedData.get(AdLayout.DESCRIPTION),
                new ArrayList<>(),
                new ArrayList<>(),
                (boolean) serializedData.get(AdLayout.VR_TOUR));
    }

    public static Map<String, Object> serializeLocal(Ad data, String adID, String cardID, String userID) {
        Map<String, Object> adData = new HashMap<>();
        adData.put(AdLayout.ADVERTISER_ID, data.getAdvertiserId());
        adData.put(AdLayout.CITY, data.getCity());
        adData.put(AdLayout.DESCRIPTION, data.getDescription());
        adData.put(AdLayout.VR_TOUR, data.hasVRTour());
        adData.put(AdLayout.PRICE, data.getPrice());
        adData.put(AdLayout.PRICE_PERIOD, data.getPricePeriod().ordinal());
        adData.put(AdLayout.STREET, data.getStreet());
        adData.put(AdLayout.TITLE, data.getTitle());
        adData.put(ADVERTISER_NAME, data.getAdvertiserName());
        adData.put(AdLayout.ID, adID);
        adData.put(AdLayout.PHOTO_REFS, data.getPhotosRefs());
        adData.put(AdLayout.CARD_ID, cardID);
        adData.put(AdLayout.USER_ID, userID);
        adData.put(AdLayout.PANORAMA_REFS, data.getPanoramaReferences());


        return adData;
    }


    public static Ad deserializeLocal(Map<String, Object> serializedData) {
        return new Ad((String) serializedData.get(AdLayout.TITLE),
                (long) serializedData.get(AdLayout.PRICE),
                PricePeriod.ALL.get((int) serializedData.get(AdLayout.PRICE_PERIOD)),
                (String) serializedData.get(AdLayout.STREET),
                (String) serializedData.get(AdLayout.CITY),
                (String) serializedData.get(ADVERTISER_NAME),
                (String) serializedData.get(AdLayout.ADVERTISER_ID),
                (String) serializedData.get(AdLayout.DESCRIPTION),
                (List<String>) serializedData.get(AdLayout.PHOTO_REFS),
                (List<java.lang.String>) serializedData.get(AdLayout.PANORAMA_REFS),
                (boolean) serializedData.get(AdLayout.VR_TOUR));
    }
}
