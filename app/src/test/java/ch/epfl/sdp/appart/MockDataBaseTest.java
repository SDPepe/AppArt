package ch.epfl.sdp.appart;


import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.appart.database.MockDataBase;
import ch.epfl.sdp.appart.scrolling.card.Card;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MockDataBaseTest {

    private MockDataBase dataBase;

    @Before
    public void init() {
        dataBase = new MockDataBase();
    }

    @Test
    public void getCardsNotEmpty() {
        try {
            List<Card> cards = dataBase.getCards().get();
            assertTrue(cards.size() > 0);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addCardAndUpdateToDatabase() {
        Card test = new Card("unknown2", "unknown2", "Lausanne2", 10000, "file:///android_asset/apart_fake_image_1.jpeg");
        try {
            assertFalse(dataBase.updateCard(test).get());
            assertTrue(dataBase.putCard(test).get().equals("unknown2"));
            assertTrue(dataBase.updateCard(test).get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
