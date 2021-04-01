package ch.epfl.sdp.appart.user;

/**
 * This class represents a generic user of our application - it is used to manage
 * a local copy of the users information in-app. The local copy will then be
 * transferred to Firestore database by the FirestoreDatabaseService.
 */
public class AppUser implements User {

    /* user attributes */
    private final String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private int age;
    private Gender gender;
    private String profilePicture;


    /**
     * App user constructor
     * @param userId the unique id of the user in the Firestore database
     * @param email the email registered with the users account
     */
    public AppUser(String userId, String email) {
        if (userId == null || email == null) {
            throw new IllegalArgumentException("ERROR - failed to create user: null parameters");
        }
        this.userId = userId;
        this.email = email;
        this.gender = Gender.NOT_SELECTED;

        /* As default, everything before '@' in email is selected as name,
        this is overwritten by the name setter once the user specifies it */
        String[] split = email.split("@");
        if (split[0] != null) {
            this.name = split[0];
        }
    }

    /**
     * getter for users name
     * @return the name of the user
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * setter for users name
     * @param name the new name for the user
     */
    @Override
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("ERROR - name parameter was null");
        }
        this.name = name;
    }

    /**
     * getter for user email
     * @return user email as a string
     */
    @Override
    public String getUserEmail() {
        return email;
    }

    /**
     * setter for user email
     * @param email the new email for the user
     */
    @Override
    public void setUserEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("ERROR - email parameter was null");
        }
        this.email = email;
    }

    /**
     * getter for user phone number
     * @return user phone number as a string
     */
    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * setter for user phone number
     * @param phoneNumber the new phoneNumber for the user
     */
    @Override
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("ERROR - phone number parameter was null");
        }
        this.phoneNumber = phoneNumber;
    }

    /**
     * getter for user profile picture
     * @return the profile picture as a path String
     */
    @Override
    public String getProfileImage() {
        return profilePicture;
    }

    /**
     * setter for user profile picture
     * @param img the image path for the users new profile picture
     */
    @Override
    public void setProfileImage(String img) {
        if (img == null) {
            throw new IllegalArgumentException("ERROR - image parameter was null");
        }
        this.profilePicture = img;
    }

    /**
     * getter for users age
     * @return the age of the user as int
     */
    @Override
    public int getAge() {
        return age;
    }

    /**
     * setter for users age
     * @param age the age of the user
     */
    @Override
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * getter for users gender
     * @return the gender of the user as String
     */
    @Override
    public String getGender() {
        return gender.name();
    }

    /**
     * setter for users gender
     * @param gender the gender of the user as string
     */
    @Override
    public void setGender(String gender) {
        if (gender == null) {
            throw new IllegalArgumentException("ERROR - gender parameter was null");
        }
        this.gender = Gender.valueOf(gender);
    }

    /**
     * getter for users id
     * @return the unique id of the user as String
     */
    @Override
    public String getUserId() {
        return userId;
    }

    /**
     * university email checker
     * @return true if the user is registered with a university domain, false otherwise
     */
    @Override
    public boolean hasUniversityEmail() {
        return UniversityEmailChecker.has(this.email);
    }

}
