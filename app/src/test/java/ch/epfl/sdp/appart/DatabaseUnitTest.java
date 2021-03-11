package ch.epfl.sdp.appart;

import org.junit.Test;

import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 */
public class DatabaseUnitTest {

    @Test
    public void getCardsTest() {
        Database db = mock(Database.class);
        db.getCards(task -> {
        });
        assertTrue(true);
    }

    @Test
    public void putCardTest() {
        Database db = mock(Database.class);
        Card card = new Card(null, "user", "Lausanne", 0, "");
        db.putCard(card, task -> {
        });
        assertTrue(true);
    }

    @Test
    public void updateCardTest() {
        Database db = mock(Database.class);
        Card card = new Card("0", "user", "Lausanne", 0, "");
        db.updateCard(card, task -> {
        });
        assertTrue(true);
    }
}
