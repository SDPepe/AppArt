package ch.epfl.sdp.appart.user;

import android.media.Image;

import java.util.List;

public interface User {

    /**
     * @return the name of the user
     */
    String getName();

    /**
     * sets the name of the user
     */
    void setName(String name);

    /**
     * getter method for user email
     *
     * @return user email as a string
     */
    String getUserEmail();

    /**
     * setter method for user email
     */
    void setUserEmail(String email);

    /**
     * getter method for user phone number
     *
     * @return user phone number as a string
     */
    String getPhoneNumber();

    /**
     * setter method for user phone number
     */
    void setPhoneNumber(String phoneNumber);

    /**
     * getter method for user profile picture
     *
     * @return the profile picture (Object type was chosen as generic, should be changed once the object Picture is identified)
     */
    String getProfileImage();

    /**
     * setter method for user profile picture
     */
    void setProfileImage(String img);

    /**
     * getter method for user gender
     */
    Gender getGender();

    /**
     * setter method for user gender
     */
    void setGender(Gender gender);

    /**
     * getter method for user age
     */
    int getAge();

    /**
     * setter method for user age
     */
    void setAge(int age);


    /**
     * getter method for ID
     *
     * @return the unique id of the user
     */
    String getUserId();

    /**
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
}
