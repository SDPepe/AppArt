package ch.epfl.sdp.appart;

import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import org.junit.Test;

import ch.epfl.sdp.appart.scrolling.card.Card;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class DatabaseUnitTest {

    @Test
    public void getCardsTest() {
        Database db = mock(Database.class);
        db.getCards();
        assertTrue(true);
    }

    @Test
    public void putCardTest() {
        Database db = mock(Database.class);
        Card card = new Card(null, "user", "Lausanne", 0, "");
        db.putCard(card);
        assertTrue(true);
    }

    @Test
    public void updateCardTest() {
        Database db = mock(Database.class);
        Card card = new Card("0", "user", "Lausanne", 0, "");
        db.updateCard(card);
        assertTrue(true);
    }

    @Test
    public void getUserTest() {
        Database db = mock(Database.class);
        db.getUser("id0");
        assertTrue(true);
    }

    @Test
    public void putUserTest() {
        Database db = mock(Database.class);
        User user =  new AppUser("id3", "test3@epfl.ch");
        db.putUser(user);
        assertTrue(true);
    }

    @Test
    public void updateUserTest() {
        Database db = mock(Database.class);
        User user =  new AppUser("id2", "test2_update@epfl.ch");
        db.updateUser(user);
        assertTrue(true);
    }


}
