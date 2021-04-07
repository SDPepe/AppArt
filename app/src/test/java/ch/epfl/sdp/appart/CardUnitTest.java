package ch.epfl.sdp.appart;

import org.junit.Test;

import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class CardUnitTest {

    @Test
    public void gettersTest() {
        Card card = new Card(null, null, "user", "Lausanne", 900,
                "assets/img1.jpg", true);
        assertNull(card.getId());
        assertEquals("Lausanne", card.getCity());
        assertEquals(900, card.getPrice());
        assertEquals("assets/img1.jpg", card.getImageUrl());
        assertEquals("user", card.getUserId());
        assertEquals(true, card.hasVRTour());
    }

    @Test
    public void settersTest() {
        Card card = new Card(null, null, "user", "Lausanne", 900, "assets/img1.jpg");
        card.setCity("Morges");
        assertEquals("Morges", card.getCity());
        card.setPrice(850);
        assertEquals(850, card.getPrice());
        card.setImageUrl("assets/img2.jpg");
        assertEquals("assets/img2.jpg", card.getImageUrl());
        card.setVRTour(true);
        assertEquals(true, card.hasVRTour());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsConstructorTest1() {
        Card c = new Card(null, null, null, "",
                0, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsConstructorTest2() {
        Card c = new Card(null, null, "user", null, 0, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsConstructorTest3() {
        Card c = new Card(null, null, "user", "Lausanne", 0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsSetCityTest() {
        Card card = new Card(null, null, "user", "Lausanne", 900, "assets/img1.jpg");
        card.setCity(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsSetImageTest() {
        Card card = new Card(null, null, "user", "Lausanne", 900, "assets/img1.jpg");
        card.setImageUrl(null);
    }

    @Test
    public void equalsTest() {
        Card card = new Card(null, null, "user", "Lausanne", 900, "assets/img1.jpg");
        assertFalse(card.equals(null));
        Card card2 = new Card("1", "ad1", "user", "Lausanne", 900, "assets/img1.jpg");
        Card card3 = new Card("1", "ad1", "user", "Lausanne", 900, "assets/img1.jpg");
        Card card4 = new Card("2", "ad2", "user", "Lausanne", 900, "assets/img1.jpg");
        assertTrue(card2.equals(card3));
        assertFalse(card2.equals(card4));
        User user = mock(User.class);
        assertFalse(card.equals(user));
        Card card5 = null;
        assertFalse(card.equals(card5));
    }
}
