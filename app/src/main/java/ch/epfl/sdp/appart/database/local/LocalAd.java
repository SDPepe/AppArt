package ch.epfl.sdp.appart.database.local;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.serializers.AdSerializer;
import ch.epfl.sdp.appart.utils.serializers.CardSerializer;
import ch.epfl.sdp.appart.utils.serializers.UserSerializer;

public class LocalAd {

    private static final String ID = "ID";

    public static CompletableFuture<Void> writeCompleteAd(Card card, Ad ad,
                                                          User user,
                                                          String fullPath) {
        Map<String, Object> cardMap = CardSerializer.serialize(card);
        cardMap.put(ID, card.getId());

        Map<String, Object> adMap = AdSerializer.serialize(ad);


        Map<String, Object> userMap = UserSerializer.serialize(user);
        userMap.put(ID, user.getUserId());

        List<Map<String, Object>> mapList = new ArrayList<>();
        mapList.add(cardMap);
        mapList.add(adMap);
        mapList.add(userMap);

        CompletableFuture<Void> futureSuccess = new CompletableFuture<>();


        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fullPath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mapList);
            oos.close();

            futureSuccess.complete(null);
        } catch (IOException e) {
            e.printStackTrace();
            futureSuccess.completeExceptionally(e);
        }

        return futureSuccess;
    }

    public static CompletableFuture<LocalCompleteAd> loadCompleteAd(String fullPath) {
        CompletableFuture<LocalCompleteAd> futureCompleteAd =
                new CompletableFuture<>();

        FileInputStream fis;
        List<Map<String, Object>> mapList;
        try {
            fis = new FileInputStream(fullPath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            mapList = (List<Map<String, Object>>) ois.readObject();
            ois.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            futureCompleteAd.completeExceptionally(e);
            return futureCompleteAd;
        }

        Map<String, Object> cardMap = mapList.get(0);
        Map<String, Object> adMap = mapList.get(1);
        Map<String, Object> userMap = mapList.get(2);

        Card card = CardSerializer.deserialize((String) cardMap.get(ID),
                cardMap);


        User user = UserSerializer.deserialize((String) userMap.get(ID),
                userMap);

        LocalCompleteAd completeAd = new LocalCompleteAd(card, null, user);
        futureCompleteAd.complete(completeAd);

        return futureCompleteAd;
    }

    public static class LocalCompleteAd {
        public final Card card;
        public final Ad ad;
        public final User user;

        public LocalCompleteAd(Card card, Ad ad, User user) {
            this.card = card;
            this.ad = ad;
            this.user = user;
        }
    }
}
