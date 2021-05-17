package ch.epfl.sdp.appart;

import org.junit.Test;

import ch.epfl.sdp.appart.location.Location;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LocationTest {

    @Test
    public void constructLocationCorrectly() {
        Location location = new Location();
        assertTrue(location.latitude == 0);
        assertTrue(location.longitude == 0);
        Location l = new Location();
        assertTrue(location.hashCode() == l.hashCode());
        assertTrue(location.equals(l));
    }
}
