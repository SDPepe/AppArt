package ch.epfl.sdp.appart.database.firebaselayout;

/**
 * Un-instantiable Holder for the layout of FIrebase Storage and Firestore.
 */
public final class FirebaseLayout {

    private FirebaseLayout(){}

    /**
     * Name of the ads directory in Firebase Storage and Firestore
     */
    public static final String ADS_DIRECTORY = "ads";

    /**
     * Name of the ads directory in Firebase Storage and Firestore
     */
    public static final String CARDS_DIRECTORY = "cards";

    /**
     * Name of the users directory in Firebase Storage and Firestore
     */
    public static final String USERS_DIRECTORY = "users";

    /**
     * Default name of photo added to FirebaseStorage
     */
    public static final String PHOTO_NAME = "Photo";

    /**
     * Default name of profile picture image added to FirebaseStorage
     */
    public static final String PROFILE_IMAGE_NAME = "profileImage";

    /**
     * Name of directory for default user icon images in FirebaseStorage
     */
    public static final String DEFAULT_USER_ICON_DIRECTORY = "default";

    /**
     * Separator to concatenate paths
     */
    public static final String SEPARATOR = "/";

    /**
     * supported image formats
     */
    public static final String JPEG = ".jpeg";
    public static final String PNG = ".png";
}
