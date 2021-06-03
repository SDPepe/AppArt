package ch.epfl.sdp.appart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.database.local.LocalDatabase;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.hilt.LocalDatabaseModule;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@UninstallModules(LocalDatabaseModule.class)
@HiltAndroidTest
public class LocalDatabaseAndroidTest {
    final List<User> users = Arrays.asList(new AppUser("user1", "test0@appart" +
                    ".ch"),
            new AppUser("user2", "test1@appart.ch"),
            new AppUser("user3", "test2@appart.ch"),
            new AppUser("user4", "test3@appart.ch"),
            new AppUser("user5", "test4@appart.ch"));

    final List<String> picturesReferences = Arrays.asList(
            "fake_ad_1.jpg",
            "fake_ad_2.jpg",
            "fake_ad_3.jpg",
            "fake_ad_4.jpg",
            "fake_ad_5.jpg"
    );
    final List<String> panoramaReferences = Arrays.asList(
            "panorama_1.jpg",
            "panorama_2.jpg",
            "panorama_3.jpg",
            "panorama_4.jpg",
            "panorama_5.jpg"
    );

    final User currentUser = new AppUser("currentUser", "fakemail@testappart" +
            ".ch");

    final List<Card> cards = Arrays.asList(new Card("card1", "ad1", "user1",
                    "Lausanne", 1000, PricePeriod.MONTH, "card_1.jpeg"),
            new Card("card2", "ad2", "user2", "Lausanne", 1000,PricePeriod.MONTH, "card_2.jpeg"),
            new Card("card3", "ad3", "user3", "Lausanne", 1000, PricePeriod.MONTH, "card_3.jpeg"),
            new Card("card4", "ad4", "user4", "Lausanne", 1000, PricePeriod.MONTH, "card_4.jpeg"),
            new Card("card5", "ad5", "user5", "Lausanne", 1000, PricePeriod.MONTH, "card_5.jpeg"));

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

        return (Ad.AdBuilder[]) new Ad.AdBuilder[]{adBuilder1, adBuilder2,
                adBuilder3,
                adBuilder4, adBuilder5};
    }

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @BindValue
    LocalDatabaseService localDatabase =
            new LocalDatabase(InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir().getPath());

    @Test
    public void addAdWithPictureSimpleTest() throws IOException {

        String appFolder =
                InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir().getPath();

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser, null).join();
        Ad.AdBuilder[] adBuilders = getAdBuilders();

        Ad ad1 = adBuilders[0].build();

        Bitmap fakeBitmap = Bitmap.createBitmap(100, 100,
                Bitmap.Config.ARGB_8888);
        fakeBitmap.eraseColor(Color.rgb(255, 0, 0));


        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0),
                Collections.singletonList(fakeBitmap), new ArrayList<>(),
                null).join();

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
    public void addAdWithPanoramaSimpleTest() throws IOException {
        String appFolder =
                InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir().getPath();

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser, null).join();
        Ad.AdBuilder[] adBuilders = getAdBuilders();

        Ad ad1 = adBuilders[0].build();

        Bitmap fakeBitmap = Bitmap.createBitmap(100, 100,
                Bitmap.Config.ARGB_8888);
        fakeBitmap.eraseColor(Color.rgb(255, 0, 0));

        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0),
                new ArrayList<>(), Collections.singletonList(fakeBitmap),
                null).join();

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
    public void addAdWithPicturesAndPanoramas() {
        String appFolder =
                InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir().getPath();

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser,
                null).join();
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
                panoramas, null).join();
        localDatabase.writeCompleteAd("ad2", "card2", ad2, users.get(1), photos,
                panoramas, null).join();
        localDatabase.writeCompleteAd("ad3", "card3", ad3, users.get(2), photos,
                panoramas, null).join();
        localDatabase.writeCompleteAd("ad4", "card4", ad4, users.get(3), photos,
                panoramas, null).join();
        localDatabase.writeCompleteAd("ad5", "card5", ad5, users.get(4), photos,
                panoramas, null).join();

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
    public void replaceAdWithDifferentNumbersOfPictures() {
        String appFolder =
                InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir().getPath();

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser, null).join();
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
                panoramas, null).join();

        List<String> panoramasPaths =
                localDatabase.getPanoramasPaths("ad1").join();
        assertThat(panoramasPaths.size(), is(panoramas.size()));

        for (int i = 0; i < 5; i++) {

            String pathToPhoto =
                    appFolder + "/favorites/currentUser/card1" +
                            "/Photo" + i + ".jpeg";
            String pathToPanorama = panoramasPaths.get(i);

            File file = new File(pathToPhoto);
            assertTrue(file.exists());

            file = new File(pathToPanorama);
            assertTrue(file.exists());
        }

        Ad.AdBuilder[] builders = getAdBuilders();

        builders[0].withPanoramaReferences(Collections.singletonList(
                "fake_panorama"))
                .withPicturesReferences(Collections.singletonList(
                        "fake_picture"));

        ad1 = builders[0].build();


        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0),
                Collections.singletonList(photos.get(0)),
                Collections.singletonList(panoramas.get(0))
                , null).join();

        for (int i = 0; i < 5; i++) {

            String pathToPhoto =
                    appFolder + "/favorites/currentUser/card1" +
                            "/Photo" + i + ".jpeg";
            String pathToPanorama =
                    appFolder + "/favorites/currentUser/card1" +
                            "/Panorama" + i + ".jpeg";

            File file = new File(pathToPhoto);
            if (i > 0) {
                assertFalse(file.exists());
            } else {
                assertTrue(file.exists());
            }

            file = new File(pathToPanorama);
            if (i > 0) {
                assertFalse(file.exists());
            } else {
                assertTrue(file.exists());
            }
        }

        localDatabase.cleanFavorites();

    }

    @Test
    public void profilePictureTest() throws IOException {
        String appFolder =
                InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir().getPath();

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser,
                null).join();
        Ad.AdBuilder[] adBuilders = getAdBuilders();

        Ad ad1 = adBuilders[0].build();
        Bitmap fakeBitmap = Bitmap.createBitmap(100, 100,
                Bitmap.Config.ARGB_8888);
        fakeBitmap.eraseColor(Color.rgb(getRandomColor(),
                getRandomColor(), getRandomColor()));

        AppUser userWithProfilePic = new AppUser("user1", "fakeemail" +
                "@testappart.ch");
        userWithProfilePic.setProfileImagePathAndName("test.jpeg");

        localDatabase.writeCompleteAd("ad1", "card1", ad1, userWithProfilePic
                , new ArrayList<>(), new ArrayList<>(), fakeBitmap).join();

        File originalFile = new File(appFolder + "/favorites/profile_pic.jpeg");
        FileOutputStream fosOut;
        try {
            fosOut = new FileOutputStream(originalFile);
            fakeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fosOut);
            fosOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File retrievedBitmapFile = new File(appFolder + "/favorites" +
                "/currentUser/users/user1/profileImage.jpeg");

        byte[] bytesOriginal = Files.readAllBytes(originalFile.toPath());
        byte[] bytesRetrieved =
                Files.readAllBytes(retrievedBitmapFile.toPath());

        assertArrayEquals(bytesOriginal, bytesRetrieved);

        localDatabase.cleanFavorites();

    }

    @Test
    public void setCurrentUserTest() {

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser, null).join();

        AppUser currentUserRetrieved =
                (AppUser) localDatabase.loadCurrentUser();

        assertEquals(currentUserRetrieved, (AppUser) currentUser);

        localDatabase.cleanFavorites();
    }

    @Test
    public void addAdsWithoutPictureTest() {

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser, null);
        User retrievedCurrentUser = localDatabase.getCurrentUser();
        assertNotNull(retrievedCurrentUser);
        assertEquals(retrievedCurrentUser, currentUser);

        Ad.AdBuilder[] adBuilders = getAdBuilders();

        Ad ad1 = adBuilders[0].build();
        Ad ad2 = adBuilders[1].build();
        Ad ad3 = adBuilders[2].build();
        Ad ad4 = adBuilders[3].build();
        Ad ad5 = adBuilders[4].build();

        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0),
                new ArrayList<>(), new ArrayList<>(), null).join();
        localDatabase.writeCompleteAd("ad2", "card2", ad2, users.get(1),
                new ArrayList<>(), new ArrayList<>(), null).join();
        localDatabase.writeCompleteAd("ad3", "card3", ad3, users.get(2),
                new ArrayList<>(), new ArrayList<>(), null).join();
        localDatabase.writeCompleteAd("ad4", "card4", ad4, users.get(3),
                new ArrayList<>(), new ArrayList<>(), null).join();
        localDatabase.writeCompleteAd("ad5", "card5", ad5, users.get(4),
                new ArrayList<>(), new ArrayList<>(), null).join();

        Ad[] ads = {ad1, ad2, ad3, ad4, ad5};

        checkRetrievedData(localDatabase, ads, cards, 5);

        localDatabase.cleanFavorites();
    }

    private void checkRetrievedData(LocalDatabaseService localDatabase, Ad[] ads,
                                    List<Card> cards, int nbValidUsers) {
        List<Card> cardsRetrieved = localDatabase.getCards().join();
        assertEquals(cards, cardsRetrieved);

        for (int i = 0; i < users.size(); ++i) {
            AppUser userRetrieved =
                    (AppUser) localDatabase.getUser("user" + (i + 1)).join();
            if (i >= nbValidUsers) {
                assertNull(userRetrieved);
            } else {
                assertEquals((AppUser) users.get(i), userRetrieved);
            }
        }

        for (int i = 0; i < ads.length; ++i) {
            Ad adRetrieved = localDatabase.getAd("ad" + (i + 1)).join();
            assertThat(adRetrieved.equals(ads[i]), is(true));
        }
    }

    @Test
    public void addAdsWithOneUserOnly() {

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser, null).join();
        Ad.AdBuilder[] adBuilders = getAdBuilders();

        for (Ad.AdBuilder adBuilder : adBuilders) {
            adBuilder.withAdvertiserId("user1");
        }

        Ad ad1 = adBuilders[0].build();
        Ad ad2 = adBuilders[1].build();
        Ad ad3 = adBuilders[2].build();
        Ad ad4 = adBuilders[3].build();
        Ad ad5 = adBuilders[4].build();

        Ad[] ads = {ad1, ad2, ad3, ad4, ad5};

        List<Card> cards = Arrays.asList(new Card("card1", "ad1", "user1",
                        "Lausanne", 1000, PricePeriod.MONTH, "card_1.jpeg"),
                new Card("card2", "ad2", "user1", "Lausanne", 1000, PricePeriod.MONTH, "card_2" +
                        ".jpeg"),
                new Card("card3", "ad3", "user1", "Lausanne", 1000, PricePeriod.MONTH, "card_3" +
                        ".jpeg"),
                new Card("card4", "ad4", "user1", "Lausanne", 1000, PricePeriod.MONTH, "card_4" +
                        ".jpeg"),
                new Card("card5", "ad5", "user1", "Lausanne", 1000, PricePeriod.MONTH, "card_5" +
                        ".jpeg"));

        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0),
                new ArrayList<>(), new ArrayList<>(), null).join();
        localDatabase.writeCompleteAd("ad2", "card2", ad2, users.get(0),
                new ArrayList<>(), new ArrayList<>(), null).join();
        localDatabase.writeCompleteAd("ad3", "card3", ad3, users.get(0),
                new ArrayList<>(), new ArrayList<>(),
                null).join();
        localDatabase.writeCompleteAd("ad4", "card4", ad4, users.get(0),
                new ArrayList<>(), new ArrayList<>(),
                null).join();
        localDatabase.writeCompleteAd("ad5", "card5", ad5, users.get(0),
                new ArrayList<>(), new ArrayList<>(),
                null).join();


        checkRetrievedData(localDatabase, ads, cards, 1);

        localDatabase.cleanFavorites();

    }

    @Test
    public void removingCardRemovesUserTest() {

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser,
                null).join();
        Ad.AdBuilder[] adBuilders = getAdBuilders();

        Ad ad1 = adBuilders[0].build();
        Ad ad2 = adBuilders[1].build();
        Ad ad3 = adBuilders[2].build();
        Ad ad4 = adBuilders[3].build();
        Ad ad5 = adBuilders[4].build();

        Ad[] ads = {ad1, ad2, ad3, ad4, ad5};

        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0),
                new ArrayList<>(), new ArrayList<>(),
                null).join();
        localDatabase.writeCompleteAd("ad2", "card2", ad2, users.get(1),
                new ArrayList<>(), new ArrayList<>(),
                null).join();
        localDatabase.writeCompleteAd("ad3", "card3", ad3, users.get(2),
                new ArrayList<>(), new ArrayList<>(),
                null).join();
        localDatabase.writeCompleteAd("ad4", "card4", ad4, users.get(3),
                new ArrayList<>(), new ArrayList<>(),
                null).join();
        localDatabase.writeCompleteAd("ad5", "card5", ad5, users.get(4),
                new ArrayList<>(), new ArrayList<>(),
                null).join();

        checkRetrievedData(localDatabase, ads, this.cards, 5);

        int size = cards.size();

        for (int i = 0; i < size; ++i) {
            localDatabase.removeCard("card" + (i + 1));

            List<Card> retrievedCards = localDatabase.getCards().join();

            assertThat(retrievedCards.size(), is(size - 1 - i));

            for (Card card : retrievedCards) {
                assertNotEquals(card, users.get(i));
            }

            Ad retrievedAd = localDatabase.getAd("ad" + (i + 1)).join();
            assertNull(retrievedAd);

            User retrievedUser = localDatabase.getUser("user" + (i + 1)).join();
            assertNull(retrievedUser);
        }


        localDatabase.cleanFavorites();
    }

    @Test
    public void profilePictureIsRemovedTest() {

        String appFolder =
                InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir().getPath();

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser,
                null).join();
        Ad.AdBuilder[] adBuilders = getAdBuilders();

        Ad ad1 = adBuilders[0].build();

        AppUser userWithProfilePic = new AppUser("user1", "fakeemail" +
                "@testappart.ch");
        userWithProfilePic.setProfileImagePathAndName("test.jpeg");

        Bitmap fakeBitmap = Bitmap.createBitmap(100, 100,
                Bitmap.Config.ARGB_8888);
        fakeBitmap.eraseColor(Color.rgb(getRandomColor(),
                getRandomColor(), getRandomColor()));

        localDatabase.writeCompleteAd("ad1", "card1", ad1, userWithProfilePic
                , new ArrayList<>(), new ArrayList<>(), fakeBitmap).join();


        File profilePicFile = new File(appFolder + "/favorites" +
                "/currentUser/users/user1/profileImage.jpeg");
        assertTrue(profilePicFile.exists());

        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0),
                new ArrayList<>(), new ArrayList<>(), null).join();
        assertFalse(profilePicFile.exists());

        localDatabase.cleanFavorites();
    }

    public int getRandomColor() {
        return (int) (Math.random() * 255);
    }

    @Test
    public void currentUserProfilePicTest() throws IOException {
        String appFolder =
                InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir().getPath();

        localDatabase.cleanFavorites();

        Bitmap fakeBitmap = Bitmap.createBitmap(100, 100,
                Bitmap.Config.ARGB_8888);
        fakeBitmap.eraseColor(Color.rgb(getRandomColor(),
                getRandomColor(), getRandomColor()));

        currentUser.setProfileImagePathAndName("fake.jpeg");
        localDatabase.setCurrentUser(currentUser,
                fakeBitmap).join();

        File originalFile = new File(appFolder + "/favorites/test" +
                "/profileImage.jpeg");
        new File(appFolder + "/favorites/test").mkdirs();
        FileOutputStream fosOut;
        try {
            fosOut = new FileOutputStream(originalFile);
            fakeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fosOut);
            fosOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File retrievedBitmapFile = new File(appFolder + "/favorites" +
                "/profileImage.jpeg");
        byte[] bytesOriginal = Files.readAllBytes(originalFile.toPath());
        byte[] bytesRetrieved =
                Files.readAllBytes(retrievedBitmapFile.toPath());

        assertArrayEquals(bytesOriginal, bytesRetrieved);


        localDatabase.cleanFavorites();

    }
}
