package ch.epfl.sdp.appart;

import android.app.Activity;

import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.scrolling.card.CardAdapter;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

public class CardAdapterUnitTest {

    @Test
    public void constructorThrowsOnNullArgs1(){
        assertThrows(IllegalArgumentException.class, () -> {
            CardAdapter ac = new CardAdapter(mock(Activity.class), mock(DatabaseService.class), null);
        });
    }

    @Test
    public void constructorThrowsOnNullArgs2(){
        assertThrows(IllegalArgumentException.class, () -> {
            CardAdapter ac = new CardAdapter(null, mock(DatabaseService.class), new ArrayList<>());
        });
    }
}
