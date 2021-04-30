package ch.epfl.sdp.appart.utils;

/**
 * USE WITH intent.putExtra( "ID of the put request" , " provided information " )
 *
 * Holder for messages between activities.
 * Example: called activities may behave differently depending on the callee activity.
 *
 */
public final class ActivityCommunicationLayout {

    private ActivityCommunicationLayout(){}


    /*  IDs for put requests */

    public static final String PROVIDING_ACTIVITY_NAME = "activity";

    public static final String PROVIDING_USER_ID = "userID";

    public static final String PROVIDING_CARD_ID = "cardID";

    public static final String PROVIDING_AD_ID = "adID";

    public static final String PROVIDING_EMAIL = "email";

    public static final String PROVIDING_PASSWORD = "password";


    /*  IDs for provided information */

    public static final String USER_PROFILE_ACTIVITY = "UserProfileActivity";

    public static final String AD_CREATION_ACTIVITY = "AdCreationActivity";

}
