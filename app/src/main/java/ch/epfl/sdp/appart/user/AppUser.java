package ch.epfl.sdp.appart.user;

/**
 * This class represents a generic user of our application - the actual
 * communication with firebase is done in the Firebase login service
 */
public class AppUser implements User {
    private final String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String profilePicture;


    public AppUser(String userId, String name, String email, String phoneNumber, String profilePicture) {
        //User has not necessary a name
        if (userId == null || /*name == null ||*/ email == null) {
            throw new IllegalArgumentException("ERROR - failed to create user: null parameters");
        }
        this.userId = userId;
        this.name = name;
        this.email = email;
        if(phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
        if(profilePicture != null) {
            this.profilePicture = profilePicture;
        }
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("ERROR - name parameter was null");
        }
        this.name = name;
    }

    @Override
    public String getUserEmail() {
        return email;
    }

    @Override
    public void setUserEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("ERROR - email parameter was null");
        }
        this.email = email;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("ERROR - phone number parameter was null");
        }
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getProfileImage() {
        return profilePicture;
    }

    @Override
    public void setProfileImage(String img) {
        if (img == null) {
            throw new IllegalArgumentException("ERROR - image parameter was null");
        }
        this.profilePicture = img;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public boolean hasUniversityEmail() {
        return UniversityEmailDatabase.has(this.email);
    }

}
