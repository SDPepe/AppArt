package ch.epfl.sdp.appart.user;

public interface User {

    /**
     * @return the name of the user
     */
    public String getName();

    /**
     * sets the name of the user
     */
    public void setName();

    /**
     * getter method for ID
     * @return the unique id of the user
     */
     public String getUserId();

    /**
     * setter method for user email
     */
    public void setUserEmail();

    /**
     * getter method for user email
     * @return user email as a string
     */
     public String getUserEmail();

    /**
     * setter method for user phone number
     */
    public void setPhoneNumber();

    /**
     * getter method for user phone number
     * @return user phone number as a string
     */
     public String getPhoneNumber();

    /**
     * setter method for user profile picture
     */
     public void setProfileImage();

    /**
     * getter method for user profile picture
     * @return the profile picture (Object type was chosen as generic, should be changed once the object Picture is identified)
     */
    public Image getProfileImage();

    /**
     * @return true if the user is registered with a university domain, false otherwise
     */
    public boolean hasUniversityEmail();
}
