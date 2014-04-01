package be.simonraes.dotadata.util;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Simon on 12/02/14.
 */
public class AppPreferences {

    public static String getAccountID(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("be.simonraes.dotadata.accountid", "");
    }

    public static void putAccountID(Context context, String accountID) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("be.simonraes.dotadata.accountid", accountID).commit();
    }
}
