package be.simonraes.dotadata.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.activity.MatchActivity;
import be.simonraes.dotadata.adapter.RecentGamesAdapter;
import be.simonraes.dotadata.async.DetailMatchLoader;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.historyloading.DatabaseMatchLoader;
import be.simonraes.dotadata.historyloading.HistoryLoader;
import be.simonraes.dotadata.util.AppPreferences;
import be.simonraes.dotadata.util.InternetCheck;
import be.simonraes.dotadata.util.OrientationHelper;

import java.util.ArrayList;

/**
 * Created by Simon on 18/02/14.
 * Displays list of played games for the active user.
 */
public class RecentGamesFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener, DatabaseMatchLoader.ASyncResponseDatabase, HistoryLoader.ASyncResponseHistoryLoader, DetailMatchLoader.DetailMatchLoaderDelegate {

    private ListView lvRecentGames;
    private ProgressBar pbRecentGames;

    private RecentGamesAdapter listAdapter;

    private ArrayList<DetailMatchLite> matches = new ArrayList<DetailMatchLite>();

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
        View view = inflater.inflate(R.layout.list_layout, container, false);
        pbRecentGames = (ProgressBar) view.findViewById(R.id.pbListLayout);
        lvRecentGames = (ListView) view.findViewById(R.id.listView);

        if (getActivity().getActionBar() != null) {
            getActivity().setTitle("Recent games");
            getActivity().getActionBar().setSubtitle(null);
        }

        //update active drawer item
        if (getActivity() instanceof DrawerController) {
            ((DrawerController) getActivity()).setActiveDrawerItem(1);

            //make actionbar show drawer icon
            ((DrawerController) getActivity()).getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
            //update the actionbar to show the up carat
            if (getActivity() != null && getActivity().getActionBar() != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }


        if (AppPreferences.getActiveAccountId(getActivity()).equals("")) {

            new AlertDialog.Builder(getActivity())
                    .setTitle("Welcome!")
                    .setCancelable(false)
                    .setMessage("Your Dota 2 account ID is required before your matches can be downloaded. Hit 'Ok' to get started.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getFragmentManager().beginTransaction().replace(R.id.content_frame, new AddUserFragment()).commit();
                        }
                    }).show();
        } else {
            setHasOptionsMenu(true);

            if (savedInstanceState == null) {
                if (matches.size() == 0) {
                    //this is the first time opening this screen, get a fresh set of matches from the database
                    loadMatchesFromDatabase();
                }
            } else {
                // Load previous list from savedState
                matches = savedInstanceState.getParcelableArrayList("matches");
            }

            footerView = inflater.inflate(R.layout.historygames_footer, null);
            lvRecentGames.addFooterView(footerView, null, false);

            listAdapter = new RecentGamesAdapter(getActivity(), matches);
            lvRecentGames.setAdapter(listAdapter);

            lvRecentGames.setOnItemClickListener(this);
            lvRecentGames.setOnScrollListener(this);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("matches", matches);
    }

    /**
     * Setup actionbar
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_menu, menu);

        MenuItem btnFavourite = menu.findItem(R.id.btnFavourite);
        if (btnFavourite != null) {
            btnFavourite.setVisible(false);
        }
        MenuItem btnNote = menu.findItem(R.id.btnNote);
        if (btnNote != null) {
            btnNote.setVisible(false);
        }
    }

    //Actionbar button clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnRefresh:
                // Only start download if it isn't already downloading
                if (InternetCheck.isOnline(getActivity())) {
                    OrientationHelper.lockOrientation(getActivity());
                    HistoryLoader loader = new HistoryLoader(getActivity(), this, AppPreferences.getActiveAccountId(getActivity()));
                    loader.updateHistory();
                } else {
                    Toast.makeText(getActivity(), "You are not connected to the internet.", Toast.LENGTH_SHORT).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadMatchesFromDatabase() {

        DatabaseMatchLoader loader = new DatabaseMatchLoader(this, getActivity());

        if (matches.size() < 1) {
            // List doesn't contain any matches yet, get 50 most recent
            pbRecentGames.setVisibility(View.VISIBLE);
            lvRecentGames.setVisibility(View.GONE);

            loader.execute();
        } else {
            // List already contains matches, get the next 50
            loader.execute(matches.get(matches.size() - 1).getMatch_id());
        }
    }

    //Detect click on match in list
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DetailMatchLite matchLite = (DetailMatchLite) lvRecentGames.getAdapter().getItem(position);

        getActivity().setProgressBarIndeterminateVisibility(true);

        DetailMatchLoader loader = new DetailMatchLoader(getActivity(), this);
        loader.execute(matchLite.getMatch_id());
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            //start loading next set of matches at 5 rows from the last
            if (lvRecentGames.getLastVisiblePosition() >= lvRecentGames.getCount() - 5) {
                //load more list items:
                loadMatchesFromDatabase();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * Received matches from database
     */
    @Override
    public void processFinish(ArrayList<DetailMatchLite> detailMatches) {

        if (detailMatches.size() == 0) {
            if (matches.size() == 0) {
                //database didn't contain any matches
                Toast.makeText(getActivity(), "No games found.", Toast.LENGTH_SHORT).show();
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


    //historyloader is ready
    @Override
    public void processFinish(boolean foundGames) {
        if (foundGames) {
            matches.clear();
            loadMatchesFromDatabase();
        }
        OrientationHelper.unlockOrientation(getActivity());
    }


    /**
     * Receiving full match details from the database.
     */
    @Override
    public void loadDone(DetailMatch match) {
        getActivity().setProgressBarIndeterminateVisibility(false);

        Intent intent = new Intent(getActivity(), MatchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("match", match);
        startActivity(intent);
    }
}
