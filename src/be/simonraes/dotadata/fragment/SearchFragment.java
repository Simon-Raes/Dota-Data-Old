package be.simonraes.dotadata.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.activity.MatchActivity;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.detailmatch.DetailContainer;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.parser.DetailMatchParser;
import be.simonraes.dotadata.util.AppPreferences;
import be.simonraes.dotadata.util.OrientationHelper;

/**
 * Screen to search a game by MatchId.
 * Created by Simon Raes on 19/04/2014.
 */
public class SearchFragment extends Fragment implements View.OnClickListener, DetailMatchParser.ASyncResponseDetail {

    private EditText txtMatch;
    public static ProgressDialog introDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragView = inflater.inflate(R.layout.search_fragment_layout, container, false);

        if (getActivity().getActionBar() != null) {
            getActivity().setTitle("Search");
            getActivity().getActionBar().setSubtitle(null);
        }
        setHasOptionsMenu(true);

        //update active drawer item (0 = this screen has no drawer item)
        ((DrawerController) getActivity()).setActiveDrawerItem(6);
        //make sure keyboard doesn't automatically open on page load
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        txtMatch = (EditText) fragView.findViewById(R.id.txtSearchMatchID);
        //lock orientation while keyboard is visible (crashes when going from portrait to landscape, but not the other way around (?!))
        txtMatch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    OrientationHelper.lockOrientation(getActivity());
                } else {
                    OrientationHelper.unlockOrientation(getActivity());
                }
            }
        });

        //handle the OK button on the keyboard
        txtMatch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(txtMatch.getWindowToken(), 0);
                    searchMatch();
                    return true;
                }
                return false;
            }
        });
        Button btnSearch = (Button) fragView.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);

        return fragView;
    }

    @Override
    public void onClick(View view) {
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(txtMatch.getWindowToken(), 0);

        switch (view.getId()) {
            case R.id.btnSearch:
                searchMatch();
                break;
            default:
                break;
        }
    }


    private void searchMatch() {

        String matchID = "";

        if (txtMatch != null) {
            matchID = txtMatch.getText().toString();
        }

        if (matchID != null && !matchID.equals("")) {

            MatchesDataSource mds = new MatchesDataSource(getActivity(), AppPreferences.getActiveAccountId(getActivity()));
            if (mds.matchExists(matchID)) {
                DetailMatch match = mds.getMatchByID(matchID);
                Intent intent = new Intent(getActivity(), MatchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("match", match);
                startActivity(intent);
            } else {
                // Not saved yet, download it. This parser does NOT store the match in the database
                // (users could download only their wins and skew the stats if it did)
                DetailMatchParser parser = new DetailMatchParser(this);
                parser.execute(matchID);
                OrientationHelper.lockOrientation(getActivity());
                introDialog = ProgressDialog.show(getActivity(), "", "Searching.", true);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_menu, menu);

        MenuItem btnRefresh = menu.findItem(R.id.btnRefresh);
        if (btnRefresh != null) {
            btnRefresh.setVisible(false);
        }
        MenuItem btnFavourite = menu.findItem(R.id.btnFavourite);
        if (btnFavourite != null) {
            btnFavourite.setVisible(false);
        }
        MenuItem btnNote = menu.findItem(R.id.btnNote);
        if (btnNote != null) {
            btnNote.setVisible(false);
        }
        MenuItem spinHeroes = menu.findItem(R.id.spinHeroes);
        if (spinHeroes != null) {
            spinHeroes.setVisible(false);
        }
        MenuItem spinGameModes = menu.findItem(R.id.spinGameModes);
        if (spinGameModes != null) {
            spinGameModes.setVisible(false);
        }
    }

    @Override
    public void processFinish(DetailContainer result) {
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
        OrientationHelper.unlockOrientation(getActivity());
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
