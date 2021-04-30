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


    /*  IDs for provided information */

    public static final String USER_PROFILE_ACTIVITY = "UserProfileActivity";

    public static final String AD_CREATION_ACTIVITY = "AdCreationActivity";

}
