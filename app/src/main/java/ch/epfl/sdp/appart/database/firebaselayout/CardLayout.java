package ch.epfl.sdp.appart.database.firebaselayout;

/**
 * Un-instantiable Holder for the activity_fullscreen_image.xml of the fields for a Card on Firestore.
 */
public final class CardLayout {

    private CardLayout() {
    }

    /**
     * absolute path to the folder containing the card image
     */
    public final static String IMAGE_DIRECTORY = "ads";

    /**
     * absolute path to the cards directory in Firestore
     */
    public final static String AD_ID = "ad_id"; //adId

    /**
     * @apiNote : String on Firestore
     */
    public final static String USER_ID = "user_id"; //userId

    /**
     * @apiNote : String on Firestore
     */
    public final static String CITY = "city";

    /**
     * @apiNote : long on Firestore
     */
    public final static String PRICE = "price";

    /**
     * @apiNote : String on Firestore
     */
    public final static String IMAGE = "picture_id"; //imageUrl
}
