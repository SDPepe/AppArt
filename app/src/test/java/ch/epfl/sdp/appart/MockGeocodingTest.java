package ch.epfl.sdp.appart;

import org.junit.Test;

import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.place.address.AddressFactory;
import ch.epfl.sdp.appart.location.geocoding.MockGeocodingService;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MockGeocodingTest {

    @Test
    public void geocodingServiceBehaveAsExpectedDummy() {
        MockGeocodingService service = new MockGeocodingService();
        assertEquals(new Location(0, 0), service.getLocation(AddressFactory.makeAddress("lol 1b, 2000 Place")).join());
        assertEquals(AddressFactory.makeAddress("Dummy street 1c, 1000 DummyCity"), service.getAddress(new Location(123, 123)).join());
        assertTrue(0.0f == service.getDistance(AddressFactory.makeAddress("lol 1b, 2000 Place"), AddressFactory.makeAddress("lol 1b, 2000 Place")).join());
        assertTrue(0.0f == service.getDistance(new Location(123, 123), new Location(123, 123)).join());
    }

}
