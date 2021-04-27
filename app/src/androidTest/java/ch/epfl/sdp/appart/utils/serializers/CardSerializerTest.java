package ch.epfl.sdp.appart.utils.serializers;


import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.sdp.appart.database.firestorelayout.CardLayout;
import ch.epfl.sdp.appart.scrolling.card.Card;

public class CardSerializerTest {
    public CardSerializer serializer = new CardSerializer();

    @Test
    public void serializeTest() {
        Random random = new Random();
        for (int i = 0; i < 1000; ++i) {
            Card card = new Card(generateRandomString(10, random), generateRandomString(10, random),
                    generateRandomString(10, random), generateRandomString(10, random), random.nextInt(1000),
                    generateRandomString(10, random), random.nextBoolean());
            Map<String, Object> serializedCard = serializer.serialize(card);
            Assert.assertEquals(serializedCard.get(CardLayout.AD_ID), card.getAdId());
            Assert.assertEquals(serializedCard.get(CardLayout.CITY), card.getCity());
            Assert.assertEquals(serializedCard.get(CardLayout.IMAGE), card.getImageUrl());
            Assert.assertEquals(serializedCard.get(CardLayout.PRICE), card.getPrice());
            Assert.assertEquals(serializedCard.get(CardLayout.USER_ID), card.getUserId());
        }
    }

    @Test
    public void deserializeTest() {
        Random random = new Random();
        for (int i = 0; i < 1000; ++i) {
            Map<String, Object> serializedCard = new HashMap<>();
            serializedCard.put(CardLayout.AD_ID, generateRandomString(10, random));
            serializedCard.put(CardLayout.CITY, generateRandomString(10, random));
            serializedCard.put(CardLayout.IMAGE, generateRandomString(10, random));
            serializedCard.put(CardLayout.PRICE, (long) random.nextInt(1000));
            serializedCard.put(CardLayout.USER_ID, generateRandomString(10, random));

            Card card = serializer.deserialize("", serializedCard);

            Assert.assertEquals(serializedCard.get(CardLayout.AD_ID), card.getAdId());
            Assert.assertEquals(serializedCard.get(CardLayout.CITY), card.getCity());
            Assert.assertEquals(serializedCard.get(CardLayout.IMAGE), card.getImageUrl());
            Assert.assertEquals(serializedCard.get(CardLayout.PRICE), card.getPrice());
            Assert.assertEquals(serializedCard.get(CardLayout.USER_ID), card.getUserId());
        }
    }

    @Test
    public void serializeThenDeserializeTest() {
        Random random = new Random();
        for (int i = 0; i < 1000; ++i) {
            Card card = new Card(generateRandomString(10, random), generateRandomString(10, random),
                    generateRandomString(10, random), generateRandomString(10, random), random.nextInt(1000),
                    generateRandomString(10, random), random.nextBoolean());
            Map<String, Object> serializedCard = serializer.serialize(card);
            Card deserializedCard = serializer.deserialize("", serializedCard);
            Assert.assertEquals(deserializedCard.getAdId(), card.getAdId());
            Assert.assertEquals(deserializedCard.getCity(), card.getCity());
            Assert.assertEquals(deserializedCard.getImageUrl(), card.getImageUrl());
            Assert.assertEquals(deserializedCard.getPrice(), card.getPrice());
            Assert.assertEquals(deserializedCard.getUserId(), card.getUserId());
        }
    }

    private String generateRandomString(int length, Random rand) {
        byte[] bytes = new byte[length];
        rand.nextBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

}