package be.simonraes.dotadata.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.delegates.ASyncResponseHistoryLoader;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.historyloading.HistoryLoader;

import java.util.ArrayList;

/**
 * Created by Simon on 14/02/14.
 */
public class StatsFragment extends Fragment implements View.OnClickListener, ASyncResponseHistoryLoader {

    private Button btnUpdateMatches, btnClearDatabase;
    private Button btnNumberOfRecords, btnDeleteLatestMatch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stats_layout, null);

        getActivity().getActionBar().setTitle("Statistics");

        btnUpdateMatches = (Button) view.findViewById(R.id.btnUpdateMatches);
        btnUpdateMatches.setOnClickListener(this);

        btnClearDatabase = (Button) view.findViewById(R.id.btnClearDatabase);
        btnClearDatabase.setOnClickListener(this);

        btnNumberOfRecords = (Button) view.findViewById(R.id.btnNumberOfRecords);
        btnNumberOfRecords.setOnClickListener(this);

        btnDeleteLatestMatch = (Button) view.findViewById(R.id.btnDeleteLatestMatch);
        btnDeleteLatestMatch.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdateMatches:
                HistoryLoader loader = new HistoryLoader(this, getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("be.simonraes.dotadata.accountid", ""));
                loader.updateHistory();
                break;
            case R.id.btnClearDatabase:
                getActivity().deleteDatabase("be.simonraes.dotadata.db");
                break;
            case R.id.btnNumberOfRecords:
                MatchesDataSource mds = new MatchesDataSource(getActivity());
                Toast.makeText(getActivity(), Integer.toString(mds.getNumberOfRecords()), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnDeleteLatestMatch:
                MatchesDataSource mds2 = new MatchesDataSource(getActivity());
                mds2.deleteLatestMatch();
                break;
            default:
                break;
        }
    }

    @Override
    public void processFinish(ArrayList<DetailMatch> result) {
        //receives arraylist with full history
    }
}
