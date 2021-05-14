package ch.epfl.sdp.appart;

import org.junit.Test;

import ch.epfl.sdp.appart.location.address.Address;
import ch.epfl.sdp.appart.location.address.AddressFactory;
import ch.epfl.sdp.appart.location.address.MalformedAddressException;

import static org.junit.Assert.assertThrows;

public class AddressTest {

    @Test
    public void validAddressesCreation() {
        Address a = AddressFactory.makeAddress("Quai de Nogent 1", "1400", "Yverdon-les-Bains");

    }

    @Test
    public void validAddressesCreationWithFullAddress() {
        Address a = AddressFactory.makeAddress("Quai de Nogent 1a, 1400 Yverdon-les-Bains");
    }

    @Test
    public void invalidAddressStreetCreation() {
        assertThrows(MalformedAddressException.class, () -> {
            Address a = AddressFactory.makeAddress("1", "1400", "Yverdon-les-Bains");
        });
    }

    @Test
    public void invalidAddressPostalCodeCreation() {
        assertThrows(MalformedAddressException.class, () -> {
            Address a = AddressFactory.makeAddress("street", "140a", "Yverdon-les-Bains");
        });
    }

    @Test
    public void invalidAddressLocalityCreation() {
        assertThrows(MalformedAddressException.class, () -> {
            Address a = AddressFactory.makeAddress("1", "1400", "1Yverdon-les-Bains");
        });
    }

}
