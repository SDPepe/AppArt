package ch.epfl.sdp.appart;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import ch.epfl.sdp.appart.user.User;

public class CardUnitTest {

    @Test
    public void gettersTest() {
        User user = mock(User.class);
        Card card = new Card(null, user, "Lausanne", 900, "assets/img1.jpg");
        assertEquals("Lausanne", card.getCity());
        assertEquals(900, card.getPrice());
        assertEquals("assets/img1.jpg", card.getImageUrl());
        assertNull(card.getId());
    }

    @Test
    public void settersTest() {
        User user = mock(User.class);
        Card card = new Card(null, user, "Lausanne", 900, "assets/img1.jpg");
        card.setCity("Morges");
        assertEquals("Morges", card.getCity());
        card.setPrice(850);
        assertEquals(850, card.getPrice());
        card.setImageUrl("assets/img2.jpg");
        assertEquals("assets/img2.jpg", card.getImageUrl());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsConstructorTest1() {
        Card c = new Card(null, null, "",
                0, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsConstructorTest2() {
        User user = mock(User.class);
        Card c = new Card(null, user, null, 0, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsSetCityTest() {
        User user = mock(User.class);
        Card card = new Card(null, user, "Lausanne", 900, "assets/img1.jpg");
        card.setCity(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsSetImageTest() {
        User user = mock(User.class);
        Card card = new Card(null, user, "Lausanne", 900, "assets/img1.jpg");
        card.setImageUrl(null);
    }

    @Test
    public void equalsTest() {
        User user = mock(User.class);
        Card card = new Card(null, user, "Lausanne", 900, "assets/img1.jpg");
        assertEquals(false, card.equals(null));
        Card card2 = new Card("1", user, "Lausanne", 900, "assets/img1.jpg");
        Card card3 = new Card("1", user, "Lausanne", 900, "assets/img1.jpg");
        Card card4 = new Card("2", user, "Lausanne", 900, "assets/img1.jpg");
        assertTrue(card2.equals(card3));
        assertFalse(card2.equals(card4));
    }
}
