package ch.epfl.sdp.appart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.database.local.LocalDatabase;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LocalDatabaseTest {

    List<Card> cards = Arrays.asList(new Card("card1", "ad1", "user1",
                    "Lausanne", 1000, "card_1.jpeg"),
            new Card("card2", "ad2", "user2", "Lausanne", 1000, "card_2.jpeg"),
            new Card("card3", "ad3", "user3", "Lausanne", 1000, "card_3.jpeg"),
            new Card("card4", "ad4", "user4", "Lausanne", 1000, "card_4.jpeg"),
            new Card("card5", "ad5", "user5", "Lausanne", 1000, "card_5.jpeg"));

    List<User> users = Arrays.asList(new AppUser("user1", "test0@appart.ch"),
            new AppUser("user2", "test1@appart.ch"),
            new AppUser("user3", "test2@appart.ch"),
            new AppUser("user4", "test3@appart.ch"),
            new AppUser("user5", "test4@appart.ch"));

    List<String> picturesReferences = Arrays.asList(
            "fake_ad_1.jpg",
            "fake_ad_2.jpg",
            "fake_ad_3.jpg",
            "fake_ad_4.jpg",
            "fake_ad_5.jpg"
    );
    List<String> panoramaReferences = Arrays.asList(
            "panorama_1.jpg",
            "panorama_2.jpg",
            "panorama_3.jpg",
            "panorama_4.jpg",
            "panorama_5.jpg"
    );

    User currentUser = new AppUser("currentUser", "fakemail@testappart.ch");

    private Ad.AdBuilder[] getAdBuilders() {
        Ad.AdBuilder adBuilder1 = new Ad.AdBuilder()
                .withTitle("EPFL")
                .withPrice(100000)
                .withPricePeriod(PricePeriod.WEEK)
                .withStreet("Fake street").withCity("1015 Lausanne")
                .withAdvertiserName("Test advertiser")
                .withAdvertiserId("user1")
                .withDescription("Fake desc")
                .withPicturesReferences(picturesReferences)
                .withPanoramaReferences(panoramaReferences)
                .hasVRTour(false);
        Ad.AdBuilder adBuilder2 = new Ad.AdBuilder()
                .withTitle("EPFL")
                .withPrice(100000)
                .withPricePeriod(PricePeriod.MONTH)
                .withStreet("Fake street").withCity("1015 Lausanne")
                .withAdvertiserName("Test advertiser")
                .withAdvertiserId("user2")
                .withDescription("Fake desc")
                .withPicturesReferences(picturesReferences)
                .withPanoramaReferences(panoramaReferences)
                .hasVRTour(false);
        Ad.AdBuilder adBuilder3 = new Ad.AdBuilder()
                .withTitle("EPFL")
                .withPrice(100000)
                .withPricePeriod(PricePeriod.MONTH)
                .withStreet("Fake street").withCity("1015 Lausanne")
                .withAdvertiserName("Test advertiser")
                .withAdvertiserId("user3")
                .withDescription("Fake desc")
                .withPicturesReferences(picturesReferences)
                .withPanoramaReferences(panoramaReferences)
                .hasVRTour(false);
        Ad.AdBuilder adBuilder4 = new Ad.AdBuilder()
                .withTitle("EPFL")
                .withPrice(100000)
                .withPricePeriod(PricePeriod.MONTH)
                .withStreet("Fake street").withCity("1015 Lausanne")
                .withAdvertiserName("Test advertiser")
                .withAdvertiserId("user4")
                .withDescription("Fake desc")
                .withPicturesReferences(picturesReferences)
                .withPanoramaReferences(panoramaReferences)
                .hasVRTour(false);
        Ad.AdBuilder adBuilder5 = new Ad.AdBuilder()
                .withTitle("EPFL")
                .withPrice(100000)
                .withPricePeriod(PricePeriod.MONTH)
                .withStreet("Fake street").withCity("1015 Lausanne")
                .withAdvertiserName("Test advertiser")
                .withAdvertiserId("user5")
                .withDescription("Fake desc")
                .withPicturesReferences(picturesReferences)
                .withPanoramaReferences(panoramaReferences)
                .hasVRTour(false);

        Ad.AdBuilder[] adBuilders = {adBuilder1, adBuilder2, adBuilder3,
                adBuilder4, adBuilder5};
        return adBuilders;
    }

    private void checkRetrievedData(LocalDatabase localDatabase, Ad[] ads,
                                    List<Card> cards, int nbValidUsers) throws IOException, ClassNotFoundException {
        List<Card> cardsRetrieved = localDatabase.getCards("currentUser");
        assertEquals(cards, cardsRetrieved);

        for (int i = 0; i < users.size(); ++i) {
            AppUser userRetrieved = (AppUser) localDatabase.getUser("currentUser",
                    "user" + (i + 1));
            if (i >= nbValidUsers) {
                assertNull(userRetrieved);
            } else {
                assertEquals((AppUser) users.get(i), userRetrieved);
            }
        }

        for (int i = 0; i < ads.length; ++i) {
            Ad adRetrieved = localDatabase.getAd("ad" + (i + 1), "currentUser");
            assertThat(adRetrieved.equals(ads[i]), is(true));
        }
    }

    @Test
    public void addAdWithPictureSimpleTest() throws IOException,
            ClassNotFoundException {

        String appFolder =
                InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir().getPath();
        LocalDatabase localDatabase = new LocalDatabase(appFolder);

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser,
                adFolderPath -> CompletableFuture.completedFuture(null));
        Ad.AdBuilder[] adBuilders = getAdBuilders();

        Ad ad1 = adBuilders[0].build();

        Bitmap fakeBitmap = Bitmap.createBitmap(100, 100,
                Bitmap.Config.ARGB_8888);
        fakeBitmap.eraseColor(Color.rgb(255, 0, 0));


        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0),
                Arrays.asList(fakeBitmap), new ArrayList<>(),
                str -> CompletableFuture.completedFuture(null));

        File original = new File(appFolder + "/favorites/currentUser/card1" +
                "/Photo0.jpeg");
        assertThat(original.exists(), is(true));

        Bitmap retrievedBitmap =
                BitmapFactory.decodeFile(original.getAbsolutePath());

        File retrieved = new File(appFolder + "/favorites/test.jpeg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(retrieved);
            retrievedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytesOriginal = Files.readAllBytes(original.toPath());
        byte[] bytesRetrieved = Files.readAllBytes(retrieved.toPath());


        assertThat(Arrays.equals(bytesOriginal, bytesRetrieved), is(true));

        localDatabase.cleanFavorites();


    }

    @Test
    public void addAdWithPanoramaSimpleTest() throws IOException,
            ClassNotFoundException {
        String appFolder =
                InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir().getPath();
        LocalDatabase localDatabase = new LocalDatabase(appFolder);

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser,
                adFolderPath -> CompletableFuture.completedFuture(null));
        Ad.AdBuilder[] adBuilders = getAdBuilders();

        Ad ad1 = adBuilders[0].build();

        Bitmap fakeBitmap = Bitmap.createBitmap(100, 100,
                Bitmap.Config.ARGB_8888);
        fakeBitmap.eraseColor(Color.rgb(255, 0, 0));

        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0),
                new ArrayList<>(), Arrays.asList(fakeBitmap),
                str -> CompletableFuture.completedFuture(null));

        File original = new File(appFolder + "/favorites/currentUser/card1" +
                "/Panorama0.jpeg");
        assertThat(original.exists(), is(true));

        Bitmap retrievedBitmap =
                BitmapFactory.decodeFile(original.getAbsolutePath());

        File retrieved = new File(appFolder + "/favorites/test.jpeg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(retrieved);
            retrievedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytesOriginal = Files.readAllBytes(original.toPath());
        byte[] bytesRetrieved = Files.readAllBytes(retrieved.toPath());


        assertThat(Arrays.equals(bytesOriginal, bytesRetrieved), is(true));

        localDatabase.cleanFavorites();
    }


    @Test
    public void addAdWithPicturesAndPanoramas() throws IOException,
            ClassNotFoundException {
        String appFolder =
                InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir().getPath();
        LocalDatabase localDatabase = new LocalDatabase(appFolder);

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser,
                adFolderPath -> CompletableFuture.completedFuture(null));
        Ad.AdBuilder[] adBuilders = getAdBuilders();

        List<Bitmap> photos = new ArrayList<>();
        List<Bitmap> panoramas = new ArrayList<>();

        for (int i = 0; i < 5; ++i) {
            Bitmap fakeBitmap = Bitmap.createBitmap(100, 100,
                    Bitmap.Config.ARGB_8888);
            fakeBitmap.eraseColor(Color.rgb(getRandomColor(),
                    getRandomColor(), getRandomColor()));
            photos.add(fakeBitmap);
            panoramas.add(fakeBitmap);
        }

        Ad ad1 = adBuilders[0].build();
        Ad ad2 = adBuilders[1].build();
        Ad ad3 = adBuilders[2].build();
        Ad ad4 = adBuilders[3].build();
        Ad ad5 = adBuilders[4].build();

        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0), photos,
                panoramas, str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad2", "card2", ad2, users.get(1), photos,
                panoramas, str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad3", "card3", ad3, users.get(2), photos,
                panoramas, str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad4", "card4", ad4, users.get(3), photos,
                panoramas, str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad5", "card5", ad5, users.get(4), photos,
                panoramas, str -> CompletableFuture.completedFuture(null));

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; ++j) {

                String pathToPhoto = appFolder + "/favorites/currentUser/card"
                        + (i + 1) + "/Photo" + j + ".jpeg";
                String pathToPanorama = appFolder + "/favorites/currentUser" +
                        "/card" + (i + 1) + "/Panorama" + j + ".jpeg";

                File file = new File(pathToPhoto);
                assertTrue(file.exists());

                file = new File(pathToPanorama);
                assertTrue(file.exists());
            }
        }

        localDatabase.cleanFavorites();

    }

    @Test
    public void replaceAdWithDifferentNumbersOfPictures() throws IOException,
            ClassNotFoundException {
        String appFolder =
                InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir().getPath();
        LocalDatabase localDatabase = new LocalDatabase(appFolder);

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser,
                adFolderPath -> CompletableFuture.completedFuture(null));
        Ad.AdBuilder[] adBuilders = getAdBuilders();

        List<Bitmap> photos = new ArrayList<>();
        List<Bitmap> panoramas = new ArrayList<>();

        for (int i = 0; i < 5; ++i) {
            Bitmap fakeBitmap = Bitmap.createBitmap(100, 100,
                    Bitmap.Config.ARGB_8888);
            fakeBitmap.eraseColor(Color.rgb(getRandomColor(),
                    getRandomColor(), getRandomColor()));
            photos.add(fakeBitmap);
            panoramas.add(fakeBitmap);
        }

        Ad ad1 = adBuilders[0].build();
        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0), photos,
                panoramas, str -> CompletableFuture.completedFuture(null));

        List<String> panoramasPaths = localDatabase.getPanoramasPaths("ad1");
        assertThat(panoramasPaths.size(), is(panoramas.size()));

        for (int i = 0; i < 5; i++) {

            String pathToPhoto =
                    appFolder + "/favorites/currentUser/card1" +
                            "/Photo" + i + ".jpeg";
            /*String pathToPanorama =
                    appFolder + "/favorites/currentUser/card1" +
                            "/Panorama" + i + ".jpeg";*/
            String pathToPanorama = panoramasPaths.get(i);

            File file = new File(pathToPhoto);
            assertTrue(file.exists());

            file = new File(pathToPanorama);
            assertTrue(file.exists());
        }

        Ad.AdBuilder[] builders = getAdBuilders();

        builders[0].withPanoramaReferences(Arrays.asList("fake_panorama"))
                    .withPicturesReferences(Arrays.asList("fake_picture"));

        ad1 = builders[0].build();


        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0),
                Arrays.asList(photos.get(0)), Arrays.asList(panoramas.get(0))
                , str -> CompletableFuture.completedFuture(null));

        for (int i = 0; i < 5; i++) {

            String pathToPhoto =
                    appFolder + "/favorites/currentUser/card1" +
                            "/Photo" + i + ".jpeg";
            String pathToPanorama =
                    appFolder + "/favorites/currentUser/card1" +
                            "/Panorama" + i + ".jpeg";

            File file = new File(pathToPhoto);
            if(i > 0) {
                assertFalse(file.exists());
            } else {
                assertTrue(file.exists());
            }

            file = new File(pathToPanorama);
            if(i > 0) {
                assertFalse(file.exists());
            } else {
                assertTrue(file.exists());
            }
        }

        localDatabase.cleanFavorites();

    }

    public int getRandomColor() {
        return (int) (Math.random() * 255);
    }
}
