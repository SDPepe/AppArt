package ch.epfl.sdp.appart.utils.serializers;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.database.firebaselayout.CardLayout;
import ch.epfl.sdp.appart.scrolling.card.Card;

public class CardSerializer implements Serializer<Card> {

    @Override
    public Map<String, Object> serialize(Card data) {
        Map<String, Object> docData = new HashMap<>();
        docData.put(CardLayout.USER_ID, data.getUserId());
        docData.put(CardLayout.CITY, data.getCity());
        docData.put(CardLayout.PRICE, data.getPrice());
        docData.put(CardLayout.IMAGE, data.getImageUrl());
        docData.put(CardLayout.AD_ID, data.getAdId());
        return docData;
    }

    @Override
    public Card deserialize(String id, Map<String, Object> serializedData) {
        return new Card(id, (String) serializedData.get(CardLayout.AD_ID), (String) serializedData.get(CardLayout.USER_ID),
            (String) serializedData.get(CardLayout.CITY),
            (long) serializedData.get(CardLayout.PRICE),
            (String) serializedData.get(CardLayout.IMAGE));
    }
}
