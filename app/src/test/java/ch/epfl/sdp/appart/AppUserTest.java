package ch.epfl.sdp.appart;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.Gender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AppUserTest {

    @Test
    public void userIsCreatedCorrectly() {
        AppUser user = new AppUser("1234", "test.appart@epfl.ch");
        assertNotEquals(null, user);
        AppUser user2 = new AppUser("1234", "test.appart.ch");
        assertNotEquals(null, user2);
    }

    @Test
    public void nameGetterAndSetterWork() {
        AppUser user = new AppUser("1234", "test.appart@epfl.ch");
        user.setName("Antoine");
        assertEquals("Antoine", user.getName());
    }

    @Test
    public void emailGetterAndSetterWork() {
        AppUser user = new AppUser("1234", "test.appart@epfl.ch");
        user.setUserEmail("carlomusso98@gmail.com");
        assertEquals("carlomusso98@gmail.com", user.getUserEmail());
    }

    @Test
    public void phoneNumberGetterAndSetterWork() {
        AppUser user = new AppUser("1234", "test.appart@epfl.ch");
        user.setPhoneNumber("1234567812345678");
        assertEquals("1234567812345678", user.getPhoneNumber());
    }

    @Test
    public void profileImageGetterAndSetterWork() {
        AppUser user = new AppUser("1234", "test.appart@epfl.ch");
        user.setProfileImage("/home/Carlo/pictures/foo.jpg");
        assertEquals("/home/Carlo/pictures/foo.jpg", user.getProfileImage());
    }

    @Test
    public void ageGetterAndSetterWork() {
        AppUser user = new AppUser("1234", "test.appart@epfl.ch");
        user.setAge(25);
        assertEquals(25, user.getAge());
    }

    @Test
    public void genderSetterThrowsOnNull() {
        AppUser user = new AppUser("1234", "test.appart@epfl.ch");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setGender(null);
        });
    }

    @Test
    public void genderGetterAndSetterWork() {
        AppUser user = new AppUser("1234", "test.appart@epfl.ch");
        user.setGender(Gender.FEMALE);
        assertEquals(Gender.FEMALE, user.getGender());
        user.setGender(Gender.OTHER);
        assertEquals(Gender.OTHER, user.getGender());
    }

    @Test
    public void userIdGetterWorks() {
        AppUser user = new AppUser("1234", "test.appart@epfl.ch");
        assertEquals("1234", user.getUserId());
    }

    @Test
    public void constructorFailsWithNullParameters1() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppUser user = new AppUser(null, null);
        });
    }

    @Test
    public void constructorFailsWithNullParameters2() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppUser user = new AppUser("42", null);
        });
    }

    @Test
    public void constructorFailsWithNullParameters3() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppUser user = new AppUser(null, "test.appart@epfl.ch");
        });
    }

    @Test
    public void nameSetterFailsWithNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppUser user = new AppUser("1234", "test.appart@epfl.ch");
            user.setName(null);
        });
    }

    @Test
    public void emailSetterFailsWithNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppUser user = new AppUser("1234", "test.appart@epfl.ch");
            user.setUserEmail(null);
        });
    }

    @Test
    public void phoneNumberSetterFailsWithNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppUser user = new AppUser("1234", "test.appart@epfl.ch");
            user.setPhoneNumber(null);
        });
    }

    @Test
    public void imageSetterFailsWithNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppUser user = new AppUser("1234", "test.appart@epfl.ch");
            user.setProfileImage(null);
        });
    }

    @Test
    public void hasUniversityEmailReturnsTrueForUniversityEmail() {
        AppUser user = new AppUser("1234", "test.appart@epfl.ch");

        assertTrue(user.hasUniversityEmail());
    }

    @Test
    public void hasUniversityEmailReturnsFalseForNonUniversityEmail() {
        AppUser user = new AppUser("1234", "carlomusso98@gmail.com");
        assertFalse(user.hasUniversityEmail());
    }

    @Test
    public void hasUniversityEmailReturnsFalseForFakeSyntaxEmail() {
        AppUser user = new AppUser("1234", "test.appart@yahoo.it@epfl.ch");
        assertFalse(user.hasUniversityEmail());
    }

    @Test
    public void adIdSetterThrowsOnNullArgument(){
        AppUser user = new AppUser("1234", "test@appart.ch");
        assertThrows(IllegalArgumentException.class, () -> user.addAdId(null));
    }

    @Test
    public void adIdSetterWorksOnGoodValue(){
        AppUser user = new AppUser("1234", "test@appart.ch");
        user.addAdId("id");
        List<String> ids = new ArrayList<>();
        ids.add("id");
        assertEquals(ids, user.getAdsIds());
    }

    @Test
    public void adIdGetterWorks(){
        AppUser user = new AppUser("1234", "test@appart.ch");
        assertEquals(new ArrayList<>(), user.getAdsIds());
    }
}