package ch.epfl.sdp.appart;

import java.util.ArrayList;
import java.util.List;

public class MockDB implements Database {

    List<Card> cards;
    String[] locations = {"Lausanne", "Zurich", "Bern", "Bümplitz", "Genève"};
    int[] prices = {900, 1500, 1350, 1210, 1800};
    String[] imageRefs = {"MockDB/ap1.jpg", "MockDB/ap2.jpg", "MockDB/ap3.jpg", "MockDB/ap4.jpg",
            "MockDB/ap5.jpg"};

    public MockDB() {
        cards = new ArrayList<>();

        // TODO change when FirebaseUser is ready
        //User user = new FirebaseUser();
        for (int i = 0; i < locations.length; i++) {
            //cards.add(new Card(i, user, locations[i], prices[i], imageRefs[i]));
        }
    }

    @Override
    public List<Card> getCards() {
        return cards;
    }

    @Override
    public boolean putCard(Card card) {
        return false;
    }
}
