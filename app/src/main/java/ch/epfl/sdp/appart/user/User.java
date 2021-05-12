package ch.epfl.sdp.appart.user;

import java.util.List;
import java.util.Set;

public interface User {

    /**
     * getter for users name
     *
     * @return the name of the user
     */
    String getName();

    /**
     * setter for users name
     *
     * @param name the new name for the user
     */
    void setName(String name);

    /**
     * getter for user email
     *
     * @return user email as a string
     */
    String getUserEmail();

    /**
     * setter for user email
     *
     * @param email the new email for the user
     */
    void setUserEmail(String email);

    /**
     * getter for user phone number
     *
     * @return user phone number as a string
     */
    String getPhoneNumber();

    /**
     * setter for user phone number
     *
     * @param phoneNumber the new phoneNumber for the user
     */
    void setPhoneNumber(String phoneNumber);

    /**
     * getter for user profile picture
     *
     * @return the profile picture as a path (e.g. users/default/photo.jpeg)
     */
    String getProfileImagePathAndName();

    /**
     * setter for user profile picture
     *
     * @param img the image complete path (e.g. users/default/photo.jpeg)
     * of the users new profile picture
     */
    void setProfileImagePathAndName(String img);

    /**
     * getter for users age
     *
     * @return the age of the user as int
     */
    long getAge();

    /**
     * setter for users age
     *
     * @param age the age of the user
     */
    void setAge(long age);

    /**
     * getter for users gender
     *
     * @return the gender of the user as String
     */
    String getGender();

    /**
     * setter for users gender
     *
     * @param gender the gender of the user as string
     */
    void setGender(String gender);

    /**
     * getter for users id
     *
     * @return the unique id of the user as String
     */
    String getUserId();

    /**
     * university email checker
     *
     * @return true if the user is registered with a university domain, false otherwise
     */
    boolean hasUniversityEmail();

    /**
     * getter method for ad references list
     *
     * @return a list of IDs of the ads of this user
     */
    List<String> getAdsIds();

    /**
     * adds the ad id to the list of this user
     *
     * @param id, the ID of the ad
     */
    void addAdId(String id);

    /**
     * getter method for the list of the favorite ads of the user
     * @return a list of the ids of the ads the user has marked as favorites
     */
    Set<String> getFavoritesIds();

    /**
     * adds the ad id to the list of the favorite ads of this user
     * @param id, the id of the ad
     */
    void addFavorite(String id);

    /**
     * sets default user-icon uri
     */
    void setDefaultProfileImage();

    /**
     * @return true if the user is currently using the
     * default user icon as profile image, false if the
     * user previously uploaded a profile image
     */
    Boolean hasDefaultProfileImage();

}
