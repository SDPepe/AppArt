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
}
