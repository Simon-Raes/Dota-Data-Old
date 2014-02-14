package be.simonraes.dotadata.fragment;

import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.delegates.ASyncResponseHistoryLoader;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.historyloading.HistoryLoader;

import java.util.ArrayList;

/**
 * Created by Simon on 14/02/14.
 */
public class StatsFragment extends Fragment implements View.OnClickListener, ASyncResponseHistoryLoader {

    private Button btnUpdateMatches;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stats_layout, null);

        btnUpdateMatches = (Button) view.findViewById(R.id.btnUpdateMatches);
        btnUpdateMatches.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdateMatches:
                HistoryLoader loader = new HistoryLoader(this, getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("be.simonraes.dotadata.accountid", ""));

                //todo: start notification that will be updated during historydownloading


                loader.getAllMatches();
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
