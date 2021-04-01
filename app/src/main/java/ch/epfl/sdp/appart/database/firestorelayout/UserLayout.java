package ch.epfl.sdp.appart.database.firestorelayout;

/**
 * Un-instantiable Holder for the layout of the fields for a User on Firestore.
 */
public class UserLayout {

    private UserLayout() {}

    /**
     * absolute path to the users directory in Firestore
     */
    public final static String DIRECTORY = "users";

    /**
     * @apiNote : long on Firestore
     */
    public final static String AGE        = "age";

    /**
     * @apiNote : String on Firestore
     */
    public final static String EMAIL      = "email";

    /**
     * @apiNote : long on Firestore
     */
    public final static String GENDER     = "gender";

    /**
     * @apiNote : String on Firestore
     */
    public final static String NAME       = "name";

    /**
     * @apiNote : String on Firestore
     */
    public final static String PICTURE    = "pfpRef";

    /**
     * @apiNote : String on Firestore
     */
    public final static String PHONE      = "phone";

}
