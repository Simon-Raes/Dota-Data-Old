package be.simonraes.dotadata.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Simon on 12/02/14.
 */
public class Preferencess {

    public static String getAccountID(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("be.simonraes.dotadata.accountid", "");
    }
}
