package ch.epfl.sdp.appart;


import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MockDatabaseTest {

    private MockDatabaseService dataBase;

    @Before
    public void init() {
        dataBase = new MockDatabaseService();
    }

    @Test
    public void getCardsNotEmpty() {
        try {
            List<Card> cards = dataBase.getCards().get();
            assertTrue(cards.size() > 0);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addCardAndUpdateToDatabase() {
        Card test = new Card("unknown2", "unknown2", "unknown2", "Lausanne2", 10000, "file:///android_asset/apart_fake_image_1.jpeg");
        try {
            assertFalse(dataBase.updateCard(test).get());
            assertEquals("unknown2", dataBase.putCard(test).get());
            assertTrue(dataBase.updateCard(test).get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void userOperationsWork() {
        User user = new AppUser("1234", "test.appart@epfl.ch");
        assertTrue(dataBase.putUser(user).join());
        assertEquals(user, dataBase.getUser("1234").join());
        assertTrue(dataBase.updateUser(user).join());
        assertEquals(user, dataBase.getUser("1234").join());
        User user3 = new AppUser("4321", "test.appart@epfl.ch");
        assertFalse(dataBase.updateUser(user3).join());
    }

}
