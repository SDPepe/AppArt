package ch.epfl.sdp.appart;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.database.local.MockLocalDatabase;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import kotlin.NotImplementedError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class MockLocalDatabaseTest {

    private AppUser vetterli;
    private MockLocalDatabase localdb;

    @Before
    public void init() {
        localdb = new MockLocalDatabase();
        vetterli = new AppUser("vetterli-id", "vetterli@epfl.ch");
        vetterli.setName("Martin Vetterli");
        vetterli.setAge(40);
        vetterli.setGender("MALE");
        vetterli.setPhoneNumber("0777777777");
    }

    @Test
    public void getCurrentUserReturnsNull() {
        assertNull(localdb.getCurrentUser());
    }

    @Test
    public void writeCompleteAdThrows() {
        assertThrows(NotImplementedError.class, () -> {
            localdb.writeCompleteAd(null, null, null, null, null, null, null);
        });
    }

    @Test
    public void getCardsThrows() {
        assertThrows(NotImplementedError.class, () -> {
            localdb.getCards();
        });
    }

    @Test
    public void getAdWorks() throws ExecutionException, InterruptedException {
        List<String> picturesReferences = Arrays.asList(
                "fake_ad_1.jpg",
                "fake_ad_2.jpg",
                "fake_ad_3.jpg",
                "fake_ad_4.jpg",
                "fake_ad_5.jpg"
        );
        Ad ad = new Ad.AdBuilder()
                .withTitle("EPFL")
                .withPrice(100000)
                .withPricePeriod(PricePeriod.MONTH)
                .withStreet("Station 18").withCity("1015 Lausanne")
                .withAdvertiserName("Martin Vetterli")
                .withAdvertiserId("vetterli-id")
                .withDescription("Ever wanted the EPFL campus all for yourself?")
                .withPicturesReferences(picturesReferences)
                .withPanoramaReferences(picturesReferences) //put the pictures since its mocked
                .hasVRTour(false)
                .build();
        assertEquals(ad, localdb.getAd("arandomidbecausewhocares").get());
    }

    @Test
    public void getUserWorksForGoodValue() throws ExecutionException, InterruptedException {
        assertEquals(vetterli.getUserId(),
                localdb.getUser("vetterli-id").get().getUserId());
    }

    @Test
    public void getUserWorksForBadValue() {
        assertTrue(localdb.getUser("").isCompletedExceptionally());
    }

    @Test
    public void cleanFavoritesThrows() {
        assertThrows(NotImplementedError.class, () -> {
            localdb.cleanFavorites();
        });
    }

    @Test
    public void removeCardThrows() {
        assertThrows(NotImplementedError.class, () -> {
            localdb.removeCard("");
        });
    }

    @Test
    public void setCurrentUserThrows() {
        assertThrows(NotImplementedError.class, () -> {
            localdb.setCurrentUser(null, null);
        });
    }

    @Test
    public void loadCurrentThrows() {
        assertThrows(NotImplementedError.class, () -> {
            localdb.loadCurrentUser();
        });
    }

    @Test
    public void getPanoramasPathsThrows() {
        assertThrows(NotImplementedError.class, () -> {
            localdb.getPanoramasPaths(null);
        });
    }
}

