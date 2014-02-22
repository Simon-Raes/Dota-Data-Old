package be.simonraes.dotadata.fragment;

import android.view.*;
import android.widget.ProgressBar;
import be.simonraes.dotadata.adapter.LiveLeagueGamesAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.delegates.ASyncResponseLiveLeague;
import be.simonraes.dotadata.liveleaguegame.LiveLeagueContainer;
import be.simonraes.dotadata.liveleaguegame.LiveLeagueMatch;
import be.simonraes.dotadata.parser.LiveLeagueMatchParser;

import java.util.ArrayList;

/**
 * Created by Simon on 4/02/14.
 */
public class LiveLeagueGamesFragment extends Fragment implements AdapterView.OnItemClickListener, ASyncResponseLiveLeague {

    private ListView lvRecentGames;
    private ProgressBar pbRecentGames;
    private ArrayList<LiveLeagueMatch> matches = new ArrayList<LiveLeagueMatch>();


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

        getActivity().getActionBar().setTitle("Live league games");

        //force onCreateOptionsMenu to be called
        setHasOptionsMenu(true);


        if (savedInstanceState == null) {
            if (matches.size() == 0) {
                //this is the first time opening this screen, get a fresh set of matches from the database
                loadMatches();
            }
        } else {
            //list state has been saved before, load that state
            matches = savedInstanceState.getParcelableArrayList("matches");
        }

        lvRecentGames.setAdapter(new LiveLeagueGamesAdapter(getActivity(), matches));
        lvRecentGames.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("matches", matches);

    }

    private void loadMatches() {
        pbRecentGames.setVisibility(View.VISIBLE);
        lvRecentGames.setVisibility(View.GONE);

        LiveLeagueMatchParser parser = new LiveLeagueMatchParser(this);
        parser.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = new LiveLeagueGameFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);

        LiveLeagueMatch match = (LiveLeagueMatch) lvRecentGames.getAdapter().getItem(position);

        //send object to fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("liveLeagueMatch", match);
        fragment.setArguments(bundle);

        transaction.addToBackStack(null).commit();
    }

    @Override
    public void processFinish(LiveLeagueContainer result) {
        pbRecentGames.setVisibility(View.GONE);
        lvRecentGames.setVisibility(View.VISIBLE);

        matches = result.getLiveLeagueMatches().getLiveLeagueMatches();
        lvRecentGames.setAdapter(new LiveLeagueGamesAdapter(getActivity(), matches));
        ((LiveLeagueGamesAdapter) lvRecentGames.getAdapter()).notifyDataSetChanged();
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.refresh_menu, menu);
//    }

    //ActionBar button clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnRefresh:
                loadMatches();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
