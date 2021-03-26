package ch.epfl.sdp.appart;

import android.app.Activity;

import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.sdp.appart.database.Database;
import ch.epfl.sdp.appart.database.MockDataBase;
import ch.epfl.sdp.appart.scrolling.card.CardAdapter;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

public class CardAdapterTest {

    Database db = new MockDataBase();

    @Test
    public void constructorThrowsOnNullArgs1(){
        assertThrows(IllegalArgumentException.class, () -> {
            CardAdapter ac = new CardAdapter(mock(Activity.class), db, null);
        });
    }

    @Test
    public void constructorThrowsOnNullArgs2(){
        assertThrows(IllegalArgumentException.class, () -> {
            CardAdapter ac = new CardAdapter(null, db, new ArrayList<>());
        });
    }
}
