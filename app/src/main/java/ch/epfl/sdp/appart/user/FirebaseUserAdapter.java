package ch.epfl.sdp.appart.user;

import android.media.Image;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseUserAdapter implements User {
    private String name;
    private String userId;
    private String email;
    private String phoneNumber;
    private Image profilePicture;


    public FirebaseUserAdapter(String userId, String name, String email) {
        if (userId == null || name == null || email == null) {
            throw new IllegalArgumentException("Failed to create user: null parameters");
        }
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
        if (name == null) {
            throw new IllegalArgumentException("cannot set null name");
        }
        this.name = name;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void setUserEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("cannot set null name");
        }
        this.email = email;
    }

    @Override
    public String getUserEmail() {
        return email;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("cannot set null name");
        }
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setProfileImage(Image img) {
        if (img == null) {
            throw new IllegalArgumentException("cannot set null name");
        }
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
    public FirebaseUser getUserInstance() {
       throw new UnsupportedOperationException("The function call is still not implemented");
    }
}
