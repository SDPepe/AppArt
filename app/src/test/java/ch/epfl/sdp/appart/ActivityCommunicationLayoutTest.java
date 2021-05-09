package ch.epfl.sdp.appart;

import org.junit.Test;

import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ActivityCommunicationLayoutTest {

    @Test
    public void providingActivityNameIsCorrect() {
        assertEquals("activity", ActivityCommunicationLayout.PROVIDING_ACTIVITY_NAME);
    }

    @Test
    public void providingUserIdIsCorrect() {
        assertEquals("userID", ActivityCommunicationLayout.PROVIDING_USER_ID);
    }

    @Test
    public void providingCardIdIsCorrect() {
        assertEquals("cardID", ActivityCommunicationLayout.PROVIDING_CARD_ID);
    }

    @Test
    public void providingAdIdIsCorrect() {
        assertEquals("adID", ActivityCommunicationLayout.PROVIDING_AD_ID);
    }

    @Test
    public void providingEmailIsCorrect() {
        assertEquals("email", ActivityCommunicationLayout.PROVIDING_EMAIL);
    }

    @Test
    public void providingPasswordIsCorrect() {
        assertEquals("password", ActivityCommunicationLayout.PROVIDING_PASSWORD);
    }

    @Test
    public void providingImageUriIsCorrect() {
        assertEquals("uri", ActivityCommunicationLayout.PROVIDING_IMAGE_URI);
    }

    @Test
    public void providingSizeIsCorrect() {
        assertEquals("size", ActivityCommunicationLayout.PROVIDING_SIZE);
    }

    @Test
    public void userProfileActivityStringIsCorrect() {
        assertEquals("UserProfileActivity", ActivityCommunicationLayout.USER_PROFILE_ACTIVITY);
    }

    @Test
    public void adCreationActivityStringIsCorrect() {
        assertEquals("AdCreationActivity", ActivityCommunicationLayout.AD_CREATION_ACTIVITY);
    }
}