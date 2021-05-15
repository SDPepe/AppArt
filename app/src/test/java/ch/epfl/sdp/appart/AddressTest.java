package ch.epfl.sdp.appart;

import org.junit.Test;

import ch.epfl.sdp.appart.location.address.Address;
import ch.epfl.sdp.appart.location.address.AddressFactory;
import ch.epfl.sdp.appart.location.address.MalformedAddressException;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertEquals;

public class AddressTest {

    @Test
    public void validAddressesCreation() {
        Address a = AddressFactory.makeAddress("Quai de Nogent 1", "1400", "Yverdon-les-Bains");

    }

    @Test
    public void validAddressesCreationWithFullAddress() {
        Address a = AddressFactory.makeAddress("Quai de Nogent 1a, 1400 Yverdon-les-Bains");
        Address b = AddressFactory.makeAddress("Quai de Nogent 1a, 1400 Yverdon-les-Bains");
        Address c = AddressFactory.makeAddress("Avenue Haldimand 103, Yverdon-les-Bains");
        Address d = AddressFactory.makeAddress("Rue de la Roselière 3, Yverdon-les-Bains");
    }

    @Test
    public void validAddressesCreationWithFullAddressOrElse() {
        assertEquals(null, AddressFactory.makeAddressOrElse("Yverdon-les-Bains", () -> { return null; } ));
    }

    @Test
    public void invalidAddressesCreationWithFullAddress() {
        assertThrows(MalformedAddressException.class, () -> {
            Address a = AddressFactory.makeAddress("Quai de Nogent, 1400 Yverdon-les-Bains");
        });
    }

    @Test
    public void invalidAddressesCreationWithoutPostalCodeAddress() {
        assertThrows(MalformedAddressException.class, () -> {
            Address a = AddressFactory.makeAddress("Quai de Nogent 1a, 1300");
        });
    }

    @Test
    public void validAddressesCreationWithoutPostalCodeAddress() {
        Address a = AddressFactory.makeAddress("Quai de Nogent 1a, Yverdon-les-Bains");
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
            Address a = AddressFactory.makeAddress("street 1", "140a", "Yverdon-les-Bains");
        });
    }

    @Test
    public void invalidAddressLocalityCreation() {
        assertThrows(MalformedAddressException.class, () -> {
            Address a = AddressFactory.makeAddress("abcd 1", "1400", "1Yverdon-les-Bains");
        });
    }

}
