package ch.epfl.sdp.appart;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.epfl.sdp.appart.user.FirebaseUserAdapter;

public class CardUnitTest {

    private Card card;

    @BeforeAll
    public void initCard(){
        card = new Card(42, new FirebaseUserAdapter(), "Fancy apartment",
                "Av. de la gare 1", "900", null, true, false);
    }

    @Test
    public void gettersTest(){
        assertThat(card.getTitle(), is("Fancy apartment"));
        assertThat(card.getAddress(), is("Av. de la gare 1"));
        assertThat(card.getPrice(), is("900"));
        //assertThat(card.getImage(), is());
        assertThat(card.hidePrice(), is(true));
        assertThat(card.hideAddress(), is(false));
    }

    @Test
    public void settersTest(){
        card.setTitle("Awesome apartment");
        assertThat(card.getTitle(), is("Awesome apartment"));
        card.setAddress("Av. de la gare 3");
        assertThat(card.getAddress(), is("Av. de la gare 3"));
        card.setPrice("850");
        assertThat(card.getPrice(), is("850"));
        //card.setImage();
        //assertThat(card.getImage(),is());
        card.setHidePrice(false);
        assertThat(card.hidePrice(), is(false));
        card.setHideAddress(true);
        assertThat(card.hideAddress(), is(false));
    }

    @Test
    public void nullArgumentsTest(){
        assertThrows(IllegalArgumentException.class, () -> new Card(0, null, "",
                "", "", null, true, true));
        assertThrows(IllegalArgumentException.class, () -> new Card(0, new FirebaseUserAdapter(),
                null, "", "", null, true, true));
        assertThrows(IllegalArgumentException.class, () -> new Card(0, new FirebaseUserAdapter(),
                "", null, "", null, true, true));
        assertThrows(IllegalArgumentException.class, () -> new Card(0, new FirebaseUserAdapter(),
                "", "", null, null, true, true));
        assertThrows(IllegalArgumentException.class, () -> card.setTitle(null));
        assertThrows(IllegalArgumentException.class, () -> card.setAddress(null));
        assertThrows(IllegalArgumentException.class, () -> card.setPrice(null));
        assertThrows(IllegalArgumentException.class, () -> card.setImage(null));
    }
}
