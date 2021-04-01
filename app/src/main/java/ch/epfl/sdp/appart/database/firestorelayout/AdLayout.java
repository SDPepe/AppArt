package ch.epfl.sdp.appart.database.firestorelayout;

/**
 * Un-instantiable Holder for the layout of the fields for an Ad on Firestore.
 */
public final class AdLayout {

    private AdLayout() {}

    /**
     * absolute path to the ads directory in Firestore
     */
    public final static String DIRECTORY   = "ads";

    /**
     * absolute path to the pictures directory of the ad in Firestore
     */
    public final static String PICTURES_DIRECTORY   = "photosRefs";

    /**
     * @apiNote : String on Firestore
     */
    public final static String TITLE        = "title";

    /**
     * @apiNote : long on Firestore
     */
    public final static String PRICE        = "price";

    /**
     * @apiNote : String on Firestore
     */
    public final static String STREET       = "street";

    /**
     * @apiNote : String on Firestore
     */
    public final static String CITY         = "city";

    /**
     * @apiNote : String on Firestore
     */
    public final static String ADVERTISER_ID   = "advertiserId";

    /**
     * @apiNote : String on Firestore
     */
    public final static String DESCRIPTION  = "description";

    /**
     * @apiNote : boolean on Firestore
     */
    public final static String VR_TOUR      = "hasVTour";

    /**
     * @apiNote : long on Firestore
     */
    public final static String PRICE_PERIOD = "PricePeriod";

}
