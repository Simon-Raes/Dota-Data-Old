package be.simonraes.dotadata.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.adapter.RecentGamesAdapter;
import be.simonraes.dotadata.delegates.ASyncResponseDatabase;
import be.simonraes.dotadata.delegates.ASyncResponseHistoryLoader;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.historyloading.DatabaseMatchLoader;
import be.simonraes.dotadata.historyloading.HistoryLoader;
import be.simonraes.dotadata.util.InternetCheck;
import be.simonraes.dotadata.util.Preferencess;

import java.util.ArrayList;

/**
 * Created by Simon on 18/02/14.
 */
public class RecentGamesFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener, ASyncResponseDatabase, ASyncResponseHistoryLoader {

    private ListView lvRecentGames;
    private RecentGamesAdapter listAdapter;
    private ArrayList<DetailMatch> matches = new ArrayList<DetailMatch>();
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

        getActivity().setTitle("Recent games");

        if (Preferencess.getAccountID(getActivity()).equals("")) {

            new AlertDialog.Builder(getActivity())
                    .setTitle("Welcome!")
                    .setCancelable(false)
                    .setMessage("Your Dota 2 account ID is required before your matches can be downloaded. Hit 'Ok' to get started.")
                    .setIcon(R.drawable.dotadata_sm)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            getFragmentManager().beginTransaction().replace(R.id.content_frame, new AddUserFragment()).commit();
                        }
                    }).show();
        } else {
            //force onCreateOptionsMenu to be called
            setHasOptionsMenu(true);

            if (savedInstanceState == null) {
                if (matches.size() == 0) {
                    //this is the first time opening this screen, get a fresh set of matches from the database

                    if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("be.simonraes.dotadata.downloadinprogress", false)) {
                        Toast.makeText(getActivity(), "Download still in progress.", Toast.LENGTH_SHORT).show();
                    } else {
                        loadMatchesFromDatabase();
                    }
                }
            } else {
                //list state has been saved before, load that state
                matches = savedInstanceState.getParcelableArrayList("matches");
            }

            footerView = inflater.inflate(R.layout.historygames_footer, null);

//            if (matches.size() > 0) {
            lvRecentGames.addFooterView(footerView, null, false);
//            }

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

    //Detect click on match in list
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        DetailMatch match = (DetailMatch) lvRecentGames.getAdapter().getItem(position);

        Fragment fragment = new MatchDetailFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);

        //hacky way to set UP arrow in actionbar of matchdetails screen
        ((DrawerController) getActivity()).getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);


        //send object to fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("be.simonraes.dotadata.detailmatch", match);
        fragment.setArguments(bundle);

        transaction.addToBackStack(null).commit();
    }


    //ActionBar button clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnRefresh:
                //only start download if it isn't already downloading
                if (InternetCheck.isOnline(getActivity())) {
                    HistoryLoader loader = new HistoryLoader(getActivity(), this);
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
            //list doesn't contain any matches yet, get 50 most recent
            pbRecentGames.setVisibility(View.VISIBLE);
            lvRecentGames.setVisibility(View.GONE);
            loader.execute();
        } else {
            //list already contains matches, get the next 50
            loader.execute(matches.get(matches.size() - 1).getMatch_id());
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            if (lvRecentGames.getLastVisiblePosition() >= lvRecentGames.getCount() - 5) {
                //load more list items:
                loadMatchesFromDatabase();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    //received matches from database
    @Override
    public void processFinish(ArrayList<DetailMatch> detailMatches) {

        if (detailMatches.size() == 0) {
            if (matches.size() == 0) {
                //database didn't contain any matches
                Toast.makeText(getActivity(), "No games found", Toast.LENGTH_SHORT).show();
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


    @Override
    public void processFinish() {
        matches.clear();
        loadMatchesFromDatabase();
    }
}
