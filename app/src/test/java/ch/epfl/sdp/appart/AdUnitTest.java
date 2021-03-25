package ch.epfl.sdp.appart;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.appart.scrolling.ad.Ad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class AdUnitTest {

    private Ad ad;

    @Before
    public void setup() {
        List<String> refs = new ArrayList<>();
        refs.add("ref");
        ad = new Ad("Ad title", "1000 / mo", "Station 18, 1015 Lausanne",
                "unknown", "Cool place", refs);
    }


    @Test
    public void exceptionOnNullArg1() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAD = new Ad(null, "1000 / mo", "Station 18, 1015 Lausanne",
                    "unknown", "Cool place", new ArrayList<String>());
        });
    }

    @Test
    public void exceptionOnNullArg2() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAD = new Ad("Ad title", null, "Station 18, 1015 Lausanne",
                    "unknown", "Cool place", new ArrayList<String>());
        });
    }

    @Test
    public void exceptionOnNullArg3() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAD = new Ad("Ad title", "1000 / mo", null,
                    "unknown", "Cool place", new ArrayList<String>());
        });
    }

    @Test
    public void exceptionOnNullArg4() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAD = new Ad("Ad title", "1000 / mo", "Station 18, 1015 Lausanne",
                    null, "Cool place", new ArrayList<String>());
        });
    }

    @Test
    public void exceptionOnNullArg5() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAD = new Ad("Ad title", "1000 / mo", "Station 18, 1015 Lausanne",
                    "unknown", null, new ArrayList<String>());
        });
    }

    @Test
    public void exceptionOnNullArg6() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ad failAD = new Ad("Ad title", "1000 / mo", "Station 18, 1015 Lausanne",
                    "unknown", "Cool place", null);
        });
    }

    @Test
    public void titleGetterTest() {
        assertEquals("Ad title", ad.getTitle());
    }

    @Test
    public void priceGetterTest() {
        assertEquals("1000 / mo", ad.getPrice());
    }

    @Test
    public void addressGetterTest() {
        assertEquals("Station 18, 1015 Lausanne", ad.getAddress());
    }

    @Test
    public void advertiserGetterTest() {
        assertEquals("unknown", ad.getAdvertiserId());
    }

    @Test
    public void descriptionGetterTest() {
        assertEquals("Cool place", ad.getDescription());
    }

    @Test
    public void photosrefsGetterTest() {
        List<String> refs = new ArrayList<>();
        refs.add("ref");
        assertEquals(refs, ad.getPhotosRefs());
    }

    @Test
    public void hsaVTourGetterTest() {
        List<String> refs = new ArrayList<>();
        refs.add("ref");
        ad = new Ad("Ad title", "1000 / mo", "Station 18, 1015 Lausanne",
                "unknown", "Cool place", refs, true);
        assertEquals(true, ad.hasVRTour());
    }
}
