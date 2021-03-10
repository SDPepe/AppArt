package ch.epfl.sdp.appart;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MockDBUnitTests {

    @Test
    public void getCardsTest() {
        List<Card> cards = new ArrayList<>();
        String[] locations = {"Lausanne", "Zurich", "Bern", "Bümplitz", "Genève"};
        int[] prices = {900, 1500, 1350, 1210, 1800};
        String[] imageRefs = {"MockDB/ap1.jpg", "MockDB/ap2.jpg", "MockDB/ap3.jpg", "MockDB/ap4.jpg",
                "MockDB/ap5.jpg"};
        for (int i = 0; i < locations.length; i++) {
            cards.add(new Card("", mock(User.class), locations[i], prices[i], imageRefs[i]));
        }
        User user = new AppUser("0", "Mock", "john.doe@epfl.ch", "");
        Database mockdb = new MockDB(user);
        assertEquals(cards, mockdb.getCards());
    }

    @Test
    public void putCardTest() {
        User user = new AppUser("0", "Mock", "john.doe@epfl.ch", "");
        Database mockdb = new MockDB(user);
        Card card = new Card(null, mock(User.class), "", 0, "");
        assertEquals(false, mockdb.putCard(card));
    }
}
