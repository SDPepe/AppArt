package ch.epfl.sdp.appart.user;

import android.media.Image;

// dummy class for testing
public class FirebaseUserAdapter implements User {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public void setUserEmail(String email) {

    }

    @Override
    public String getUserEmail() {
        return null;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {

    }

    @Override
    public String getPhoneNumber() {
        return null;
    }

    @Override
    public void setProfileImage(Image img) {

    }

    @Override
    public Image getProfileImage() {
        return null;
    }

    @Override
    public boolean hasUniversityEmail() {
        return false;
    }
}
