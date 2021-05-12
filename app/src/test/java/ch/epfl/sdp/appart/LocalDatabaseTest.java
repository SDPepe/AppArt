package ch.epfl.sdp.appart;

import org.junit.Test;

import java.io.IOException;
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;


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

     /*localAd.

        /*localAd.writeCompleteAd("ad1", "card1", ad1, users.get(0),
                str -> CompletableFuture.completedFuture(null), "currentUser");
        localAd.writeCompleteAd("ad2", "card2", ad2, users.get(1),
                str -> CompletableFuture.completedFuture(null), "currentUser");
        localAd.writeCompleteAd("ad3", "card3", ad3, users.get(2),
                str -> CompletableFuture.completedFuture(null), "currentUser");
        localAd.writeCompleteAd("ad4", "card4", ad4, users.get(3),
                str -> CompletableFuture.completedFuture(null), "currentUser");
        localAd.writeCompleteAd("ad5", "card5", ad5, users.get(4),
                str -> CompletableFuture.completedFuture(null), "currentUser");*/


        /*List<Card> cardsRetrieved = localAd.getCards("currentUser");
        assertEquals(cards, cardsRetrieved);

        for(int i = 0; i < users.size(); ++i) {
            AppUser userRetrieved = (AppUser) localAd.getUser("currentUser", "user" + (i+1));
            assertEquals((AppUser) users.get(i), userRetrieved);
        }*/

    @Test
    public void setCurrentUserTest() throws IOException, ClassNotFoundException {
        LocalDatabase localDatabase = new LocalDatabase(".");

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser, adFolderPath -> CompletableFuture.completedFuture(null));

        AppUser currentUserRetrieved = (AppUser) localDatabase.loadCurrentUser();

        assertEquals(currentUserRetrieved, (AppUser)currentUser);

        localDatabase.cleanFavorites();
    }

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

        Ad.AdBuilder[] adBuilders = {adBuilder1, adBuilder2, adBuilder3, adBuilder4, adBuilder5};
        return adBuilders;
    }

    @Test
    public void addAdsWithoutPictureTest() throws IOException, ClassNotFoundException {
        LocalDatabase localDatabase = new LocalDatabase(".");

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser, adFolderPath -> CompletableFuture.completedFuture(null));
        User retrievedCurrentUser = localDatabase.getCurrentUser();
        assertNotNull(retrievedCurrentUser);
        assertEquals(retrievedCurrentUser, currentUser);

        Ad.AdBuilder[] adBuilders = getAdBuilders();

        Ad ad1 = adBuilders[0].build();
        Ad ad2 = adBuilders[1].build();
        Ad ad3 = adBuilders[2].build();
        Ad ad4 = adBuilders[3].build();
        Ad ad5 = adBuilders[4].build();

        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad2", "card2", ad2, users.get(1), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad3", "card3", ad3, users.get(2), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad4", "card4", ad4, users.get(3), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad5", "card5", ad5, users.get(4), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));

        Ad ads[] = {ad1, ad2, ad3, ad4, ad5};

        checkRetrievedData(localDatabase, ads, cards, 5);

        localDatabase.cleanFavorites();
    }

    private void checkRetrievedData(LocalDatabase localDatabase, Ad[] ads, List<Card> cards, int nbValidUsers) throws IOException, ClassNotFoundException {
        List<Card> cardsRetrieved = localDatabase.getCards("currentUser");
        assertEquals(cards, cardsRetrieved);

        for(int i = 0; i < users.size(); ++i) {
            AppUser userRetrieved = (AppUser) localDatabase.getUser("currentUser", "user" + (i+1));
            if(i >= nbValidUsers) {
                assertNull(userRetrieved);
            } else {
                assertEquals((AppUser) users.get(i), userRetrieved);
            }
        }

        for(int i = 0; i < ads.length; ++i) {
            Ad adRetrieved = localDatabase.getAd("ad" + (i+1), "currentUser");
            assertThat(adRetrieved.equals(ads[i]), is(true));
        }
    }

    @Test
    public void addAdsWithOneUserOnly() throws IOException,
            ClassNotFoundException {
        LocalDatabase localDatabase = new LocalDatabase(".");

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser, adFolderPath -> CompletableFuture.completedFuture(null));
        Ad.AdBuilder[] adBuilders = getAdBuilders();

        for(int i = 0; i < adBuilders.length; ++i) {
            adBuilders[i].withAdvertiserId("user1");
        }

        Ad ad1 = adBuilders[0].build();
        Ad ad2 = adBuilders[1].build();
        Ad ad3 = adBuilders[2].build();
        Ad ad4 = adBuilders[3].build();
        Ad ad5 = adBuilders[4].build();

        Ad[] ads = {ad1, ad2, ad3, ad4, ad5};

        List<Card> cards = Arrays.asList(new Card("card1", "ad1", "user1", "Lausanne", 1000, "card_1.jpeg"),
                new Card("card2", "ad2", "user1", "Lausanne", 1000, "card_2.jpeg"),
                new Card("card3", "ad3", "user1", "Lausanne", 1000, "card_3.jpeg"),
                new Card("card4", "ad4", "user1", "Lausanne", 1000, "card_4.jpeg"),
                new Card("card5", "ad5", "user1", "Lausanne", 1000, "card_5.jpeg"));

        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad2", "card2", ad2, users.get(0), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad3", "card3", ad3, users.get(0), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad4", "card4", ad4, users.get(0), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad5", "card5", ad5, users.get(0), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));


        checkRetrievedData(localDatabase, ads, cards, 1);

        localDatabase.cleanFavorites();

    }

    @Test
    public void removingCardRemovesUserTest() throws IOException, ClassNotFoundException {
        LocalDatabase localDatabase = new LocalDatabase(".");

        localDatabase.cleanFavorites();

        localDatabase.setCurrentUser(currentUser, adFolderPath -> CompletableFuture.completedFuture(null));
        Ad.AdBuilder[] adBuilders = getAdBuilders();

        Ad ad1 = adBuilders[0].build();
        Ad ad2 = adBuilders[1].build();
        Ad ad3 = adBuilders[2].build();
        Ad ad4 = adBuilders[3].build();
        Ad ad5 = adBuilders[4].build();

        Ad[] ads = {ad1, ad2, ad3, ad4, ad5};

        localDatabase.writeCompleteAd("ad1", "card1", ad1, users.get(0), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad2", "card2", ad2, users.get(1), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad3", "card3", ad3, users.get(2), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad4", "card4", ad4, users.get(3), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));
        localDatabase.writeCompleteAd("ad5", "card5", ad5, users.get(4), new ArrayList<>(), new ArrayList<>(), str -> CompletableFuture.completedFuture(null));

        checkRetrievedData(localDatabase, ads, this.cards, 5);

        int size = cards.size();

        for(int i =0; i < size; ++i) {
            localDatabase.removeCard("card" + (i + 1), "currentUser");

            List<Card> retrievedCards = localDatabase.getCards("currentUser");

            assertThat(retrievedCards.size(), is(size - 1 - i));

            for(Card card: retrievedCards) {
                assertNotEquals(card, users.get(i));
            }

            Ad retrievedAd = localDatabase.getAd("ad" + (i + 1), "currentUser");
            assertNull(retrievedAd);

            User retrievedUser = localDatabase.getUser("currentUser", "user" + (i + 1));
            assertNull(retrievedUser);
        }




        localDatabase.cleanFavorites();
    }


}
