package ch.epfl.sdp.appart;

import org.junit.Test;

import ch.epfl.sdp.appart.ad.ContactInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ContactInfoTest {

    @Test
    public void nullArgumentsTest1(){
        assertThrows(IllegalArgumentException.class, () -> {
            ContactInfo ci = new ContactInfo(null, "079", "Test");
        });
    }

    @Test
    public void nullArgumentsTest2(){
        assertThrows(IllegalArgumentException.class, () -> {
            ContactInfo ci = new ContactInfo("test@email.com", null, "Test");
        });
    }

    @Test
    public void nullArgumentsTest3(){
        assertThrows(IllegalArgumentException.class, () -> {
            ContactInfo ci = new ContactInfo("test@email.com", "079", null);
        });
    }

    @Test
    public void constructorTest(){
        ContactInfo ci = new ContactInfo("test@email.com", "079", "Test");
        assertEquals("test@email.com", ci.userEmail);
        assertEquals("079", ci.userPhoneNumber);
        assertEquals("Test", ci.name);
    }



}
