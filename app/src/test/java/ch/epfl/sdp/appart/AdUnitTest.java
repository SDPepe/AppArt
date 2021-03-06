package ch.epfl.sdp.appart;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.ContactInfo;
import ch.epfl.sdp.appart.ad.PricePeriod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

public class AdUnitTest {

    private Ad ad;
    private ContactInfo ci;

    @Before
    public void setup() {
        List<String> refs = new ArrayList<>();
        refs.add("ref");
        ad = new Ad("Ad title", 1000, PricePeriod.MONTH, "Station 18",
                "1015 Lausanne", "unknown", "unknown", "Cool place", refs, refs, false);
    }


    @Test
    public void exceptionOnNullArg1() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAd = new Ad(null, 1000, PricePeriod.MONTH, "Station 18",
                    "1015 Lausanne", "unknown", "unknown", "Cool place",
                    new ArrayList<>(), new ArrayList<>(), false);
        });
    }

    @Test
    public void exceptionOnNullArg2() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAd = new Ad("Ad title", 1000, null, "Station 18",
                    "1015 Lausanne", "unknown", "unknown", "Cool place",
                    new ArrayList<>(), new ArrayList<>(), false);
        });
    }

    @Test
    public void exceptionOnNullArg3() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAd = new Ad("Ad title", 1000, PricePeriod.MONTH, null,
                    "1015 Lausanne", "unknown", "unknown", "Cool place",
                    new ArrayList<>(), new ArrayList<>(), false);
        });
    }

    @Test
    public void exceptionOnNullArg4() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAd = new Ad("Ad title", 1000, PricePeriod.MONTH, "Station 18",
                    null, "unknown", "unknown", "Cool place",
                    new ArrayList<>(), new ArrayList<>(), false);
        });
    }

    @Test
    public void exceptionOnNullArg5() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAd = new Ad("Ad title", 1000, PricePeriod.MONTH, "Station 18",
                    "1015 Lausanne", "unknown", null, "Cool place",
                    new ArrayList<>(), new ArrayList<>(),false);
        });
    }

    @Test
    public void exceptionOnNullArg6() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAd = new Ad("Ad title", 1000, PricePeriod.MONTH, "Station 18",
                    "1015 Lausanne", "unknown", "unknown", null,
                    new ArrayList<>(), new ArrayList<>(), false);
        });
    }

    @Test
    public void exceptionOnNullArg7() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAd = new Ad("Ad title", 1000, PricePeriod.MONTH, "Station 18",
                    "1015 Lausanne", "unknown", "unknown", "Cool place",
                    null, null, false);

        });
    }

    @Test
    public void gettersTest() {
        assertEquals("Ad title", ad.getTitle());
        assertEquals(1000, ad.getPrice());
        assertEquals(PricePeriod.MONTH, ad.getPricePeriod());
        assertEquals("Station 18", ad.getStreet());
        assertEquals("1015 Lausanne", ad.getCity());
        assertEquals("unknown", ad.getAdvertiserId());
        assertEquals("Cool place", ad.getDescription());
        List<String> refs = new ArrayList<>();
        refs.add("ref");
        assertEquals(refs, ad.getPhotosRefs());
        assertFalse(ad.hasVRTour());
    }

}
