package ch.epfl.sdp.appart;

import android.net.Uri;
import java.net.URI;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.ContactInfo;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import kotlin.NotImplementedError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class MockDatabaseTest {

    private MockDatabaseService dataBase;

    @Before
    public void init() {
        dataBase = new MockDatabaseService();
    }

    @Test
    public void getCardsNotEmpty() {
        try {
            List<Card> cards = dataBase.getCards().get();
            assertTrue(cards.size() > 0);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCardsFilterNotEmpty() {
        try {
            List<Card> cards = dataBase.getCardsFilter("1000").get();
            assertTrue(cards.size() > 0);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void getCardsFilterPriceNotEmpty() {
        try {
            List<Card> cards = dataBase.getCardsFilterPrice(0, 1000).get();
            assertTrue(cards.size() > 0);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void getCardsByIdNotEmpty() {
        try {
            List<Card> cards = dataBase.getCardsById(Arrays.asList("1111", "2222")).get();
            assertTrue(cards.size() > 0);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateCorrectCard() {
        Card test = new Card("1111", "adId", "unknown", "Lausanne", 1000,PricePeriod.MONTH, "file:///android_asset/apart_fake_image_1.jpeg");
        try {
            assertTrue(dataBase.updateCard(test).get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateWrongCard() {
        Card test = new Card("unknown6", "adId", "unknown", "Lausanne", 1000,PricePeriod.MONTH, "file:///android_asset/apart_fake_image_1.jpeg");
        try {
            assertFalse(dataBase.updateCard(test).get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void userOperationsWork() {
        User user = new AppUser("1234", "test.appart@epfl.ch");
        assertTrue(dataBase.putUser(user).join());
        assertEquals(user, dataBase.getUser("1234").join());
        assertTrue(dataBase.updateUser(user).join());
        assertEquals(user, dataBase.getUser("1234").join());
        User user3 = new AppUser("4321", "test.appart@epfl.ch");
        assertFalse(dataBase.updateUser(user3).join());
    }

    @Test
    public void putAdWorksWithGoodValue() throws ExecutionException, InterruptedException {
        Ad ad = new Ad("title", 1000, PricePeriod.DAY, "", "", "", "",
                "", new ArrayList<>(), new ArrayList<>(), false);
        assertEquals("1234", dataBase.putAd(ad, new ArrayList<>(), new ArrayList<>()).get());

    }

    @Test
    public void putAdWorksThrowsOnBadValue() throws ExecutionException, InterruptedException {

        Ad ad = new Ad("failing", 1000, PricePeriod.DAY, "", "", "","",
                "", new ArrayList<>(), new ArrayList<>(), false);
        assertThrows(ExecutionException.class, () -> dataBase.putAd(ad, new ArrayList<>(), new ArrayList<>()).get());
    }

    @Test
    public void putImageThrowsOnNullPathAndName() throws ExecutionException, InterruptedException {
        Uri uri = mock(Uri.class);
        assertThrows(ExecutionException.class, () -> dataBase.putImage(uri, null).get());
    }

    @Test
    public void putImageIsSuccessful(){
        Uri uri = mock(Uri.class);
        String imagePathAndName = "users/test/path/photo.jpeg";
        assertTrue(dataBase.putImage(uri, imagePathAndName).getNow(null));
    }

    @Test
    public void deleteImageIsSuccessful(){
        String imagePathAndName = "users/test/path/photo.jpeg";
        assertTrue(dataBase.deleteImage(imagePathAndName).getNow(null));
    }

    @Test
    public void deleteImageThrowsOnNullPathAndName() throws ExecutionException, InterruptedException {
        Uri uri = mock(Uri.class);
        assertThrows(ExecutionException.class, () -> dataBase.deleteImage(null).get());
    }

    @Test
    public void deleteAdIsSuccessful() {
        dataBase.getCards().thenAccept(ls -> {
            int initSize = ls.size();
            assertTrue(dataBase.deleteAd("1111", "1111").getNow(false));
            dataBase.getCards().thenAccept(newLs -> assertEquals(newLs.size(), initSize >= 1 ? initSize - 1 : 0)); });
    }

}
