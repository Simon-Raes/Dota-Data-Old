package fragment;

import adapter.LiveLeagueGamesAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import be.simonraes.dotadata.R;
import liveleaguegame.LiveLeagueContainer;
import liveleaguegame.LiveLeagueMatch;

/**
 * Created by Simon on 4/02/14.
 */
public class LiveLeagueGamesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //re-use listview fragment
        View view = inflater.inflate(R.layout.recentgames_layout, container, false);

        LiveLeagueContainer resultContainer = (LiveLeagueContainer) getArguments().getSerializable("container");

        getActivity().getActionBar().setTitle("Live league games");

        //force onCreateOptionsMenu to be called
        setHasOptionsMenu(true);

        listview = (ListView) view.findViewById(R.id.lvRecentGames);
        listview.setAdapter(new LiveLeagueGamesAdapter(getActivity(), resultContainer.getLiveLeagueMatches().getLiveLeagueMatches()));
        listview.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = new LiveLeagueGameFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);

        LiveLeagueMatch match = (LiveLeagueMatch) listview.getAdapter().getItem(position);

        //send object to fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("liveLeagueMatch", match);
        fragment.setArguments(bundle);

        transaction.addToBackStack(null).commit();
    }
}
