package ch.epfl.sdp.appart;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.place.address.AddressFactory;
import ch.epfl.sdp.appart.place.PlaceOfInterest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class PlaceOfInterestTest {

    @Test
    public void createPlaceOfInterestHasExpectedBehavior() {
        PlaceOfInterest place = new PlaceOfInterest();

        place.setName("Grapix");
        place.setId("id");

        assertEquals("id", place.getPlaceId());
        assertEquals("Grapix", place.getName());

        place.setAddress("petin ou chnoque 1a, 1300 Lausanne");
        assertEquals(true, place.getAddress().equals(AddressFactory.makeAddress("petin ou chnoque 1a, 1300 Lausanne")));
        assertTrue(place.hasAddress());
        place.setAddress("lolilol invalid");
        assertNull(place.getAddress());
        assertFalse(place.hasAddress());

        place.setLocation(0, 0);
        assertEquals(new Location(0, 0), place.getLocation());
        assertTrue(place.hasLocation());

        place.setLocation(Double.NaN, Double.NaN);
        assertNull(place.getLocation());
        assertFalse(place.hasLocation());

        place.setRating(1.2);
        assertTrue(1.2 == place.getRating());

        place.setTypes(Sets.newSet("restaurant", "pool", ""));

        assertTrue(place.getTypes().contains("restaurant"));
        assertTrue(place.getTypes().contains("pool"));
        assertFalse(place.getTypes().contains(""));


    }

}
