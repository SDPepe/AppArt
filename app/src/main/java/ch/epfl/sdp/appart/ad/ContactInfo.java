package ch.epfl.sdp.appart.ad;

public class ContactInfo {
    public final String userEmail;
    public final String userPhoneNumber;
    public final String name;

    public ContactInfo(String userEmail, String userPhoneNumber, String name) {
        if (userEmail == null || userPhoneNumber == null || name == null)
            throw new IllegalArgumentException();
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.name = name;
    }

    public ContactInfo(ContactInfo contactInfo) {
        if (contactInfo == null) {
            throw new IllegalArgumentException("contact info cannot be null");
        }
        this.name = contactInfo.name;
        this.userEmail = contactInfo.userEmail;
        this.userPhoneNumber = contactInfo.userPhoneNumber;
    }
}
