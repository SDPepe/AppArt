package ch.epfl.sdp.appart;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

public class MockDB implements Database {

    List<Card> cards;
    String[] locations = {"Lausanne", "Zurich", "Bern", "Bümplitz", "Genève"};
    int[] prices = {900, 1500, 1350, 1210, 1800};
    String[] imageRefs = {"MockDB/ap1.jpg", "MockDB/ap2.jpg", "MockDB/ap3.jpg", "MockDB/ap4.jpg",
            "MockDB/ap5.jpg"};
    User mockUser;

    public MockDB(User mockUser) {
        cards = new ArrayList<>();
        this.mockUser = mockUser;

        mockUser = new AppUser("0", "Mock", "john.doe@epfl.ch", "", "");
        for (int i = 0; i < locations.length; i++) {
            cards.add(new Card("", mockUser, locations[i], prices[i], imageRefs[i]));
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
