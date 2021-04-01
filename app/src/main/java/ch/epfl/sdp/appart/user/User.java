package ch.epfl.sdp.appart.user;

public interface User {

    /**
     * getter for users name
     * @return the name of the user
     */
    String getName();

    /**
     * setter for users name
     * @param name the new name for the user
     */
    void setName(String name);

    /**
     * getter for user email
     * @return user email as a string
     */
    String getUserEmail();

    /**
     * setter for user email
     * @param email the new email for the user
     */
    void setUserEmail(String email);

    /**
     * getter for user phone number
     * @return user phone number as a string
     */
    String getPhoneNumber();

    /**
     * setter for user phone number
     * @param phoneNumber the new phoneNumber for the user
     */
    void setPhoneNumber(String phoneNumber);

    /**
     * getter for user profile picture
     * @return the profile picture as a path String
     */
    String getProfileImage();

    /**
     * setter for user profile picture
     * @param img the image path for the users new profile picture
     */
    void setProfileImage(String img);

    /**
     * getter for users age
     * @return the age of the user as int
     */
    int getAge();

    /**
     * setter for users age
     * @param age the age of the user
     */
    void setAge(int age);

    /**
     * getter for users gender
     * @return the gender of the user as String
     */
    String getGender();

    /**
     * setter for users gender
     * @param gender the gender of the user as string
     */
    void setGender(String gender);

    /**
     * getter for users id
     * @return the unique id of the user as String
     */
    String getUserId();

    /**
     * university email checker
     * @return true if the user is registered with a university domain, false otherwise
     */
    boolean hasUniversityEmail();

}
