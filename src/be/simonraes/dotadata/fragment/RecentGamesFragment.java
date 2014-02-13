package be.simonraes.dotadata.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.adapter.HistoryGamesAdapter;
import be.simonraes.dotadata.delegates.ASyncResponseDetail;
import be.simonraes.dotadata.delegates.ASyncResponseHistory;
import be.simonraes.dotadata.detailmatch.DetailContainer;
import be.simonraes.dotadata.historymatch.HistoryContainer;
import be.simonraes.dotadata.historymatch.HistoryMatch;
import be.simonraes.dotadata.parser.DetailMatchParser;
import be.simonraes.dotadata.parser.HistoryMatchParser;
import be.simonraes.dotadata.util.Preferencess;

import java.util.ArrayList;

/**
 * Created by Simon on 29/01/14.
 */
public class RecentGamesFragment extends Fragment implements ASyncResponseHistory, ASyncResponseDetail, AdapterView.OnItemClickListener {

    private ListView lvRecentGames;
    private ArrayList<HistoryMatch> matches = new ArrayList<HistoryMatch>();
    private ProgressBar pbRecentGames;

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
                    .setTitle("AccountID needed")
                    .setView(layDialogContent)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //check if account id is integer, set it as accountid preference
                            try {
                                Integer.parseInt(txtDialogAccountID.getText().toString());
                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("be.simonraes.dotadata.accountid", txtDialogAccountID.getText().toString());
                                editor.commit();
                                loadMatches();

                            } catch (Exception e) {
                                //not int - todo:show message or reopen dialog
                            }

                        }
                    })
                    .setNeutralButton("Help", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            //open screen with info on how to find account id
                            FragmentManager fm = getFragmentManager();
                            Fragment fragment = new AccountIDHelpFragment();
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.replace(R.id.content_frame, fragment);
                            transaction.addToBackStack(null).commit();
                        }
                    })
                    .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        } else {
            //force onCreateOptionsMenu to be called
            setHasOptionsMenu(true);

            if (matches.size() == 0) {
                loadMatches();
            }

            lvRecentGames.setAdapter(new HistoryGamesAdapter(getActivity(), matches));
            lvRecentGames.setOnItemClickListener(this);
        }


        return view;
    }

    //finished getting 100 recent games
    @Override
    public void processFinish(HistoryContainer result) {
        pbRecentGames.setVisibility(View.GONE);
        lvRecentGames.setVisibility(View.VISIBLE);

        matches = result.getRecentGames().getMatches();
        lvRecentGames.setAdapter(new HistoryGamesAdapter(getActivity(), matches));
        ((HistoryGamesAdapter) lvRecentGames.getAdapter()).notifyDataSetChanged();
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
        Bundle bundle = new Bundle();
        bundle.putSerializable("be/simonraes/dotadata/detailmatch", result.getDetailMatch());
        fragment.setArguments(bundle);

        transaction.addToBackStack(null).commit();
    }


    //Detect click on match in list
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        DetailMatchParser parser = new DetailMatchParser(this);
        HistoryMatch match = (HistoryMatch) lvRecentGames.getAdapter().getItem(position);
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
        switch (item.getItemId()) {
            case R.id.btnRefresh:
                loadMatches();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadMatches() {
        pbRecentGames.setVisibility(View.VISIBLE);
        lvRecentGames.setVisibility(View.GONE);

        String prefAccountID = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("be.simonraes.dotadata.accountid", "");

        HistoryMatchParser parser = new HistoryMatchParser(this);
        parser.execute(prefAccountID);
    }

    //Set action bar buttons
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recent_games_menu, menu);
    }
}