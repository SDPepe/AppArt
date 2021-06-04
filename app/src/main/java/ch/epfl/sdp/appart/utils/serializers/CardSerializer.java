package ch.epfl.sdp.appart.utils.serializers;

import ch.epfl.sdp.appart.ad.PricePeriod;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.sdp.appart.database.firebaselayout.CardLayout;
import ch.epfl.sdp.appart.scrolling.card.Card;

public class CardSerializer {

    //To prevent construction
    private CardSerializer() {
    }

    public static Map<String, Object> serialize(Card data) {
        Map<String, Object> docData = new HashMap<>();
        docData.put(CardLayout.USER_ID, data.getUserId());
        docData.put(CardLayout.CITY, data.getCity());
        docData.put(CardLayout.PRICE, data.getPrice());
        docData.put(CardLayout.PERIOD, data.getPricePeriod().toString());
        docData.put(CardLayout.IMAGE, data.getImageUrl());
        docData.put(CardLayout.AD_ID, data.getAdId());
        docData.put(CardLayout.HAS_VTOUR, data.hasVRTour());
        return docData;
    }


    public static Card deserialize(String id,
                                   Map<String, Object> serializedData) {
        return new Card(id, (String) serializedData.get(CardLayout.AD_ID),
                (String) serializedData.get(CardLayout.USER_ID),
                (String) serializedData.get(CardLayout.CITY),
                (long) serializedData.get(CardLayout.PRICE),
                PricePeriod.fromString((String) serializedData.get(CardLayout.PERIOD)),
                (String) serializedData.get(CardLayout.IMAGE),
                (Boolean) serializedData.get(CardLayout.HAS_VTOUR));
    }
}
