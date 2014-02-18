package be.simonraes.dotadata.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.adapter.DetailGamesAdapter;
import be.simonraes.dotadata.adapter.HistoryGamesAdapter;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.delegates.ASyncResponseDetail;
import be.simonraes.dotadata.delegates.ASyncResponseHistory;
import be.simonraes.dotadata.detailmatch.DetailContainer;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.historymatch.HistoryContainer;
import be.simonraes.dotadata.historymatch.HistoryMatch;
import be.simonraes.dotadata.parser.DetailMatchParser;
import be.simonraes.dotadata.parser.HistoryMatchParser;
import be.simonraes.dotadata.util.Preferencess;

import java.util.ArrayList;

/**
 * Created by Simon on 18/02/14.
 */
public class DatabaseGamesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView lvRecentGames;
    private ArrayList<DetailMatch> matches = new ArrayList<DetailMatch>();
    private ProgressBar pbRecentGames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matches_list_layout, container, false);
        pbRecentGames = (ProgressBar) view.findViewById(R.id.pbRecentGames);
        lvRecentGames = (ListView) view.findViewById(R.id.lvRecentGames);

        getActivity().getActionBar().setTitle("Recent (database) games");

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

            lvRecentGames.setAdapter(new DetailGamesAdapter(getActivity(), matches));
            lvRecentGames.setOnItemClickListener(this);
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
                loadMatches();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //todo: needs to be done in a background thread so it doesn't lock up the UI thread (progressbar isn't even shown now)
    private void loadMatches() {
        pbRecentGames.setVisibility(View.VISIBLE);
        lvRecentGames.setVisibility(View.GONE);

        MatchesDataSource mds = new MatchesDataSource(getActivity());
        matches = mds.getAllMatches();

        lvRecentGames.setAdapter(new DetailGamesAdapter(getActivity(), matches));
        ((DetailGamesAdapter) lvRecentGames.getAdapter()).notifyDataSetChanged();

        pbRecentGames.setVisibility(View.GONE);
        lvRecentGames.setVisibility(View.VISIBLE);

    }

    //Set action bar buttons
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.refresh_menu, menu);
//    }
}
