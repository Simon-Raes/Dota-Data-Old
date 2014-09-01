package be.simonraes.dotadata.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;
import be.simonraes.dotadata.R;


/**
 * Currently unused.
 * Created by Simon on 7/02/14.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        getActivity().setTitle("Settings");

        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem btnRefresh = menu.findItem(R.id.btnRefresh);
        if (btnRefresh != null) {
            btnRefresh.setVisible(false);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
//        updateSummaries();
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
//            updateSummaries();
        }
    }

    //not used
    private void updateSummaries() {
        Preference prefAccountID = findPreference("be.simonraes.dotadata.accountid");
        prefAccountID.setSummary(getPreferenceScreen().getSharedPreferences().getString("be.simonraes.dotadata.accountid", "AccountID"));
    }

}
