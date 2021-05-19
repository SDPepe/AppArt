package ch.epfl.sdp.appart.database.firebaselayout;

/**
 * Un-instantiable Holder for the layout of the fields for a User on Firestore.
 */
public final class UserLayout {

    private UserLayout() {
    }

    /**
     * @apiNote : long on Firestore
     */
    public final static String AGE = "age";

    /**
     * @apiNote : String on Firestore
     */
    public final static String EMAIL = "email";

    /**
     * @apiNote : long on Firestore
     */
    public final static String GENDER = "gender";

    /**
     * @apiNote : String on Firestore
     */
    public final static String NAME = "name";

    /**
     * @apiNote : String on Firestore
     */
    public final static String PICTURE = "picture_id"; //pfpRef

    /**
     * @apiNote : String on Firestore
     */
    public final static String PHONE = "phone";

    /**
     * @apiNote : List<String> on Firestore
     */
    public final static String AD_IDS = "ad_ids";

    /**
     * @apiNote : List<String> on Firestore
     */
    public final static String FAVORITE_IDS = "favorite_ids";

    public final static String ID = "ID";

}
