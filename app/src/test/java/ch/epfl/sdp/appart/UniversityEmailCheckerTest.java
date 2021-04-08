package ch.epfl.sdp.appart;

import org.junit.Test;

import ch.epfl.sdp.appart.user.UniversityEmailChecker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UniversityEmailCheckerTest {

    @Test
    public void hasMethodOfUniversityDatabaseWorksWithUniversityEmail() {
        String email1 = "aaaa.bbbb@epfl.ch";
        String email2 = "aaaa.bbbb@unil.ch";
        String email3 = "aaaa.bbbb@unige.ch";

        assertTrue(UniversityEmailChecker.has(email1));
        assertTrue(UniversityEmailChecker.has(email2));
        assertTrue(UniversityEmailChecker.has(email3));

    }

    @Test
    public void hasMethodOfUniversityDatabaseWorksWithNonUniversityEmail() {
        String email1 = "aaaa.bbbb@gmail.com";
        String email2 = "aaaa.bbbb@yahoo.com";
        String email3 = "aaaa.bbbb@libero.it";

        assertFalse(UniversityEmailChecker.has(email1));
        assertFalse(UniversityEmailChecker.has(email2));
        assertFalse(UniversityEmailChecker.has(email3));

    }

    @Test
    public void hasMethodOfUniversityDatabaseFailsWithNullParameter() {
        assertThrows(IllegalArgumentException.class, () -> UniversityEmailChecker.has(null));
    }

    @Test
    public void emailSyntaxIsValidMethodFailsWithNullParameter() {
        assertThrows(IllegalArgumentException.class, () -> UniversityEmailChecker.emailSyntaxIsValid(null));
    }

    @Test
    public void emailSyntaxIsValidMethodFailsWithWrongEmails() {
        String email1 = "aaaa.bbbb@epfl.ch@gmail.com";
        String email2 = "aaaa.bbbb@unil@epfl.com";

        assertFalse(UniversityEmailChecker.emailSyntaxIsValid(email1));
        assertFalse(UniversityEmailChecker.emailSyntaxIsValid(email2));

    }

    @Test
    public void emailSyntaxIsValidMethodWorksWithCorrectEmails() {
        String email1 = "aaaa.bbbb@gmail.com";
        String email2 = "aaaa.bbbb@yahoo.com";
        String email3 = "aaaa.bbbb@libero.it";

        assertTrue(UniversityEmailChecker.emailSyntaxIsValid(email1));
        assertTrue(UniversityEmailChecker.emailSyntaxIsValid(email2));
        assertTrue(UniversityEmailChecker.emailSyntaxIsValid(email3));

    }


}