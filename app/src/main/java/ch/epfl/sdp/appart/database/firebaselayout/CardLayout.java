package ch.epfl.sdp.appart.database.firebaselayout;

/**
 * Un-instantiable Holder for the layout of the fields for a Card on Firestore.
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
     * @apiNote : string on Firestore
     */
    public final static String PERIOD = "period";

    /**
     * @apiNote : String on Firestore
     */
    public final static String IMAGE = "picture_id"; //imageUrl

    /**
     * @apiNote : boolean on Firestore
     */
    public static final String HAS_VTOUR = "has_vr_tour";

    public static final String ID = "ID";
}
