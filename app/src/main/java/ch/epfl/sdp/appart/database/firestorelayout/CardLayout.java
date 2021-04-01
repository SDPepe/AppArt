package ch.epfl.sdp.appart.database.firestorelayout;

/**
 * Un-instantiable Holder for the layout of the fields for a Card on Firestore.
 */
public final class CardLayout {

    private CardLayout() {}

    /**
     * absolute path to the cards directory in Firestore
     */
    public final static String DIRECTORY = "cards";

    /**
     * absolute path to the cards directory in Firestore
     */
    public final static String AD_ID = "adId";

    /**
     * @apiNote : String on Firestore
     */
    public final static String USER_ID    = "userId";

    /**
     * @apiNote : String on Firestore
     */
    public final static String CITY       = "city";

    /**
     * @apiNote : long on Firestore
     */
    public final static String PRICE      = "price";

    /**
     * @apiNote : String on Firestore
     */
    public final static String IMAGE      = "imageUrl";
}
