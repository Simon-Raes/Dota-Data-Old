package be.simonraes.dotadata.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.activity.MatchActivity;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.delegates.ASyncResponseDetail;
import be.simonraes.dotadata.delegates.ASyncResponseDetailList;
import be.simonraes.dotadata.detailmatch.DetailContainer;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.parser.DetailMatchParser;
import be.simonraes.dotadata.parser.DetailMatchesParser;
import be.simonraes.dotadata.util.AppPreferences;
import be.simonraes.dotadata.util.OrientationHelper;


import java.util.ArrayList;

/**
 * Created by Simon Raes on 19/04/2014.
 */
public class SearchFragment extends Fragment implements View.OnClickListener, ASyncResponseDetail {

    private Button btnSearch;
    private EditText txtMatch;
    public static ProgressDialog introDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragView = inflater.inflate(R.layout.search_fragment_layout, container, false);

        getActivity().setTitle("Search");

        //update active drawer item (0 = this screen has no drawer item)
        ((DrawerController) getActivity()).setActiveDrawerItem(0);

        txtMatch = (EditText) fragView.findViewById(R.id.txtSearchMatchID);
        txtMatch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchMatch();
                    return true;
                }
                return false;
            }
        });
        btnSearch = (Button) fragView.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);

        setHasOptionsMenu(true);

        return fragView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSearch:
                searchMatch();
                break;
            default:
                break;
        }
    }

    private void searchMatch() {
        String matchID = txtMatch.getText().toString();

        if (matchID != null && !matchID.equals("")) {

            MatchesDataSource mds = new MatchesDataSource(getActivity(), AppPreferences.getAccountID(getActivity()));
            if (mds.matchExists(matchID)) {
                DetailMatch match = mds.getMatchByID(matchID);
                Intent intent = new Intent(getActivity(), MatchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("match", match);
                startActivity(intent);
            } else {
                //not saved yet, download it
                //this parser does NOT store the match in the database
                //(users could download only their wins and skew the stats)
                DetailMatchParser parser = new DetailMatchParser(this);
                parser.execute(matchID);
                //todo : should show loading dialog here
                OrientationHelper.lockOrientation(getActivity());
                introDialog = ProgressDialog.show(getActivity(), "", "Downloading match.", true);

            }
        }
    }


    @Override
    public void processFinish(DetailContainer result) {
        OrientationHelper.unlockOrientation(getActivity());
        if (introDialog != null) {
            if (introDialog.isShowing()) {
                introDialog.dismiss();
            }
        }
        if (result.getDetailMatch().getMatch_id() != null && !result.getDetailMatch().getMatch_id().equals("")) {
            DetailMatch match = result.getDetailMatch();
            Intent intent = new Intent(getActivity(), MatchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("match", match);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "No match found with that ID.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (introDialog != null) {
            if (introDialog.isShowing()) {
                introDialog.dismiss();
            }
        }
    }
}
