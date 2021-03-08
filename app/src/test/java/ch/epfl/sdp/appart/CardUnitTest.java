package ch.epfl.sdp.appart;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import ch.epfl.sdp.appart.user.User;

public class CardUnitTest {

    @Test
    public void gettersTest() {
        User user = mock(User.class);
        Card card = new Card(42, user, "Lausanne", "900", null);
        assertEquals("Lausanne", card.getCity());
        assertEquals("900", card.getPrice());
        //assertThat(card.getImage(), is());
    }

    @Test
    public void settersTest() {
        User user = mock(User.class);
        Card card = new Card(42, user, "Lausanne", "900", null);
        card.setCity("Morges");
        assertEquals("Morges", card.getCity());
        card.setPrice("850");
        assertEquals("850", card.getPrice());
        //card.setImage();
        //assertThat(card.getImage(),is());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsConstructorTest1() {
        Card c = new Card(0, null, "", "", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsConstructorTest2() {
        User user = mock(User.class);
        Card c = new Card(0,  user, null, "", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsConstructorTest3() {
        User user = mock(User.class);
        Card c = new Card(0, user, "", null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsSetCityTest() {
        User user = mock(User.class);
        Card card = new Card(42, user, "Lausanne", "900", null);
        card.setCity(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsSetPriceTest() {
        User user = mock(User.class);
        Card card = new Card(42, user, "Lausanne", "900", null);
        card.setPrice(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsSetImageTest() {
        User user = mock(User.class);
        Card card = new Card(42, user, "Lausanne", "900", null);
        card.setImage(null);
    }
}
