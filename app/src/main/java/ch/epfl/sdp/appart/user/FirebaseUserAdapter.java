package ch.epfl.sdp.appart.user;

import android.media.Image;

import com.google.firebase.auth.FirebaseUser;

public class FirebaseUserAdapter implements User {
    private String name;
    private String userId;
    private String email;
    private String phoneNumber;
    private Image profilePicture;


    public FirebaseUserAdapter(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void setUserEmail(String email) {
        this.email = email;
    }

    @Override
    public String getUserEmail() {
        return email;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setProfileImage(Image img) {
        this.profilePicture = img;
    }

    @Override
    public Image getProfileImage() {
        return profilePicture;
    }

    @Override
    public boolean hasUniversityEmail() {
        /*
         * there should be a static method (not in the user class) which verifies if the
         * email is present in the set of university addresses (e.g. "@epfl.ch", "@unil.ch")
         */
        return false;
    }

    // TO BE CONTINUED
    public FirebaseUser getUserInstance() {return null;}
}
