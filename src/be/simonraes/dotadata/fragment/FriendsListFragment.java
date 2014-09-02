package be.simonraes.dotadata.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.adapter.FriendsListAdapter;
import be.simonraes.dotadata.comparator.PlayerSummaryNameComparator;
import be.simonraes.dotadata.database.UsersDataSource;
import be.simonraes.dotadata.friendslist.Friend;
import be.simonraes.dotadata.friendslist.FriendsListContainer;
import be.simonraes.dotadata.parser.FriendsListParser;
import be.simonraes.dotadata.parser.PlayerSummaryParser;
import be.simonraes.dotadata.playersummary.PlayerSummary;
import be.simonraes.dotadata.playersummary.PlayerSummaryContainer;
import be.simonraes.dotadata.user.User;
import be.simonraes.dotadata.util.AppPreferences;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.OrientationHelper;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Simon Raes on 2/09/2014.
 */
public class FriendsListFragment extends Fragment implements FriendsListParser.ASyncResponseFriendsList, PlayerSummaryParser.ASyncResponsePlayerSummary {

    private int numberOfFriends, friendsDownloaded;
    private ArrayList<PlayerSummary> friendSummaries = new ArrayList<PlayerSummary>();
    private ListView listView;
    private FriendsListAdapter adapter;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            friendSummaries = savedInstanceState.getParcelableArrayList("friendSummaries");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, null);

        UsersDataSource uds = new UsersDataSource(getActivity());
        User activeUser = uds.getUserByID(AppPreferences.getActiveAccountId(getActivity()));
        if(getActivity().getActionBar()!=null){
            getActivity().getActionBar().setTitle("Add user");
            getActivity().getActionBar().setSubtitle(activeUser.getName()+"'s friends");
        }

        adapter = new FriendsListAdapter(getActivity(), friendSummaries);
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        progressBar = (ProgressBar) view.findViewById(R.id.pbListLayout);

        if(friendSummaries==null || friendSummaries.size()<=0){
            getActivity().setProgressBarIndeterminate(true);
            OrientationHelper.lockOrientation(getActivity());
            startFriendsListDownload();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Loading friendslist");

            progressDialog.setProgressPercentFormat(null);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("friendSummaries", friendSummaries);
    }

    private void startFriendsListDownload() {
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        numberOfFriends = 0;
        friendSummaries.clear();

        FriendsListParser parser = new FriendsListParser(this);
        parser.execute(Conversions.steam32IdToSteam64Id(AppPreferences.getActiveAccountId(getActivity())));
    }

    /**
     * Got friend Id's
     */
    @Override
    public void processFinish(FriendsListContainer result) {
        if(result!=null && result.getFriendslist()!=null) {
            numberOfFriends = result.getFriendslist().getFriends().size();
            for (Friend friend : result.getFriendslist().getFriends()) {
                PlayerSummaryParser parser = new PlayerSummaryParser(this);
                parser.execute(friend.getSteamid());
            }
        }
    }

    /**
     * Got summary of 1 player
     */
    @Override
    public void processFinish(PlayerSummaryContainer result) {
        if (result.getPlayers().getPlayers().size() > 0) {
            friendSummaries.add(result.getPlayers().getPlayers().get(0));
        }

        friendsDownloaded++;
        progressDialog.setProgress(friendsDownloaded);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(numberOfFriends);

        // Got all friends, update listview
        if(friendSummaries.size()==numberOfFriends){
            OrientationHelper.unlockOrientation(getActivity());
            Collections.sort(friendSummaries, new PlayerSummaryNameComparator());
            listView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            adapter.notifyDataSetChanged();
        }
    }
}
