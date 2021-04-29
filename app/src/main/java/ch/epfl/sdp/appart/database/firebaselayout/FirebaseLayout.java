package ch.epfl.sdp.appart.database.firebaselayout;

/**
 * Un-instantiable Holder for the activity_fullscreen_image.xml of FIrebase Storage and Firestore.
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
     * Separator to concatenate paths
     */
    public static final String SEPARATOR = "/";
}
