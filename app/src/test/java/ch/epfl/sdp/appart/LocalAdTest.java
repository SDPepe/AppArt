package ch.epfl.sdp.appart;

import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.database.local.LocalAd;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

public class LocalAdTest {

    @Test
    public void writeLocalAdTest() {

        Card card = new Card("cardID", "adID", "ownerID", "City", 1000,
                "fakeURL");
        ArrayList<String> photoRefs = new ArrayList<>();
        photoRefs.add("fakeRefs");
        Ad ad = new Ad("titleAD", 1000, PricePeriod.MONTH, "street", "city",
                "adID", "description", photoRefs, false);
        User user = new AppUser("ID", "mail");

        LocalAd.writeCompleteAd(card, ad, user, "test.list");
        LocalAd.LocalCompleteAd completeAd = LocalAd.loadCompleteAd("test" +
                ".list").join();


    }
}
