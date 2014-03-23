package be.simonraes.dotadata.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.adapter.GameModeSpinnerAdapter;
import be.simonraes.dotadata.adapter.HeroSpinnerAdapter;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.statistics.StatsRecord;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.GameModes;
import be.simonraes.dotadata.util.HeroList;

import java.util.ArrayList;

/**
 * Created by Simon on 14/02/14.
 * Creates the layout for stats and calculates stats
 */
public class StatsFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private ScrollView svStats;
    private ProgressBar pbStats;

    //todo: saveInstanceState so state is saved when going BACK to this screen (from match details)
    //http://stackoverflow.com/questions/6787071/android-fragment-how-to-save-states-of-views-in-a-fragment-when-another-fragmen

    private TextView btnStatsGamesPlayed, btnStatsWinrate;

    private Button
            btnStatsLongestGame,
            btnStatsMostKills,
            btnStatsMostDeaths,
            btnStatsMostAssists,
            btnStatsMostLastHits,
            btnStatsMostDenies,
            btnStatsMostGold,
            btnStatsMostGPM,
            btnStatsMostXPM;

    //todo: averages

    private double gamesPlayed = 0;
    private double gameswon = 0;
    private double winrate = 0;
    private Long longestGame = 0L;
    private String longestGameID;
    private int mostKills = 0;
    private String mostKillsID;
    private int mostDeaths = 0;
    private String mostDeathsID;
    private int mostAssists = 0;
    private String mostAssistsID;
    private int mostLastHits = 0;
    private String mostLastHitsID;
    private int mostDenies = 0;
    private String mostDeniesID;
    private int mostGold = 0;
    private String mostGoldID;
    private int mostGPM = 0;
    private String mostGPMID;
    private int mostXPM = 0;
    private String mostXPMID;


    private String gameModeID = "-1";
    private String heroID = "-1";

    private int countUpdate = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stats_layout, null);

        getActivity().setTitle("Statistics");
        setHasOptionsMenu(true);

        svStats = (ScrollView) view.findViewById(R.id.svStats);
        pbStats = (ProgressBar) view.findViewById(R.id.pbStats);

        btnStatsGamesPlayed = (TextView) view.findViewById(R.id.txtStatsGamesPlayed);
        btnStatsWinrate = (TextView) view.findViewById(R.id.txtStatsWinrate);
        btnStatsLongestGame = (Button) view.findViewById(R.id.txtStatsLongestGame);
        btnStatsLongestGame.setOnClickListener(this);
        btnStatsMostKills = (Button) view.findViewById(R.id.txtStatsMostKills);
        btnStatsMostKills.setOnClickListener(this);
        btnStatsMostDeaths = (Button) view.findViewById(R.id.txtStatsMostDeaths);
        btnStatsMostDeaths.setOnClickListener(this);
        btnStatsMostAssists = (Button) view.findViewById(R.id.txtStatsMostAssists);
        btnStatsMostAssists.setOnClickListener(this);
        btnStatsMostLastHits = (Button) view.findViewById(R.id.txtStatsMostLastHits);
        btnStatsMostLastHits.setOnClickListener(this);
        btnStatsMostDenies = (Button) view.findViewById(R.id.txtStatsMostDenies);
        btnStatsMostDenies.setOnClickListener(this);
        btnStatsMostGold = (Button) view.findViewById(R.id.txtStatsMostGold);
        btnStatsMostGold.setOnClickListener(this);
        btnStatsMostGPM = (Button) view.findViewById(R.id.txtStatsMostGPM);
        btnStatsMostGPM.setOnClickListener(this);
        btnStatsMostXPM = (Button) view.findViewById(R.id.txtStatsMostXPM);
        btnStatsMostXPM.setOnClickListener(this);

//        svStats.setVisibility(View.GONE);
//        pbStats.setVisibility(View.VISIBLE);

        svStats.setVisibility(View.VISIBLE);
        pbStats.setVisibility(View.GONE);

        return view;
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
            spinHeroes.setVisible(true);
            Spinner sp = (Spinner) spinHeroes.getActionView();
            if (sp != null) {
                sp.setAdapter(new HeroSpinnerAdapter(getActivity(), HeroList.getHeroes()));
                sp.setOnItemSelectedListener(this);
            }
        }
        MenuItem spinGameModes = menu.findItem(R.id.spinGameModes);
        if (spinGameModes != null) {
            spinGameModes.setVisible(true);
            Spinner sp = (Spinner) spinGameModes.getActionView();
            if (sp != null) {
                sp.setAdapter(new GameModeSpinnerAdapter(getActivity(), GameModes.getGameModes()));
                sp.setOnItemSelectedListener(this);
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int spinID = parent.getId();
        switch (spinID) {
            case R.id.spinHeroes:
                HeroSpinnerAdapter adapter = (HeroSpinnerAdapter) parent.getAdapter();
                heroID = adapter.getIDForPosition(position);
                updateContent();

                break;
            case R.id.spinGameModes:
                GameModeSpinnerAdapter gAdapter = (GameModeSpinnerAdapter) parent.getAdapter();
                gameModeID = gAdapter.getIDForPosition(position);
                updateContent();

                break;
            default:
                break;
        }
    }

    private void updateContent() {
        //initializing the spinners calls the itemSelected method, this counter makes sure this query is only called once when opening the screen
        if (countUpdate > 2) {
            MatchesDataSource mds = new MatchesDataSource(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("be.simonraes.dotadata.accountid", ""));
            ArrayList<StatsRecord> matches = null;

            //neither selected, get statsrecords for all games
            if (Integer.parseInt(gameModeID) < 1 && Integer.parseInt(heroID) < 1) {
                matches = mds.getAllStatRecords();
            }
            //only gamemode selected
            else if (Integer.parseInt(gameModeID) > 0 && Integer.parseInt(heroID) < 1) {
                matches = mds.getAllStatRecordsForGameMode(gameModeID);
            }
            //only hero selected
            else if (Integer.parseInt(gameModeID) < 1 && Integer.parseInt(heroID) > 0) {
                matches = mds.getAllStatRecordsForHero(heroID);
            }
            //both selected
            else {
                matches = mds.getAllStatRecordsForHeroAndGameMode(heroID, gameModeID);
            }

            //reset values
            gamesPlayed = 0;
            gameswon = 0;
            winrate = 0;
            longestGame = 0L;
            mostKills = 0;
            mostDeaths = 0;
            mostAssists = 0;
            mostLastHits = 0;
            mostDenies = 0;
            mostGold = 0;
            mostGPM = 0;
            mostXPM = 0;

            //calculate new values
            for (StatsRecord rec : matches) {
                gamesPlayed++;
                if (rec.isUser_win()) {
                    gameswon++;
                }
                if (Long.parseLong(rec.getDuration()) > longestGame) {
                    longestGame = Long.parseLong(rec.getDuration());
                    longestGameID = rec.getMatch_id();
                }
                if (Integer.parseInt(rec.getKills()) > mostKills) {
                    mostKills = Integer.parseInt(rec.getKills());
                    mostKillsID = rec.getMatch_id();
                }
                if (Integer.parseInt(rec.getDeaths()) > mostDeaths) {
                    mostDeaths = Integer.parseInt(rec.getDeaths());
                    mostDeathsID = rec.getMatch_id();
                }
                if (Integer.parseInt(rec.getAssists()) > mostAssists) {
                    mostAssists = Integer.parseInt(rec.getAssists());
                    mostAssistsID = rec.getMatch_id();
                }
                if (Integer.parseInt(rec.getLast_hits()) > mostLastHits) {
                    mostLastHits = Integer.parseInt(rec.getLast_hits());
                    mostLastHitsID = rec.getMatch_id();
                }
                if (Integer.parseInt(rec.getDenies()) > mostDenies) {
                    mostDenies = Integer.parseInt(rec.getDenies());
                    mostDeniesID = rec.getMatch_id();
                }
                if (Integer.parseInt(rec.getGold()) > mostGold) {
                    mostGold = Integer.parseInt(rec.getGold());
                    mostGoldID = rec.getMatch_id();
                }
                if (Integer.parseInt(rec.getGold_per_min()) > mostGPM) {
                    mostGPM = Integer.parseInt(rec.getGold_per_min());
                    mostGPMID = rec.getMatch_id();
                }
                if (Integer.parseInt(rec.getXp_per_min()) > mostXPM) {
                    mostXPM = Integer.parseInt(rec.getXp_per_min());
                    mostXPMID = rec.getMatch_id();
                }
            }
            System.out.println(gameswon + " games won");
            if (gamesPlayed > 0) {
                winrate = (gameswon / gamesPlayed) * 100;
            } else {
                winrate = 0;
            }

            btnStatsGamesPlayed.setText("Games played: " + (int) gamesPlayed);
            btnStatsWinrate.setText("Winrate: " + Conversions.roundDouble(winrate, 2) + "%");
            btnStatsLongestGame.setText("Longest game: " + Conversions.secondsToTime(longestGame.toString()));
            btnStatsMostKills.setText("Most kills: " + mostKills);
            btnStatsMostDeaths.setText("Most deaths: " + mostDeaths);
            btnStatsMostAssists.setText("Most assists: " + mostAssists);
            btnStatsMostLastHits.setText("Most last hits: " + mostLastHits);
            btnStatsMostDenies.setText("Most denies: " + mostDenies);
            btnStatsMostGold.setText("Most gold: " + mostGold);
            btnStatsMostGPM.setText("Highest GPM: " + mostGPM);
            btnStatsMostXPM.setText("Highest XPM: " + mostXPM);
        }
        countUpdate++;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        DetailMatch match = null;
        MatchesDataSource mds = new MatchesDataSource(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("be.simonraes.dotadata.accountid", ""));

        /*
        mostAssistsID;
    private String mostLastHitsID;
    private String ;
    private String ;
    private String ;
    private String ;
         */


        switch (v.getId()) {
            case R.id.txtStatsLongestGame:
                match = mds.getMatchByID(longestGameID);
                break;
            case R.id.txtStatsMostKills:
                match = mds.getMatchByID(mostKillsID);
                break;
            case R.id.txtStatsMostDeaths:
                match = mds.getMatchByID(mostDeathsID);
                break;
            case R.id.txtStatsMostLastHits:
                match = mds.getMatchByID(mostLastHitsID);
                break;
            case R.id.txtStatsMostDenies:
                match = mds.getMatchByID(mostDeniesID);
                break;
            case R.id.txtStatsMostGold:
                match = mds.getMatchByID(mostGoldID);
                break;
            case R.id.txtStatsMostGPM:
                match = mds.getMatchByID(mostGPMID);
                break;
            case R.id.txtStatsMostXPM:
                match = mds.getMatchByID(mostXPMID);
                break;
            default:
                break;
        }

        if (match != null) {

            Fragment fragment = new MatchDetailFragment();

            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_frame, fragment);

            //hacky way to set UP arrow in actionbar of matchdetails screen
            if (((DrawerController) getActivity()) != null) {
                ((DrawerController) getActivity()).getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
            }

            //send object to fragment
            Bundle bundle = new Bundle();
            bundle.putParcelable("be.simonraes.dotadata.detailmatch", match);
            fragment.setArguments(bundle);


            transaction.addToBackStack(null).commit();
        }
    }
}
