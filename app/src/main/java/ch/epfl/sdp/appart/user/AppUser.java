package ch.epfl.sdp.appart.user;

/**
 * This class represents a generic user of our application - the actual
 * communication with firebase is done in the Firebase login service
 */
public class AppUser implements User {
    private String name;
    private final String userId;
    private String email;
    private String phoneNumber;
    private int age;
    private Gender gender;
    private String profilePicture;


    public AppUser(String userId, String email) {
        if (userId == null || email == null) {
            throw new IllegalArgumentException("ERROR - failed to create user: null parameters");
        }
        this.userId = userId;
        this.email = email;
        this.gender = Gender.NOT_SELECTED;

        String[] split = email.split("@");
        if (split[0] != null) {
            this.name = split[0];
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        if (gender == null) {
            throw new IllegalArgumentException("ERROR - gender parameter was null");
        }
        this.gender = gender;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public boolean hasUniversityEmail() {
        return UniversityEmailChecker.has(this.email);
    }

}
