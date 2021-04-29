package ch.epfl.sdp.appart.utils.serializers;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;

public class AdSerializer implements Serializer<Ad> {
    @Override
    public Map<String, Object> serialize(Ad data) {
        Map<String, Object> adData = new HashMap<>();
        adData.put(AdLayout.ADVERTISER_ID, data.getAdvertiserId());
        adData.put(AdLayout.CITY, data.getCity());
        adData.put(AdLayout.DESCRIPTION, data.getDescription());
        adData.put(AdLayout.VR_TOUR, data.hasVRTour());
        adData.put(AdLayout.PRICE, data.getPrice());
        adData.put(AdLayout.PRICE_PERIOD, data.getPricePeriod().ordinal());
        adData.put(AdLayout.STREET, data.getStreet());
        adData.put(AdLayout.TITLE, data.getTitle());
        return adData;
    }

    /**
     * This method has not been implemented, because the photo references of an ad are not serialized with the rest of the data.
     * Thus, it is not possible to simply "unbox" a map to an ad just like that.
     * @param id
     * @param serializedData
     * @return
     */
    @Override
    public Ad deserialize(String id, Map<String, Object> serializedData) {
        return null;
    }
}
