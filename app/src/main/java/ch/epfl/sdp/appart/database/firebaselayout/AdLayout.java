package ch.epfl.sdp.appart.database.firebaselayout;

/**
 * Un-instantiable Holder for the layout of the fields for an Ad on Firestore.
 */
public final class AdLayout {


    //WARNING : ref -> id

    private AdLayout() {}

    /**
     * absolute path to the images collection of the ad in Firestore
     */
    public final static String IMAGES_DIRECTORY = "ads";

    /**
     * absolute path to the pictures collection of the ad in Firestore
     */
    public final static String PICTURES_DIRECTORY = "pictures_ids"; //photosRefs

    /**
     * absolute path to the panorama collection of the ad in Firestore
     */
    public final static String PANORAMA_DIRECTORY = "panoramas_ids"; //photosRefs

    /**
     * absolute path to the panorama collection of the ad in Firestore
     */
    public final static String PICTURE_ELEMENT_ID_FIELD = "id"; //photosRefs

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

    /*
        The following Strings are not stored as such on Firestore but helps with the local databse
     */

    public static final String ID = "ID";
    public static final String PHOTO_REFS = "PHOTO_REFS";
    public static final String CARD_ID = "CARD_ID";
    public static final String USER_ID = "USER_ID";
    public static final String PANORAMA_REFS = "PANORAMA_REFS";

}
