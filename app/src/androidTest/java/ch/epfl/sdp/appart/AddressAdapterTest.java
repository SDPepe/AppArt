package ch.epfl.sdp.appart;

import org.junit.Test;

import java.util.Locale;

import ch.epfl.sdp.appart.location.place.address.Address;
import ch.epfl.sdp.appart.location.place.address.AddressAdapter;

import static org.junit.Assert.assertTrue;

public class AddressAdapterTest {

    @Test
    public void adaptCorrectAddress() {
        android.location.Address a = new android.location.Address(Locale.FRENCH);
        a.setPostalCode("1400");
        a.setLocality("Lausanne");
        a.setThoroughfare("Rue du chat");
        a.setSubThoroughfare("1c");
        Address b = AddressAdapter.fromAndroidToAppartAddress(a);
        assertTrue(b.getAddress().equals("Rue du chat 1c, 1400 Lausanne"));

    }

}
