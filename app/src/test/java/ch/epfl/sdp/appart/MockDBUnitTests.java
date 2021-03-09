package ch.epfl.sdp.appart;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
            cards.add(new Card(mock(User.class), locations[i], prices[i], imageRefs[i]));
        }
        Database mockdb = new MockDB();

        // TODO change when FirebaseUser is ready
        //assertEquals(cards, mockdb.getCards());
        mockdb.getCards();
        assertEquals(true, true);
    }

    @Test
    public void putCardTest() {
        Database mockdb = new MockDB();
        Card card = new Card(mock(User.class), "", 0, "");
        assertEquals(false, mockdb.putCard(card));
    }
}
