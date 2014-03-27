package be.simonraes.dotadata.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.*;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.adapter.GameModeSpinnerAdapter;
import be.simonraes.dotadata.adapter.HeroSpinnerAdapter;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.holograph.Bar;
import be.simonraes.dotadata.holograph.BarGraph;
import be.simonraes.dotadata.holograph.PieGraph;
import be.simonraes.dotadata.holograph.PieSlice;
import be.simonraes.dotadata.statistics.DetailMatchLite;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.GameModes;
import be.simonraes.dotadata.util.HeroList;

import java.util.*;

/**
 * Created by Simon on 14/02/14.
 * Creates the layout for stats and calculates stats
 */
public class StatsFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private ScrollView scrollStats;
    private ProgressBar progressStats;

    private Spinner spinnerHeroes;
    private Spinner spinnerGameModes;

    private View view;
    private LinearLayout layStatsGameModes, layStatsHeroes, layStatsRecords, layStatsNumbers, layStatsNoGames;

    private boolean comesFromInstanceStateHeroes, comesFromInstanceStateGameModes;

    private int numbersCount = 0;


    private PieGraph pieGraph;
    private BarGraph barGraph;
    private ArrayList<Bar> points;

    private ArrayList<DetailMatchLite> matches;
    private HashMap<String, Integer> gameModesMap; //contains played gamemodes, count
    private HashMap<String, String> mapGameModeIDName; //contains played gamemodesIDs, gamemodenames
    private HashMap<String, Integer> heroesMap; //contains played heroes, count
    private HashMap<String, String> mapHeroIDName; //contains played heroesID, heronames

    private boolean wentToDetails;


    private TextView
            txtStatsGamesPlayed,
            txtStatsWinrate,
            txtStatsGamesWon,
            txtStatsGamesLost,
            txtStatsTotalDuration,
            txtStatsAverageDuration,
            txtStatsTotalKills,
            txtStatsTotalDeaths,
            txtStatsTotalAssists,
            txtStatsAverageKills,
            txtStatsAverageDeaths,
            txtStatsAverageAssists,
            txtStatsAverageGPM,
            txtStatsAverageXPM;

    private Button
            btnStatsLongestGame,
            btnStatsMostKills,
            btnStatsMostDeaths,
            btnStatsMostAssists,
            btnStatsMostLastHits,
            btnStatsMostDenies,
            btnStatsMostHeroDamage,
            btnStatsMostGPM,
            btnStatsMostXPM;
    private ImageButton btnStatsHelp;

    //numbers
    private double gamesPlayed = 0;
    private double gamesWon = 0;
    private double gamesLost = 0;
    private double winrate = 0;
    private double totalDuration;
    private double averageDuration;
    private double totalKills = 0, totalDeaths = 0, totalAssists = 0;
    private double averageKills = 0, averageDeaths = 0, averageAssists = 0;
    private double totalGPM = 0, totalXPM = 0;
    private double averageGPM = 0, averageXPM;

    //records
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
    private int mostHeroDamage = 0;
    private String mostHeroDamageID;
    private int mostGPM = 0;
    private String mostGPMID;
    private int mostXPM = 0;
    private String mostXPMID;

    private int heroSelection, gameModeSelection;

    private String gameModeID;
    private String heroID;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            System.out.println("come from state");
            // wentToDetails = false;
            comesFromInstanceStateHeroes = true;
            comesFromInstanceStateGameModes = true;
            gameModeSelection = savedInstanceState.getInt("gameModeSelection");
            heroSelection = savedInstanceState.getInt("heroSelection");
            matches = savedInstanceState.getParcelableArrayList("matches");
            mapHeroIDName = (HashMap<String, String>) savedInstanceState.getSerializable("mapHeroes");
            mapGameModeIDName = (HashMap<String, String>) savedInstanceState.getSerializable("mapGameModes");
//            countUpdate = 2;
        } else {
            System.out.println("come not from state");
            comesFromInstanceStateHeroes = false;
            comesFromInstanceStateGameModes = false;
            gameModeSelection = -1;
            heroSelection = -1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("oncreateview");
        this.view = inflater.inflate(R.layout.stats_layout, null);

        getActivity().setTitle("Statistics");
        setHasOptionsMenu(true);

        scrollStats = (ScrollView) view.findViewById(R.id.svStats);
        progressStats = (ProgressBar) view.findViewById(R.id.pbStats);

        //numbers
        txtStatsGamesPlayed = (TextView) view.findViewById(R.id.txtStatsGamesPlayed);
        txtStatsWinrate = (TextView) view.findViewById(R.id.txtStatsWinrate);
        txtStatsGamesWon = (TextView) view.findViewById(R.id.txtStatsGamesWon);
        txtStatsGamesLost = (TextView) view.findViewById(R.id.txtStatsGamesLost);
        txtStatsTotalDuration = (TextView) view.findViewById(R.id.txtStatsTotalDuration);
        txtStatsAverageDuration = (TextView) view.findViewById(R.id.txtStatsAverageDuration);
        txtStatsTotalKills = (TextView) view.findViewById(R.id.txtStatsTotalKills);
        txtStatsTotalDeaths = (TextView) view.findViewById(R.id.txtStatsTotalDeaths);
        txtStatsTotalAssists = (TextView) view.findViewById(R.id.txtStatsTotalAssists);
        txtStatsAverageKills = (TextView) view.findViewById(R.id.txtStatsAverageKills);
        txtStatsAverageDeaths = (TextView) view.findViewById(R.id.txtStatsAverageDeaths);
        txtStatsAverageAssists = (TextView) view.findViewById(R.id.txtStatsAverageAssists);
        txtStatsAverageGPM = (TextView) view.findViewById(R.id.txtStatsAverageGPM);
        txtStatsAverageXPM = (TextView) view.findViewById(R.id.txtStatsAverageXPM);

        //records
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
        btnStatsMostHeroDamage = (Button) view.findViewById(R.id.txtStatsMostHeroDamage);
        btnStatsMostHeroDamage.setOnClickListener(this);
        btnStatsMostGPM = (Button) view.findViewById(R.id.txtStatsMostGPM);
        btnStatsMostGPM.setOnClickListener(this);
        btnStatsMostXPM = (Button) view.findViewById(R.id.txtStatsMostXPM);
        btnStatsMostXPM.setOnClickListener(this);
        btnStatsHelp = (ImageButton) view.findViewById(R.id.btnStatsHelp);
        btnStatsHelp.setOnClickListener(this);

        layStatsGameModes = (LinearLayout) view.findViewById(R.id.layStatsGameModes);
        layStatsHeroes = (LinearLayout) view.findViewById(R.id.layStatsHeroes);
        layStatsNumbers = (LinearLayout) view.findViewById(R.id.layStatsNumbers);
        layStatsRecords = (LinearLayout) view.findViewById(R.id.layStatsRecords);
        layStatsNoGames = (LinearLayout) view.findViewById(R.id.layStatsNoGames);

//        countUpdate = 0;

        //ID of the active item in the spinners
        gameModeID = "-1";
        heroID = "-1";

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        System.out.println("onSaveInstanceState");

        super.onSaveInstanceState(outState);
        outState.putInt("gameModeSelection", gameModeSelection);
        System.out.println("put gamemodeselection " + gameModeSelection);
        outState.putInt("heroSelection", heroSelection);
        outState.putBoolean("comesFromInstanceStateHeroes", comesFromInstanceStateHeroes);
        outState.putBoolean("comesFromInstanceStateGameModes", comesFromInstanceStateGameModes);
        outState.putParcelableArrayList("matches", matches);
        outState.putSerializable("mapHeroes", mapHeroIDName);
        outState.putSerializable("mapGameModes", mapGameModeIDName);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        System.out.println("onCreateOptionsMenu");
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
        MenuItem spinGameModes = menu.findItem(R.id.spinGameModes);
        if (spinGameModes != null) {
            spinGameModes.setVisible(true);
            spinnerGameModes = (Spinner) spinGameModes.getActionView();
            if (spinnerGameModes != null) {

                //only show the played heroes in the dropdownlist
                if (mapHeroIDName == null) {
                    getPlayedHeroesAndGameModes();
                } else if (mapHeroIDName.size() < 1) {
                    getPlayedHeroesAndGameModes();
                }

                spinnerGameModes.setAdapter(new GameModeSpinnerAdapter(getActivity(), (HashMap<String, String>) mapGameModeIDName.clone()));
                //spinnerGameModes.setSelection(0,false); itemselected won't fire on first load with this
                spinnerGameModes.setOnItemSelectedListener(this);

                //if there was a savedState, set spinner selection
                if (gameModeSelection >= 0) {
                    spinnerGameModes.setSelection(gameModeSelection);
                }
            }
        }
        MenuItem spinHeroes = menu.findItem(R.id.spinHeroes);
        if (spinHeroes != null) {
            spinHeroes.setVisible(true);
            spinnerHeroes = (Spinner) spinHeroes.getActionView();
            if (spinnerHeroes != null) {

                //only show the played heroes in the dropdownlist
                if (mapHeroIDName == null) {
                    getPlayedHeroesAndGameModes();
                } else if (mapHeroIDName.size() < 1) {
                    getPlayedHeroesAndGameModes();
                }

                spinnerHeroes.setAdapter(new HeroSpinnerAdapter(getActivity(), (HashMap<String, String>) mapHeroIDName.clone()));
                spinnerHeroes.setOnItemSelectedListener(this);

                //if there was a savedState, set spinner selection
                if (heroSelection >= 0) {
                    spinnerHeroes.setSelection(heroSelection);
                }
            }
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("onItemSelected");

        switch (parent.getId()) {
            case R.id.spinGameModes:
                GameModeSpinnerAdapter gAdapter = (GameModeSpinnerAdapter) parent.getAdapter();
                gameModeID = gAdapter.getIDForPosition(position);
                gameModeSelection = spinnerGameModes.getSelectedItemPosition();

                //don't reload matches for a reorientation
                if (!comesFromInstanceStateGameModes) {
                    if (!wentToDetails) {
                        updateMatches();
                    }
                }


                comesFromInstanceStateGameModes = false;
                if (numbersCount > 0) {
                    updateVisuals();
                }
                numbersCount++;

                break;
            case R.id.spinHeroes:
                HeroSpinnerAdapter adapter = (HeroSpinnerAdapter) parent.getAdapter();
                heroID = adapter.getIDForPosition(position);
                heroSelection = spinnerHeroes.getSelectedItemPosition();

                //don't reload matches for a reorientation
                if (!comesFromInstanceStateHeroes) {
                    if (!wentToDetails) {
                        updateMatches();
                    }
                }
                System.out.println("setting wenttodetails to false");
                wentToDetails = false;

                comesFromInstanceStateHeroes = false;
                //display the new data
                if (numbersCount > 0) {
                    updateVisuals();
                }
                numbersCount++;


                break;

            default:
                break;
        }
    }

    private void getPlayedHeroesAndGameModes() {
        System.out.println("getPlayedHeroesAndGameModes");

        MatchesDataSource mds = new MatchesDataSource(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("be.simonraes.dotadata.accountid", ""));
        mapHeroIDName = new HashMap<String, String>();
        mapGameModeIDName = new HashMap<String, String>();
        for (DetailMatchLite rec : mds.getAllStatRecords()) {
            mapHeroIDName.put(rec.getHero_id(), HeroList.getHeroName(rec.getHero_id()));
            mapGameModeIDName.put(rec.getGame_mode(), GameModes.getGameMode(rec.getGame_mode()));
        }
        System.out.println("made new heroes map: " + mapHeroIDName.size());



    }


    //todo: put this in background thread
    /*Gets new data for the selected filters*/
    private void updateMatches() {
        System.out.println("updateMatches");
        //System.out.println("matches size: "+matches.size());

        scrollStats.setVisibility(View.GONE);
        progressStats.setVisibility(View.VISIBLE);

        //initializing the spinners calls the itemSelected method, this counter makes sure this query is only called once when opening the screen

        System.out.println("inside count>2");

        layStatsGameModes.setVisibility(View.GONE);
        layStatsHeroes.setVisibility(View.GONE);

        MatchesDataSource mds = new MatchesDataSource(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("be.simonraes.dotadata.accountid", ""));

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
    }

    /*Sets textfields, charts, graphs,... with the stored data*/
    private void updateVisuals() {
        System.out.println("updateVisuals");

        layStatsHeroes.setVisibility(View.GONE);
        layStatsGameModes.setVisibility(View.GONE);


        //neither selected, get statsrecords for all games
        if (Integer.parseInt(gameModeID) < 1 && Integer.parseInt(heroID) < 1) {
            if (matches.size() > 0) {
                btnStatsHelp.setVisibility(View.VISIBLE);
                setNumbers();
                setGameModesGraph();
                setHeroesGraph();
            }
        }
        //only gamemode selected
        else if (Integer.parseInt(gameModeID) > 0 && Integer.parseInt(heroID) < 1) {
            if (matches.size() > 0) {
                btnStatsHelp.setVisibility(View.GONE);
                setNumbers();
                setHeroesGraph();
            }
        }
        //only hero selected
        else if (Integer.parseInt(gameModeID) < 1 && Integer.parseInt(heroID) > 0) {
            if (matches.size() > 0) {
                btnStatsHelp.setVisibility(View.VISIBLE);
                setNumbers();
                setGameModesGraph();
            }
        }
        //both selected
        else {
            if (matches.size() > 0) {
                btnStatsHelp.setVisibility(View.GONE);
                setNumbers();
            }
        }

        //only show stats cards if matches were found
        if (matches.size() > 0) {
            layStatsNumbers.setVisibility(View.VISIBLE);
            layStatsRecords.setVisibility(View.VISIBLE);
            layStatsNoGames.setVisibility(View.GONE);
        } else {
            layStatsNumbers.setVisibility(View.GONE);
            layStatsRecords.setVisibility(View.GONE);
            layStatsNoGames.setVisibility(View.VISIBLE);
        }

        //show view, hide loading indicator
        scrollStats.setVisibility(View.VISIBLE);
        progressStats.setVisibility(View.GONE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        System.out.println("onClick");

        DetailMatch match = null;
        MatchesDataSource mds = new MatchesDataSource(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("be.simonraes.dotadata.accountid", ""));

        switch (v.getId()) {
            case R.id.txtStatsLongestGame:
                if (Integer.parseInt(longestGameID) > 0) {
                    match = mds.getMatchByID(longestGameID);
                    wentToDetails = true;
                }
                break;
            case R.id.txtStatsMostKills:
                if (Integer.parseInt(mostKillsID) > 0) {
                    match = mds.getMatchByID(mostKillsID);
                    wentToDetails = true;
                }
                break;
            case R.id.txtStatsMostDeaths:
                if (Integer.parseInt(mostDeathsID) > 0) {
                    match = mds.getMatchByID(mostDeathsID);
                    wentToDetails = true;
                }
                break;
            case R.id.txtStatsMostAssists:
                if (Integer.parseInt(mostAssistsID) > 0) {
                    match = mds.getMatchByID(mostAssistsID);
                    wentToDetails = true;
                }
                break;
            case R.id.txtStatsMostLastHits:
                if (Integer.parseInt(mostLastHitsID) > 0) {
                    match = mds.getMatchByID(mostLastHitsID);
                    wentToDetails = true;
                }
                break;
            case R.id.txtStatsMostDenies:
                if (Integer.parseInt(mostDeniesID) > 0) {
                    match = mds.getMatchByID(mostDeniesID);
                    wentToDetails = true;
                }
                break;
            case R.id.txtStatsMostHeroDamage:
                if (Integer.parseInt(mostHeroDamageID) > 0) {
                    match = mds.getMatchByID(mostHeroDamageID);
                    wentToDetails = true;
                }
                break;
            case R.id.txtStatsMostGPM:
                if (Integer.parseInt(mostGPMID) > 0) {
                    match = mds.getMatchByID(mostGPMID);
                    wentToDetails = true;
                }
                break;
            case R.id.txtStatsMostXPM:
                if (Integer.parseInt(mostXPMID) > 0) {
                    match = mds.getMatchByID(mostXPMID);
                    wentToDetails = true;
                }
                break;
            case R.id.btnStatsHelp:
                showInfoDialog();
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

    private void setNumbers() {
        System.out.println("setNumbers");


        //reset numbers
        gamesPlayed = 0; //
        gamesWon = 0;//
        gamesLost = 0;//
        winrate = 0;//
        totalDuration = 0;//
        averageDuration = 0;
        totalKills = 0;//
        totalDeaths = 0;//
        totalAssists = 0;//
        averageKills = 0;
        averageDeaths = 0;
        averageAssists = 0;
        totalGPM = 0; //
        totalXPM = 0; //
        averageGPM = 0;
        averageXPM = 0;

        //todo: most tower damage, hero healing

        //reset records
        longestGame = -1L;
        mostKills = -1;
        mostDeaths = -1;
        mostAssists = -1;
        mostLastHits = -1;
        mostDenies = -1;
        mostHeroDamage = -1;
        mostGPM = -1;
        mostXPM = -1;
        longestGameID = "-1";
        mostKillsID = "-1";
        mostDeathsID = "-1";
        mostAssistsID = "-1";
        mostLastHitsID = "-1";
        mostDeniesID = "-1";
        mostHeroDamageID = "-1";
        mostGPMID = "-1";
        mostXPMID = "-1";

        //calculated number of games played per gamemode
        gameModesMap = new HashMap<String, Integer>();
        for (String a : GameModes.getGameModes().values()) {
            gameModesMap.put(a, 0);
        }
        //calculate number of games played per hero
        heroesMap = new HashMap<String, Integer>();
        for (String a : HeroList.getHeroes().values()) {
            heroesMap.put(a, 0);
        }

        //calculate new values
        for (DetailMatchLite rec : matches) {
            //numbers
            gamesPlayed++;
            if (rec.isUser_win()) {
                gamesWon++;
            } else {
                gamesLost++;
            }
            totalDuration += Double.parseDouble(rec.getDuration());
            totalKills += Double.parseDouble(rec.getKills());
            totalDeaths += Double.parseDouble(rec.getDeaths());
            totalAssists += Double.parseDouble(rec.getAssists());
            totalGPM += Double.parseDouble(rec.getGold_per_min());
            totalXPM += Double.parseDouble(rec.getXp_per_min());


            //records
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
            if (Integer.parseInt(rec.getGold()) > mostHeroDamage) {
                mostHeroDamage = Integer.parseInt(rec.getHero_damage());
                mostHeroDamageID = rec.getMatch_id();
            }
            if (Integer.parseInt(rec.getGold_per_min()) > mostGPM) {
                mostGPM = Integer.parseInt(rec.getGold_per_min());
                mostGPMID = rec.getMatch_id();
            }
            if (Integer.parseInt(rec.getXp_per_min()) > mostXPM) {
                mostXPM = Integer.parseInt(rec.getXp_per_min());
                mostXPMID = rec.getMatch_id();
            }
            if (gameModesMap.get(GameModes.getGameMode(rec.getGame_mode())) != null) {
                int prevValue = gameModesMap.get(GameModes.getGameMode(rec.getGame_mode()));
                gameModesMap.put(GameModes.getGameMode(rec.getGame_mode()), prevValue + 1);
            }
            if (heroesMap.get(HeroList.getHeroName(rec.getHero_id())) != null) {
                int prevValue = heroesMap.get(HeroList.getHeroName(rec.getHero_id()));
                heroesMap.put(HeroList.getHeroName(rec.getHero_id()), prevValue + 1);
            }
        }

        if (gamesPlayed > 0) {
            winrate = (gamesWon / gamesPlayed) * 100;
        } else {
            winrate = 0;
        }

        averageDuration = totalDuration / gamesPlayed;
        averageKills = totalKills / gamesPlayed;
        averageDeaths = totalDeaths / gamesPlayed;
        averageAssists = totalAssists / gamesPlayed;
        averageGPM = totalGPM / gamesPlayed;
        averageXPM = totalXPM / gamesPlayed;

        //numbers
        txtStatsGamesPlayed.setText(Html.fromHtml("Games played<br>" + (int) gamesPlayed));
        txtStatsWinrate.setText(Html.fromHtml("Winrate<br>" + Conversions.roundDouble(winrate, 2) + "%"));
        txtStatsGamesWon.setText(Html.fromHtml("Games won<br>" + (int) gamesWon));
        txtStatsGamesLost.setText(Html.fromHtml("Games lost<br>" + (int) gamesLost));
        txtStatsTotalDuration.setText(Html.fromHtml("Total time played<br>" + Conversions.secondsToTime(Integer.toString((int) totalDuration))));
        txtStatsAverageDuration.setText(Html.fromHtml("Average game length<br>" + Conversions.secondsToTime(Integer.toString((int) averageDuration))));
        txtStatsTotalKills.setText(Html.fromHtml("Total kills<br>" + (int) totalKills));
        txtStatsTotalDeaths.setText(Html.fromHtml("Total deaths<br>" + (int) totalDeaths));
        txtStatsTotalAssists.setText(Html.fromHtml("Total assists<br>" + (int) totalAssists));
        txtStatsAverageKills.setText(Html.fromHtml("Average kills<br>" + Conversions.roundDouble(averageKills, 1)));
        txtStatsAverageDeaths.setText(Html.fromHtml("Average deaths<br>" + Conversions.roundDouble(averageDeaths, 1)));
        txtStatsAverageAssists.setText(Html.fromHtml("Average assists<br>" + Conversions.roundDouble(averageAssists, 1)));
        txtStatsAverageGPM.setText(Html.fromHtml("Average GPM<br>" + (int) averageGPM));
        txtStatsAverageXPM.setText(Html.fromHtml("Average XPM<br>" + (int) averageXPM));

        //records
        btnStatsLongestGame.setText("Longest game: " + Conversions.secondsToTime(longestGame.toString()));
        btnStatsMostKills.setText("Most kills: " + mostKills);
        btnStatsMostDeaths.setText("Most deaths: " + mostDeaths);
        btnStatsMostAssists.setText("Most assists: " + mostAssists);
        btnStatsMostLastHits.setText("Most last hits: " + mostLastHits);
        btnStatsMostDenies.setText("Most denies: " + mostDenies);
        btnStatsMostHeroDamage.setText("Most hero damage: " + mostHeroDamage);
        btnStatsMostGPM.setText("Highest GPM: " + mostGPM);
        btnStatsMostXPM.setText("Highest XPM: " + mostXPM);

    }

    private void setGameModesGraph() {
        System.out.println("setGameModesGraph");

        layStatsGameModes.setVisibility(View.VISIBLE);
        LinearLayout layLegend = (LinearLayout) view.findViewById(R.id.layPieLegend);
        layLegend.removeAllViews();

        pieGraph = (PieGraph) view.findViewById(R.id.pieGameModes);
        pieGraph.removeSlices();
        pieGraph.setOnSliceClickedListener(pieHandler);

        Random rnd;
        for (Map.Entry<String, Integer> entry : gameModesMap.entrySet()) {
            if (entry.getValue() > 0) {
                //seed the random so a gamemode will always have the same color
                rnd = new Random(entry.getKey().hashCode() + 43438); // +1 for better colors, tested 2-10 (not good)
                int sliceColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                //add slice
                PieSlice slice = new PieSlice();
                slice.setColor(sliceColor);
                slice.setValue(entry.getValue());
                slice.setTitle(entry.getKey());
                pieGraph.addSlice(slice);

                //set legend
                TextView txtLegend = new TextView(getActivity());
                txtLegend.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                txtLegend.setText(entry.getKey() + ": " + entry.getValue());
                txtLegend.setTextColor(sliceColor);
                layLegend.addView(txtLegend);
            }
        }
    }

    private void setHeroesGraph() {
        System.out.println("setHeroesGraph");
        //System.out.println("matches size: "+matches.size());

        layStatsHeroes.setVisibility(View.VISIBLE);
        barGraph = (BarGraph) view.findViewById(R.id.barHeroes);
        barGraph.setOnBarClickedListener(barHandler);

        points = new ArrayList<Bar>();
        Random rnd;
        for (Map.Entry<String, Integer> entry : heroesMap.entrySet()) {
            if (entry.getValue() > 0) {
                Bar d = new Bar();
                //seed the random so a hero will always have the same color
                rnd = new Random(entry.getKey().hashCode());
                d.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                d.setName(entry.getKey());
                d.setValue((int) entry.getValue());
                d.setValueString(Integer.toString((int) entry.getValue()));

                points.add(d);
            }
        }
        if (points.size() > 0) {
            Collections.sort(points);
            if (points.size() > 10) {
                points.subList(10, points.size()).clear();
            }

        }
        barGraph.setBars(points);
    }


    private void showInfoDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Any gamemode statistics")
                .setMessage("'Any gamemode' will show statistics based on all matches, except Diretide, Greeviling and Custom gamemodes.")
                .setCancelable(true)
                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    //called when clicking a gamemode piechart slice
    PieGraph.OnSliceClickedListener pieHandler = new PieGraph.OnSliceClickedListener() {
        public void onClick(int index) {
            String game = " game.";
            if ((int) pieGraph.getSlice(index).getValue() != 1) {
                game = " games.";
            }
            Toast.makeText(getActivity(), pieGraph.getSlice(index).getTitle() + ": " + (int) pieGraph.getSlice(index).getValue() + game, Toast.LENGTH_SHORT).show();
        }
    };

    //listener for bar chart
    BarGraph.OnBarClickedListener barHandler = new BarGraph.OnBarClickedListener() {
        public void onClick(int index) {
            Toast.makeText(getActivity(), points.get(index).getName(), Toast.LENGTH_SHORT).show();
        }
    };
}
