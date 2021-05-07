package ch.epfl.sdp.appart.database.firebaselayout;

/**
 * Un-instantiable Holder for the layout of the fields for an Ad on Firestore.
 */
public final class AdLayout {


    //WARNING : ref -> id

    private AdLayout() {}

    /**
     * absolute path to the pictures directory of the ad in Firestore
     */
    public final static String PICTURES_DIRECTORY = "pictures_ids"; //photosRefs

    /**
     * @apiNote : String on Firestore
     */
    public final static String TITLE = "title";

    /**
     * @apiNote : long on Firestore
     */
    public final static String PRICE = "price";

    /**
     * @apiNote : String on Firestore
     */
    public final static String STREET = "street";

    /**
     * @apiNote : String on Firestore
     */
    public final static String CITY = "city";

    /**
     * @apiNote : String on Firestore
     */
    public final static String ADVERTISER_ID = "advertiser_id";

    /**
     * @apiNote : String on Firestore
     */
    public final static String DESCRIPTION = "description";

    /**
     * @apiNote : boolean on Firestore
     */
    public final static String VR_TOUR = "has_vr_tour"; //hasVTour

    /**
     * @apiNote : long on Firestore
     */
    public final static String PRICE_PERIOD = "price_period"; //price_period

}
