package be.simonraes.dotadata.fragment;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import be.simonraes.dotadata.adapter.HistoryGamesAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.detailmatch.DetailContainer;
import be.simonraes.dotadata.historymatch.HistoryContainer;
import be.simonraes.dotadata.historymatch.HistoryMatch;
import be.simonraes.dotadata.interfaces.ASyncResponseDetail;
import be.simonraes.dotadata.interfaces.ASyncResponseHistory;
import be.simonraes.dotadata.parser.DetailMatchParser;
import be.simonraes.dotadata.parser.HistoryMatchParser;

/**
 * Created by Simon on 29/01/14.
 */
public class RecentGamesFragment extends Fragment implements ASyncResponseHistory, ASyncResponseDetail, AdapterView.OnItemClickListener {

    private ListView listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recentgames_layout, container, false);

        HistoryContainer resultContainer = (HistoryContainer) getArguments().getSerializable("container");

        getActivity().getActionBar().setTitle("Recent games");

        //force onCreateOptionsMenu to be called
        setHasOptionsMenu(true);

        listview = (ListView) view.findViewById(R.id.lvRecentGames);
        listview.setAdapter(new HistoryGamesAdapter(getActivity(),resultContainer.getRecentGames().getMatches()));
        listview.setOnItemClickListener(this);

        return view;
    }

    //finished getting 100 recent games
    @Override
    public void processFinish(HistoryContainer result) {
        listview.setAdapter(new HistoryGamesAdapter(getActivity(), result.getRecentGames().getMatches()));
        ((HistoryGamesAdapter)listview.getAdapter()).notifyDataSetChanged();
    }

    //finished getting details of 1 selected match
    @Override
    public void processFinish(DetailContainer result) {
        Fragment fragment = new MatchDetailFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);

        getActivity().setProgressBarIndeterminateVisibility(false);

        //send object to be.simonraes.dotadata.fragment
        Bundle bundle=new Bundle();
        bundle.putSerializable("be/simonraes/dotadata/detailmatch", result.getDetailMatch());
        fragment.setArguments(bundle);

        transaction.addToBackStack(null).commit();
    }


    //Detect click on match in list
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        DetailMatchParser parser = new DetailMatchParser(this);
        HistoryMatch match = (HistoryMatch) listview.getAdapter().getItem(position);
        parser.execute(match.getMatch_id());

        getActivity().setProgressBarIndeterminateVisibility(true);

        //met internet/webapi-status check, maar mag niet op UI thread
//        if(InternetCheck.serviceAvailable()){
//            be.simonraes.dotadata.parser.execute(match.getMatch_id());
//        } else {
//            Toast.makeText(getActivity(),InternetCheck.getErrorCode(),Toast.LENGTH_LONG).show();
//        }
    }


    //ActionBar button clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.btnRefresh:
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                String prefAccountID = sharedPref.getString("be.simonraes.dotadata.accountid", "");

                HistoryMatchParser parser = new HistoryMatchParser(this);
                parser.execute(prefAccountID);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Set action bar buttons
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recent_games_menu,menu);
    }
}