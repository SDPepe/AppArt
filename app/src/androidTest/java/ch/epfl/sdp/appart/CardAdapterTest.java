package ch.epfl.sdp.appart;

import android.app.Activity;
import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

<<<<<<< HEAD:app/src/androidTest/java/ch/epfl/sdp/appart/CardAdapterTest.java
import ch.epfl.sdp.appart.database.Database;
import ch.epfl.sdp.appart.database.MockDataBase;
=======
import ch.epfl.sdp.appart.database.DatabaseService;
>>>>>>> master:app/src/test/java/ch/epfl/sdp/appart/CardAdapterUnitTest.java
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
    public void constructorThrowsOnNullArgs1() {
        assertThrows(IllegalArgumentException.class, () -> {
<<<<<<< HEAD:app/src/androidTest/java/ch/epfl/sdp/appart/CardAdapterTest.java
            CardAdapter ac = new CardAdapter(context, db, null);
=======
            CardAdapter ac = new CardAdapter(mock(Activity.class), mock(DatabaseService.class), null);
>>>>>>> master:app/src/test/java/ch/epfl/sdp/appart/CardAdapterUnitTest.java
        });
    }

    @Test
    public void constructorThrowsOnNullArgs2() {
        assertThrows(IllegalArgumentException.class, () -> {
<<<<<<< HEAD:app/src/androidTest/java/ch/epfl/sdp/appart/CardAdapterTest.java
            CardAdapter ac = new CardAdapter(null, db, new ArrayList<>());
=======
            CardAdapter ac = new CardAdapter(null, mock(DatabaseService.class), new ArrayList<>());
>>>>>>> master:app/src/test/java/ch/epfl/sdp/appart/CardAdapterUnitTest.java
        });
    }
}
