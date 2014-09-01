package be.simonraes.dotadata.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.MatchActivity;
import be.simonraes.dotadata.adapter.RecentGamesAdapter;
import be.simonraes.dotadata.async.StatsMatchesLoader;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.historyloading.DatabaseMatchLoader;
import be.simonraes.dotadata.util.AppPreferences;
import be.simonraes.dotadata.util.OrientationHelper;

import java.util.ArrayList;

/**
 * Created by Simon Raes on 15/04/2014.
 */
public class StatsMatchesFragment extends Fragment implements AdapterView.OnItemClickListener, DatabaseMatchLoader.ASyncResponseDatabase, StatsMatchesLoader.ASyncResponseStatsLoader {

    private ListView lvRecentGames;
    private RecentGamesAdapter listAdapter;
    private ArrayList<DetailMatchLite> matches = new ArrayList<DetailMatchLite>();
    private ProgressBar pbRecentGames;
    private View footerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //onCreate() will still be called when fragment is in the background, save matches here for when user returns to this screen
        if (savedInstanceState != null) {
            matches = savedInstanceState.getParcelableArrayList("matches");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matches_list_layout, container, false);
        pbRecentGames = (ProgressBar) view.findViewById(R.id.pbRecentGames);
        lvRecentGames = (ListView) view.findViewById(R.id.lvRecentGames);

        //setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            //load previous list from savedState
            matches = savedInstanceState.getParcelableArrayList("matches");
        } else {
            if (matches.size() == 0) {
//                loadMatchesFromDatabase();
            }
        }
        lvRecentGames.setOnItemClickListener(this);

        listAdapter = new RecentGamesAdapter(getActivity(), matches);
        lvRecentGames.setAdapter(listAdapter);

        //    wentToDetails = false;


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        System.out.println("saved to state ");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("matches", matches);
    }

    /**
     * Fill this screen with data of all stats matches
     */
    private void loadMatchesFromDatabase() {
        //lock orientation during loading
        OrientationHelper.lockOrientation(getActivity());


        StatsMatchesLoader sml = new StatsMatchesLoader(this, getActivity());
        sml.execute("0", "0");
    }


    public void setMatches(ArrayList<DetailMatchLite> matchesIncoming) {
        System.out.println("got it");
        matches = matchesIncoming;
        if (listAdapter == null) {
            listAdapter = new RecentGamesAdapter(getActivity(), matches);
            if (lvRecentGames != null) {
                lvRecentGames.setAdapter(listAdapter);
            }
        } else {
            //listAdapter.notifyDataSetChanged();
            listAdapter = new RecentGamesAdapter(getActivity(), matches);
            if (lvRecentGames != null) {
                lvRecentGames.setAdapter(listAdapter);
            }
        }
    }

    //Detect click on match in list
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //launch this as a new activity so the state in the stats screen is easier to preserve

        DetailMatchLite matchLite = (DetailMatchLite) lvRecentGames.getAdapter().getItem(position);

        MatchesDataSource mds = new MatchesDataSource(getActivity(), AppPreferences.getAccountID(getActivity()));
        DetailMatch match = mds.getMatchByID(matchLite.getMatch_id());

        Intent intent = new Intent(getActivity(), MatchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("match", match);
        startActivity(intent);
    }

    @Override
    public void processFinish(ArrayList<DetailMatchLite> detailMatches) {
        if (detailMatches.size() == 0) {
            if (matches.size() == 0) {
                //database didn't contain any matches
            } else {
                //reached last game, remove list loading footer
                lvRecentGames.removeFooterView(footerView);
            }
        }

        matches.addAll(detailMatches);
        listAdapter.notifyDataSetChanged();

        pbRecentGames.setVisibility(View.GONE);
        lvRecentGames.setVisibility(View.VISIBLE);
    }
}
