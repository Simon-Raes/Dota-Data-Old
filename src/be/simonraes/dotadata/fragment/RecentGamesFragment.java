package be.simonraes.dotadata.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.adapter.RecentGamesAdapter;
import be.simonraes.dotadata.delegates.ASyncResponseDatabase;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.historyloading.DatabaseMatchLoader;
import be.simonraes.dotadata.historyloading.HistoryLoader;
import be.simonraes.dotadata.util.Preferencess;

import java.util.ArrayList;

/**
 * Created by Simon on 18/02/14.
 */
public class RecentGamesFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener, ASyncResponseDatabase {

    private ListView lvRecentGames;
    private RecentGamesAdapter listAdapter;
    private ArrayList<DetailMatch> matches = new ArrayList<DetailMatch>();
    private ProgressBar pbRecentGames;
    private View footerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matches_list_layout, container, false);
        pbRecentGames = (ProgressBar) view.findViewById(R.id.pbRecentGames);
        lvRecentGames = (ListView) view.findViewById(R.id.lvRecentGames);

        getActivity().getActionBar().setTitle("Recent games");

        if (Preferencess.getAccountID(getActivity()).equals("")) {

            final LinearLayout layDialogContent = (LinearLayout) inflater.inflate(R.layout.dialog_accountid_content, null);
            final EditText txtDialogAccountID = (EditText) layDialogContent.findViewById(R.id.txtDialogAccountID);
            new AlertDialog.Builder(getActivity())
                    .setTitle("Welcome!")
                    .setCancelable(false)
                    .setMessage("Your Dota 2 account ID is required before your matches can be downloaded. Hit 'Ok' to get started.")
                    .setIcon(R.drawable.dd_sm)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            getFragmentManager().beginTransaction().replace(R.id.content_frame, new AccountIDHelpFragment()).commit();
                        }
                    }).show();
        } else {
            //force onCreateOptionsMenu to be called
            setHasOptionsMenu(true);

            if (matches.size() == 0) {
                loadMatches();
            }

            listAdapter = new RecentGamesAdapter(getActivity(), matches);
            lvRecentGames.setAdapter(listAdapter);
            footerView = inflater.inflate(R.layout.historygames_footer, null);
            lvRecentGames.addFooterView(footerView);
            lvRecentGames.setOnItemClickListener(this);
            lvRecentGames.setOnScrollListener(this);
        }

        return view;
    }

    //Detect click on match in list
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        DetailMatch match = (DetailMatch) lvRecentGames.getAdapter().getItem(position);

        Fragment fragment = new MatchDetailFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);

        getActivity().setProgressBarIndeterminateVisibility(false);

        //send object to fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("be.simonraes.dotadata.detailmatch", match);
        fragment.setArguments(bundle);

        transaction.addToBackStack(null).commit();
    }


    //ActionBar button clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnRefresh:
                HistoryLoader loader = new HistoryLoader(getActivity());
                loader.updateHistory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadMatches() {
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
            if (lvRecentGames.getLastVisiblePosition() >= lvRecentGames.getCount() - 5) { //getal duidt aan hoe ver van het einde de loading al zal starten
                //load more list items:
                loadMatches();
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
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem btnRefresh = menu.findItem(R.id.btnRefresh);
        btnRefresh.setVisible(true);
    }


}
