package ch.epfl.sdp.appart;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.appart.scrolling.PricePeriod;
import ch.epfl.sdp.appart.scrolling.ad.Ad;
import ch.epfl.sdp.appart.scrolling.ad.ContactInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

public class AdUnitTest {

    private Ad ad;
    private ContactInfo ci;

    @Before
    public void setup(){
        List<String> refs = new ArrayList<>();
        refs.add("ref");
        ci = new ContactInfo("test@appart.ch", "0790790709",
                "TestUser");
        ad = new Ad("Ad title", 1000, PricePeriod.MONTH, "Station 18",
                "1015 Lausanne", "unknown", "Cool place", refs,
                false, ci);
    }


    @Test
    public void exceptionOnNullArg1(){
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAD = new Ad(null, 1000, PricePeriod.MONTH, "Station 18",
                    "1015 Lausanne", "unknown", "Cool place",
                    new ArrayList<>(), false, ci);
        });
    }

    @Test
    public void exceptionOnNullArg2(){
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAD = new Ad("Ad title", 1000, null, "Station 18",
                    "1015 Lausanne", "unknown", "Cool place",
                    new ArrayList<>(), false, ci);
        });
    }

    @Test
    public void exceptionOnNullArg3(){
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAD = new Ad("Ad title", 1000, PricePeriod.MONTH, null,
                    "1015 Lausanne", "unknown", "Cool place",
                    new ArrayList<>(), false, ci);
        });
    }

    @Test
    public void exceptionOnNullArg4(){
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAD = new Ad("Ad title", 1000, PricePeriod.MONTH, "Station 18",
                    null, "unknown", "Cool place",
                    new ArrayList<>(), false, ci);
        });
    }

    @Test
    public void exceptionOnNullArg5(){
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAD = new Ad("Ad title", 1000, PricePeriod.MONTH, "Station 18",
                    "1015 Lausanne", null, "Cool place",
                    new ArrayList<>(), false, ci);
        });
    }

    @Test
    public void exceptionOnNullArg6(){
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAD = new Ad("Ad title", 1000, PricePeriod.MONTH, "Station 18",
                    "1015 Lausanne", "unknown", null,
                    new ArrayList<>(), false, ci);
        });
    }

    @Test
    public void exceptionOnNullArg7(){
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAD = new Ad("Ad title", 1000, PricePeriod.MONTH, "Station 18",
                    "1015 Lausanne", "unknown", "Cool place",
                    null, false, ci);
        });
    }

    @Test
    public void exceptionOnNullArg8(){
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAD = new Ad("Ad title", 1000, PricePeriod.MONTH, "Station 18",
                    "1015 Lausanne", "unknown", "Cool place",
                    new ArrayList<>(), false, null);
        });
    }

    @Test
    public void gettersTest(){
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
        assertEquals(ci, ad.getContactInfo());
    }

}
