package ch.epfl.sdp.appart;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.sdp.appart.user.User;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DatabaseUnitTest {

    @Test
    public void getCardsTest() {
        Database db = mock(Database.class);
        db.getCards(task -> {
        });
        assertTrue(true);
    }

    @Test
    public void putCardTest() {
        Database db = mock(Database.class);
        User user = mock(User.class);
        Card card = new Card(null, user, "Lausanne", 0, "");
        db.putCard(card, task -> {
        });
        assertTrue(true);
    }

    @Test
    public void updateCardTest() {
        Database db = mock(Database.class);
        User user = mock(User.class);
        Card card = new Card("0", user, "Lausanne", 0, "");
        db.updateCard(card, task -> {
        });
        assertTrue(true);
    }
}
