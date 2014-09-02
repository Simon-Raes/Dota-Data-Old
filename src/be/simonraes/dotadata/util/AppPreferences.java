package be.simonraes.dotadata.util;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Class to get and set shared preferences for the app.
 * Created by Simon on 12/02/14.
 */
public class AppPreferences {

    public static String getActiveAccountId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("be.simonraes.dotadata.accountid", "");
    }

    public static void setActiveAccountId(Context context, String accountID) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("be.simonraes.dotadata.accountid", accountID).commit();
    }
}
