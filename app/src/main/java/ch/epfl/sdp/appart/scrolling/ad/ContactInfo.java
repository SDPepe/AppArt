package ch.epfl.sdp.appart.scrolling.ad;

/**
 * Object representing information to contact a user.
 * <p>
 * It contains the user name, phone number and email address.
 */
public class ContactInfo {
    public final String userEmail;
    public final String userPhoneNumber;
    public final String name;

    /**
     * Constructor of ContactInfo.
     *
     * @param userEmail       the email address of the user
     * @param userPhoneNumber the phone number of the user
     * @param name            the name of the user
     */
    public ContactInfo(String userEmail, String userPhoneNumber, String name) {
        if (userEmail == null || userPhoneNumber == null || name == null)
            throw new IllegalArgumentException();
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.name = name;
    }
}
