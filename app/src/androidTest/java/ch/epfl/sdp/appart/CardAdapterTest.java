package ch.epfl.sdp.appart;

import android.app.Activity;
import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.sdp.appart.database.Database;
import ch.epfl.sdp.appart.database.MockDataBase;
import ch.epfl.sdp.appart.scrolling.card.CardAdapter;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

public class CardAdapterTest {

    Database db = new MockDataBase();
    @ApplicationContext
    Activity context;

    @Test
    public void constructorThrowsOnNullArgs1(){
        assertThrows(IllegalArgumentException.class, () -> {
            CardAdapter ac = new CardAdapter(context, db, null);
        });
    }

    @Test
    public void constructorThrowsOnNullArgs2(){
        assertThrows(IllegalArgumentException.class, () -> {
            CardAdapter ac = new CardAdapter(null, db, new ArrayList<>());
        });
    }
}
