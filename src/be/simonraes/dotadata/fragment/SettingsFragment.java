package be.simonraes.dotadata.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;
import be.simonraes.dotadata.R;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * Created by Simon on 7/02/14.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getActionBar().setTitle("Settings");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

//        Preference prefAccountID = findPreference("be.simonraes.dotadata.accountid");
//        prefAccountID.setSummary(sharedPref.getString("be.simonraes.dotadata.accountid", "AccountID"));

        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals("be.simonraes.dotadata.accountid")) {
            Preference prefAccountID = findPreference("be.simonraes.dotadata.accountid");
            prefAccountID.setSummary(sharedPreferences.getString("be.simonraes.dotadata.accountid", "No AccountID set"));
        }
    }

}
