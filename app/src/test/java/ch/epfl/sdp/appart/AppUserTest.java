package ch.epfl.sdp.appart.user;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AppUserTest {

    @Test
    public void userIsCreatedCorrectly() {
        AppUser user = new AppUser("1234", "Carlo", "carlo.musso@epfl.ch", "1234567890");
        assertNotEquals(null, user);
    }

    @Test
    public void nameGetterAndSetterWork() {
        AppUser user = new AppUser("1234", "Carlo", "carlo.musso@epfl.ch", "1234567890");
        user.setName("Antoine");
        assertEquals("Antoine", user.getName());
    }

    @Test
    public void emailGetterAndSetterWork() {
        AppUser user = new AppUser("1234", "Carlo", "carlo.musso@epfl.ch", "1234567890");
        user.setUserEmail("carlomusso98@gmail.com");
        assertEquals("carlomusso98@gmail.com", user.getUserEmail());
    }

    @Test
    public void phoneNumberGetterAndSetterWork() {
        AppUser user = new AppUser("1234", "Carlo", "carlo.musso@epfl.ch", "1234567890");
        user.setPhoneNumber("1234567812345678");
        assertEquals("1234567812345678", user.getPhoneNumber());
    }

    @Test
    public void profileImageGetterAndSetterWork() {
        AppUser user = new AppUser("1234", "Carlo", "carlo.musso@epfl.ch", "1234567890");
        user.setProfileImage("/home/Carlo/pictures/foo.jpg");
        assertEquals("/home/Carlo/pictures/foo.jpg", user.getProfileImage());
    }

    @Test
    public void userIdGetterWorks() {
        AppUser user = new AppUser("1234", "Carlo", "carlo.musso@epfl.ch", "1234567890");
        assertEquals("1234", user.getUserId());
    }

    @Test
    public void constructorFailsWithNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppUser user = new AppUser(null, null, null, null);
        });
    }

    @Test
    public void nameSetterFailsWithNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppUser user = new AppUser("1234", "Carlo", "carlo.musso@epfl.ch", "1234567890");
            user.setName(null);
        });
    }

    @Test
    public void emailSetterFailsWithNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppUser user = new AppUser("1234", "Carlo", "carlo.musso@epfl.ch", "1234567890");
            user.setUserEmail(null);
        });
    }

    @Test
    public void phoneNumberSetterFailsWithNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppUser user = new AppUser("1234", "Carlo", "carlo.musso@epfl.ch", "1234567890");
            user.setPhoneNumber(null);
        });
    }

    @Test
    public void imageSetterFailsWithNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppUser user = new AppUser("1234", "Carlo", "carlo.musso@epfl.ch", "1234567890");
            user.setProfileImage(null);
        });
    }

    @Test
    public void hasUniversityEmailReturnsTrueForUniversityEmail() {
        AppUser user = new AppUser("1234", "Carlo", "carlo.musso@epfl.ch", "1234567890");
        assertEquals(true, user.hasUniversityEmail());
    }

    @Test
    public void hasUniversityEmailReturnsFalseForNonUniversityEmail() {
        AppUser user = new AppUser("1234", "Carlo", "carlomusso98@gmail.com", "1234567890");
        assertEquals(false, user.hasUniversityEmail());
    }

    @Test
    public void hasUniversityEmailReturnsFalseForFakeSyntaxEmail() {
        AppUser user = new AppUser("1234", "Carlo", "quentin@gmail.com@epfl.ch", "1234567890");
        assertEquals(false, user.hasUniversityEmail());
    }
}