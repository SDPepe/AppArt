package ch.epfl.sdp.appart.database.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesHelper {

    private static final String EMAIL = "username";
    private static final String PASSWORD = "password";

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void saveUserForAutoLogin(Context context,String email, String password){
        Editor ed = getPreferences(context).edit();
        ed.putString(EMAIL, email);
        ed.putString(PASSWORD, password);
        ed.apply();
    }

    public static String getSavedEmail(Context context) {
        return getPreferences(context).getString(EMAIL, "");
    }

    public static String getSavedPassword(Context context) {
        return getPreferences(context).getString(PASSWORD, "");
    }

    public static void clearSavedUserForAutoLogin(Context context) {
        Editor ed = getPreferences(context).edit();
        ed.clear();
        ed.apply();
    }
}
