package ch.epfl.sdp.appart;

import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.sdp.appart.user.User;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DatabaseUnitTest {

    @Test
    public void getCardsTest() {
        Database db = mock(Database.class);
        when(db.getCards()).thenReturn(new ArrayList<>());
        assertEquals(new ArrayList<>(), db.getCards());
    }

    @Test
    public void putCardTest() {
        Database db = mock(Database.class);
        User user = mock(User.class);
        Card card = new Card(null, user, "Lausanne", 0, "");
        when(db.putCard(card)).thenReturn(true);
        assertEquals(true, db.putCard(card));
    }
}
