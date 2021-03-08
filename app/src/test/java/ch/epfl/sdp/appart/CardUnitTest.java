package ch.epfl.sdp.appart;


import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import ch.epfl.sdp.appart.user.FirebaseUserAdapter;

public class CardUnitTest {

    @Test
    public void gettersTest() {
        Card card = new Card(42, new FirebaseUserAdapter(),
                "Lausanne", "900", null);
        assertEquals("Lausanne", card.getCity());
        assertEquals("900", card.getPrice());
        //assertThat(card.getImage(), is());
    }

    @Test
    public void settersTest() {
        Card card = new Card(42, new FirebaseUserAdapter(),
                "Lausanne", "900", null);
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
        Card c = new Card(0, new FirebaseUserAdapter(), null, "", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsConstructorTest3() {
        Card c = new Card(0, new FirebaseUserAdapter(), "", null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsSetCityTest() {
        Card card = new Card(42, new FirebaseUserAdapter(),
                "Lausanne", "900", null);
        card.setCity(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsSetPriceTest() {
        Card card = new Card(42, new FirebaseUserAdapter(),
                "Lausanne", "900", null);
        card.setPrice(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentsSetImageTest() {
        Card card = new Card(42, new FirebaseUserAdapter(),
                "Lausanne", "900", null);
        card.setImage(null);
    }
}
