package ch.epfl.sdp.appart.database.local;

import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.utils.StoragePathBuilder;

/**
 * This class is a util class for the local database. It performs most of the
 * "paths" operations.
 */
public class LocalDatabasePaths {


    public static String appPath = null;
    public static String userID = null;
    public static String cardID = null;


    public static final String favoritesFolder = "favorites";
    public static final String profilePicName =
            FirebaseLayout.PROFILE_IMAGE_NAME + ".jpeg";
    //data file storing the actual ad information
    public static final String dataFileName = "data.fav";
    //folder containing every users needed for the local ads, i.e the ad owner
    public static final String usersFolder = "users";
    //data file containing the data for the user
    public static final String userData = "user.data";
    //data file containing the data for the current user, i.e the last logged
    // in user (when the app was online)s
    public static final String currentUserData = "currentUser.data";

    public static String favoritesFolder() {
        return new StoragePathBuilder().toDirectory(appPath).toDirectory(favoritesFolder).build();
    }

    public static String currentUserFolder(String currentUserID) {
        return new StoragePathBuilder().toDirectory(favoritesFolder()).toDirectory(currentUserID).build();
    }

    public static String currentUserProfilePicture() {
        return new StoragePathBuilder().toDirectory(favoritesFolder()).withFile(profilePicName);
    }

    public static String currentUserData() {
        return new StoragePathBuilder().toDirectory(favoritesFolder()).withFile(currentUserData);
    }

    public static String usersFolder(String currentUserID) {
        return new StoragePathBuilder().toDirectory(currentUserFolder(currentUserID)).toDirectory(usersFolder).build();
    }

    public static String userFolder(String currentUserID, String userID) {
        return new StoragePathBuilder().toDirectory(usersFolder(currentUserID)).toDirectory(userID).build();
    }

    public static String userFolder(String currentUserID) {
        return userFolder(currentUserID, userID);
    }

    public static String userProfilePic(String currentUserID, String userID) {
        return new StoragePathBuilder().toDirectory(userFolder(currentUserID,
                userID)).withFile(profilePicName);
    }

    public static String userProfilePic(String currentUserID) {
        return userProfilePic(currentUserID, userID);
    }

    public static String userData(String currentUserID, String userID) {
        return new StoragePathBuilder().toDirectory(userFolder(currentUserID,
                userID)).withFile(userData);
    }

    public static String userData(String currentUserID) {
        return userData(currentUserID, userID);
    }

    public static String cardFolder(String currentUserID, String cardID) {
        return new StoragePathBuilder().toDirectory(currentUserFolder(currentUserID)).toDirectory(cardID).build();
    }

    public static String cardFolder(String currentUserID) {
        return cardFolder(currentUserID, cardID);
    }

    public static String cardData(String currentUserID, String cardID) {
        return new StoragePathBuilder().toDirectory(cardFolder(currentUserID,
                cardID)).withFile(dataFileName);
    }

    public static String cardData(String currentUserID) {
        return cardData(currentUserID, cardID);
    }

    public static String imageFile(String currentUserID, String cardID, int i
            , String name) {
        return new StoragePathBuilder().toDirectory(cardFolder(currentUserID,
                cardID)).withFile(name + i + ".jpeg");
    }

    public static String photoFile(String currentUserID, int i) {
        return photoFile(currentUserID, cardID, i);
    }

    public static String photoFile(String currentUserID, String cardID, int i) {
        return imageFile(currentUserID, cardID, i, FirebaseLayout.PHOTO_NAME);
    }

    public static String panoramaFile(String currentUserID, int i) {
        return panoramaFile(currentUserID, cardID, i);
    }

    public static String panoramaFile(String currentUserID, String cardID,
                                      int i) {
        return imageFile(currentUserID, cardID, i,
                FirebaseLayout.PANORAMA_NAME);
    }


}
