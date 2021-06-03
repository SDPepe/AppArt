package ch.epfl.sdp.appart.user;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.scrolling.card.Card;

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
    private long age;
    private Gender gender;
    private String profileImagePathAndName;
    private List<String> adsIds;
    private Set<String> favoritesIds;
    private boolean hasDefaultProfileImage = true;

/* default values */
    private static final String DEFAULT_IMAGE_NAME_NO_GENDER = "user_example_no_gender";
    private static final String DEFAULT_IMAGE_NAME_FEMALE = "user_example_female";
    private static final String DEFAULT_IMAGE_NAME_MALE = "user_example_male";

    /**
     * App user constructor
     *
     * @param userId the unique id of the user in the Firestore database
     * @param email  the email registered with the users account
     */
    public AppUser(String userId, String email) {
        if (userId == null || email == null) {
            throw new IllegalArgumentException("ERROR - failed to create user: null parameters");
        }
        this.userId = userId;
        this.email = email;
        this.gender = Gender.NOT_SELECTED;
        this.adsIds = new ArrayList<>();
        favoritesIds = new HashSet<>();
        setDefaultProfileImage();

        /* As default, everything before '@' in email is selected as name,
        this is overwritten by the name setter once the user specifies it */
        String[] split = email.split("@");
        if (split[0] != null) {
            this.name = split[0];
        }
    }

    /**
     * getter for users name
     *
     * @return the name of the user
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * setter for users name
     *
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
     *
     * @return user email as a string
     */
    @Override
    public String getUserEmail() {
        return email;
    }

    /**
     * setter for user email
     *
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
     *
     * @return user phone number as a string
     */
    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * setter for user phone number
     *
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
     *
     * @return the profile picture as a path (e.g. users/default/photo.jpeg)
     */
    @Override
    public String getProfileImagePathAndName() {
        return profileImagePathAndName;
    }

    /**
     * setter for user profile picture
     *
     * @param img the image complete path (e.g. users/default/photo.jpeg)
     * of the users new profile picture
     */
    @Override
    public void setProfileImagePathAndName(String img) {
        if (img == null) {
            throw new IllegalArgumentException("ERROR - image parameter was null");
        }
        this.profileImagePathAndName = img;
        this.hasDefaultProfileImage = false;
    }

    /**
     * getter for users age
     * @return the age of the user as int
     */
    @Override
    public long getAge() {
        return age;
    }

    /**
     * setter for users age
     *
     * @param age the age of the user
     */
    @Override
    public void setAge(long age) {
        this.age = age;
    }

    /**
     * getter for users gender
     *
     * @return the gender of the user as String
     */
    @Override
    public String getGender() {
        return gender.name();
    }

    /**
     * setter for users gender
     *
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
     *
     * @return the unique id of the user as String
     */
    @Override
    public String getUserId() {
        return userId;
    }

    /**
     * university email checker
     *
     * @return true if the user is registered with a university domain, false otherwise
     */
    @Override
    public boolean hasUniversityEmail() {
        return UniversityEmailChecker.has(this.email);
    }

    @Override
    public List<String> getAdsIds() {
        return adsIds;
    }

    @Override
    public void addAdId(String id) {
        if (id == null)
            throw new IllegalArgumentException("The argument is null");
        adsIds.add(id);
    }

    @Override
    public Set<String> getFavoritesIds() {
        return favoritesIds;
    }

    @Override
    public void addFavorite(String id) {
        if (id == null)
            throw new IllegalArgumentException("id can't be null");
        if (!favoritesIds.contains(id))
            favoritesIds.add(id);
        else
            favoritesIds.remove(id);
    }

    @Override
    public void removeFavorite(String id) {
        this.favoritesIds.remove(id);
    }

    /**
     *
     * @return the name of the user gender-icon image
     */
    private String findDrawableIdByGender() {
        if (gender == Gender.FEMALE) {
            return DEFAULT_IMAGE_NAME_FEMALE + FirebaseLayout.PNG;
        } else if (gender == Gender.MALE) {
            return DEFAULT_IMAGE_NAME_MALE + FirebaseLayout.PNG;
        } else {
            return DEFAULT_IMAGE_NAME_NO_GENDER + FirebaseLayout.PNG;
        }
    }

    /**
     * sets the default user icon
     */
    public void setDefaultProfileImage(){
        StringBuilder defaultImagePathInDb = new StringBuilder();
        defaultImagePathInDb
                .append(FirebaseLayout.USERS_DIRECTORY)
                .append(FirebaseLayout.SEPARATOR)
                .append(FirebaseLayout.DEFAULT_USER_ICON_DIRECTORY)
                .append(FirebaseLayout.SEPARATOR)
                .append(findDrawableIdByGender());

        this.profileImagePathAndName = defaultImagePathInDb.toString();
        this.hasDefaultProfileImage = true;
    }

    /**
     * @return true if the user is currently using the
     * default user icon as profile image, false if the
     * user previously uploaded a profile image
     */
    public Boolean hasDefaultProfileImage() {
        return this.hasDefaultProfileImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this.userId == null) return false;
        if (!(o instanceof AppUser)) return false;
        AppUser other = (AppUser) o;
        return this.userId.equals(other.userId);
    }

}
