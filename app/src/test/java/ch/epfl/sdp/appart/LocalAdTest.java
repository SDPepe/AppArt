package ch.epfl.sdp.appart;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.database.local.LocalAd;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

public class LocalAdTest {

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
            new AppUser("user5", "test4@appart.ch"),
            new AppUser("user5", "test4@appart.ch"));

    List<String> picturesReferences = Arrays.asList(
            "fake_ad_1.jpg",
            "fake_ad_2.jpg",
            "fake_ad_3.jpg",
            "fake_ad_4.jpg",
            "fake_ad_5.jpg"
    );
    Ad ad1 = new Ad.AdBuilder()
            .withTitle("EPFL")
            .withPrice(100000)
            .withPricePeriod(PricePeriod.WEEK)
            .withStreet("Fake street").withCity("1015 Lausanne")
            .withAdvertiserName("Test advertiser")
            .withAdvertiserId("user1")
            .withDescription("Fake desc")
            .withPhotosIds(picturesReferences)
            .hasVRTour(false)
            .build();
    Ad ad2 = new Ad.AdBuilder()
            .withTitle("EPFL")
            .withPrice(100000)
            .withPricePeriod(PricePeriod.MONTH)
            .withStreet("Fake street").withCity("1015 Lausanne")
            .withAdvertiserName("Test advertiser")
            .withAdvertiserId("user2")
            .withDescription("Fake desc")
            .withPhotosIds(picturesReferences)
            .hasVRTour(false)
            .build();
    Ad ad3 = new Ad.AdBuilder()
            .withTitle("EPFL")
            .withPrice(100000)
            .withPricePeriod(PricePeriod.MONTH)
            .withStreet("Fake street").withCity("1015 Lausanne")
            .withAdvertiserName("Test advertiser")
            .withAdvertiserId("user3")
            .withDescription("Fake desc")
            .withPhotosIds(picturesReferences)
            .hasVRTour(false)
            .build();
    Ad ad4 = new Ad.AdBuilder()
            .withTitle("EPFL")
            .withPrice(100000)
            .withPricePeriod(PricePeriod.MONTH)
            .withStreet("Fake street").withCity("1015 Lausanne")
            .withAdvertiserName("Test advertiser")
            .withAdvertiserId("user4")
            .withDescription("Fake desc")
            .withPhotosIds(picturesReferences)
            .hasVRTour(false)
            .build();
    Ad ad5 = new Ad.AdBuilder()
            .withTitle("EPFL")
            .withPrice(100000)
            .withPricePeriod(PricePeriod.MONTH)
            .withStreet("Fake street").withCity("1015 Lausanne")
            .withAdvertiserName("Test advertiser")
            .withAdvertiserId("user5")
            .withDescription("Fake desc")
            .withPhotosIds(picturesReferences)
            .hasVRTour(false)
            .build();

    @Test
    public void simpleWriteLocalAdTests() {
        LocalAd localAd = new LocalAd(".");

        //TODO: Need to test folders already existing to see remove functionnality
        //TODO: Need to test corrupt data
        //TODO: Need to test several ad per users
        localAd.writeCompleteAd("ad1", "card1", ad1, users.get(0), str -> CompletableFuture.completedFuture(null), "currentUser");
        localAd.writeCompleteAd("ad2", "card1", ad1, users.get(0), str -> CompletableFuture.completedFuture(null), "currentUser");
        List<Card> cards = localAd.getCards("currentUser");
    }
}
