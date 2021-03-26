package ch.epfl.sdp.appart.user;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UniversityEmailDatabaseTest {

    @Test
    public void hasMethodOfUniversityDatabaseWorksWithUniversityEmail() {
        String email1 = "aaaa.bbbb@epfl.ch";
        String email2 = "aaaa.bbbb@unil.ch";
        String email3 = "aaaa.bbbb@unige.ch";

        assertEquals(true, UniversityEmailDatabase.has(email1));
        assertEquals(true, UniversityEmailDatabase.has(email2));
        assertEquals(true, UniversityEmailDatabase.has(email3));

    }

    @Test
    public void hasMethodOfUniversityDatabaseWorksWithNonUniversityEmail() {
        String email1 = "aaaa.bbbb@gmail.com";
        String email2 = "aaaa.bbbb@yahoo.com";
        String email3 = "aaaa.bbbb@libero.it";

        assertEquals(false, UniversityEmailDatabase.has(email1));
        assertEquals(false, UniversityEmailDatabase.has(email2));
        assertEquals(false, UniversityEmailDatabase.has(email3));

    }

    @Test
    public void hasMethodOfUniversityDatabaseFailsWithNullParameter() {
        assertThrows(IllegalArgumentException.class, () -> {
            UniversityEmailDatabase.has(null);
        });
    }

    @Test
    public void emailSyntaxIsValidMethodFailsWithNullParameter() {
        assertThrows(IllegalArgumentException.class, () -> {
            UniversityEmailDatabase.emailSyntaxIsValid(null);
        });
    }

    @Test
    public void emailSyntaxIsValidMethodFailsWithWrongEmails() {
        String email1 = "aaaa.bbbb@epfl.ch@gmail.com";
        String email2 = "aaaa.bbbb@unil@epfl.com";

        assertEquals(false, UniversityEmailDatabase.emailSyntaxIsValid(email1));
        assertEquals(false, UniversityEmailDatabase.emailSyntaxIsValid(email2));

    }

    @Test
    public void emailSyntaxIsValidMethodWorksWithCorrectEmails() {
        String email1 = "aaaa.bbbb@gmail.com";
        String email2 = "aaaa.bbbb@yahoo.com";
        String email3 = "aaaa.bbbb@libero.it";

        assertEquals(true, UniversityEmailDatabase.emailSyntaxIsValid(email1));
        assertEquals(true, UniversityEmailDatabase.emailSyntaxIsValid(email2));
        assertEquals(true, UniversityEmailDatabase.emailSyntaxIsValid(email3));

    }


}