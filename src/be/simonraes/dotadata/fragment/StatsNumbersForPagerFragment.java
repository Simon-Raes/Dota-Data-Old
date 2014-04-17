package be.simonraes.dotadata.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.*;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.activity.MatchActivity;
import be.simonraes.dotadata.adapter.GameModeSpinnerAdapter;
import be.simonraes.dotadata.adapter.HeroSpinnerAdapter;
import be.simonraes.dotadata.async.StatsMatchesLoader;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.delegates.ASyncResponseStatsLoader;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.holograph.Bar;
import be.simonraes.dotadata.holograph.BarGraph;
import be.simonraes.dotadata.holograph.PieGraph;
import be.simonraes.dotadata.holograph.PieSlice;
import be.simonraes.dotadata.statistics.PlayedHeroesMapper;
import be.simonraes.dotadata.util.*;

import java.util.*;

/**
 * Created by Simon on 14/02/14.
 * Creates the layout for stats and calculates stats
 */
public class StatsNumbersForPagerFragment extends Fragment implements View.OnClickListener, ASyncResponseStatsLoader {

    private ScrollView scrollStats;
    private ProgressBar progressStats;

    private int mShortAnimationDuration;

    private Spinner spinnerHeroes;
    private Spinner spinnerGameModes;

    private View view;
    private LinearLayout layStatsGameModes, layStatsHeroes, layStatsRecords, layStatsNumbers, layStatsNoGames;

    private PieGraph pieGraph;
    private BarGraph barGraph;
    private ArrayList<Bar> points;

    private ArrayList<DetailMatchLite> matches;
    private HashMap<String, Integer> gameModesMap; //contains played gamemodes, count
    private HashMap<String, String> mapGameModeIDName; //contains played gamemodesIDs, gamemodenames
    private HashMap<String, Integer> heroesMap; //contains played heroes, count
    private HashMap<String, String> mapHeroIDName; //contains played heroesID, heronames

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
            btnStatsMostTowerDamage,
            btnStatsMostGPM,
            btnStatsMostXPM;
    private ImageButton btnStatsHelp;

    private StatsMatchesLoader sml;
    //    private boolean wentToDetailsHeroes, wentToDetailsGameModes;
    //public boolean wentToDetails;
    long lastUpdate = 0;

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
    private int mostTowerDamage = 0;
    private String mostTowerDamageID;
    private int mostGPM = 0;
    private String mostGPMID;
    private int mostXPM = 0;
    private String mostXPMID;


    //
//    //ID of the selected item in the spinner
    private String gameModeID, heroID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("oncreateview");
        this.view = inflater.inflate(R.layout.stats_layout, null);


        //optionsmenu only gets created on second call for some reason, so call it both here and in the containing view pager fragment
        //fixes problem where spinners only show up after rotating the screen
        super.setHasOptionsMenu(true);


        scrollStats = (ScrollView) view.findViewById(R.id.svStats);
        progressStats = (ProgressBar) view.findViewById(R.id.pbStats);

        crossFadeToLoading();

//        scrollStats.setVisibility(View.GONE);
//        progressStats.setVisibility(View.VISIBLE);

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
        btnStatsMostTowerDamage = (Button) view.findViewById(R.id.btnStatsMostTowerDamage);
        btnStatsMostTowerDamage.setOnClickListener(this);
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


        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);


        return view;
    }

    public void setMatches(ArrayList<DetailMatchLite> matches, String gameModeID, String heroID) {
        this.matches = matches;
        this.gameModeID = gameModeID;
        this.heroID = heroID;
    }

    /*Sets textfields, charts, graphs,... with the stored data*/
    public void updateVisuals() {

        crossFadeToLoading();

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
        crossFadeToStats();

        //unlock orientation
        OrientationHelper.unlockOrientation(getActivity());
    }


    @Override
    public void onClick(View v) {
        System.out.println("onClick");

        DetailMatch match = null;
        MatchesDataSource mds = new MatchesDataSource(getActivity(), AppPreferences.getAccountID(getActivity()));
        System.out.println("went to details TRUE");
        switch (v.getId()) {
            case R.id.txtStatsLongestGame:
                if (Integer.parseInt(longestGameID) > 0) {
                    match = mds.getMatchByID(longestGameID);
                }
                break;
            case R.id.txtStatsMostKills:
                if (Integer.parseInt(mostKillsID) > 0) {
                    match = mds.getMatchByID(mostKillsID);
                }
                break;
            case R.id.txtStatsMostDeaths:
                if (Integer.parseInt(mostDeathsID) > 0) {
                    match = mds.getMatchByID(mostDeathsID);
                }
                break;
            case R.id.txtStatsMostAssists:
                if (Integer.parseInt(mostAssistsID) > 0) {
                    match = mds.getMatchByID(mostAssistsID);
                }
                break;
            case R.id.txtStatsMostLastHits:
                if (Integer.parseInt(mostLastHitsID) > 0) {
                    match = mds.getMatchByID(mostLastHitsID);
                }
                break;
            case R.id.txtStatsMostDenies:
                if (Integer.parseInt(mostDeniesID) > 0) {
                    match = mds.getMatchByID(mostDeniesID);
                }
                break;
            case R.id.txtStatsMostHeroDamage:
                if (Integer.parseInt(mostHeroDamageID) > 0) {
                    match = mds.getMatchByID(mostHeroDamageID);
                }
                break;
            case R.id.btnStatsMostTowerDamage:
                if (Integer.parseInt(mostTowerDamageID) > 0) {
                    match = mds.getMatchByID(mostTowerDamageID);
                }
                break;
            case R.id.txtStatsMostGPM:
                if (Integer.parseInt(mostGPMID) > 0) {
                    match = mds.getMatchByID(mostGPMID);
                }
                break;
            case R.id.txtStatsMostXPM:
                if (Integer.parseInt(mostXPMID) > 0) {
                    match = mds.getMatchByID(mostXPMID);
                }
                break;
            case R.id.btnStatsHelp:
                showInfoDialog();
                break;
            default:
                break;
        }

        if (match != null) {

            //launch this as a new activity so the state in the stats screen is easier to preserve

            Intent intent = new Intent(getActivity(), MatchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("match", match);
            startActivity(intent);
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

        //todo: hero healing

        //reset records
        longestGame = -1L;
        mostKills = -1;
        mostDeaths = -1;
        mostAssists = -1;
        mostLastHits = -1;
        mostDenies = -1;
        mostHeroDamage = -1;
        mostTowerDamage = -1;
        mostGPM = -1;
        mostXPM = -1;
        longestGameID = "-1";
        mostKillsID = "-1";
        mostDeathsID = "-1";
        mostAssistsID = "-1";
        mostLastHitsID = "-1";
        mostDeniesID = "-1";
        mostHeroDamageID = "-1";
        mostTowerDamageID = "-1";
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
        for (DetailMatchLite matchLite : matches) {
            //numbers
            gamesPlayed++;
//            if (matchLite.isUser_win()) {
//                gamesWon++;
//            } else {
//                gamesLost++;
//            }

            if (MatchUtils.isUser_win(matchLite)) {
                gamesWon++;

            } else {
                gamesLost++;
            }

            totalDuration += Double.parseDouble(matchLite.getDuration());
            totalKills += Double.parseDouble(matchLite.getKills());
            totalDeaths += Double.parseDouble(matchLite.getDeaths());
            totalAssists += Double.parseDouble(matchLite.getAssists());
            totalGPM += Double.parseDouble(matchLite.getGold_per_min());
            totalXPM += Double.parseDouble(matchLite.getXp_per_min());


            //records
            if (Long.parseLong(matchLite.getDuration()) > longestGame) {
                longestGame = Long.parseLong(matchLite.getDuration());
                longestGameID = matchLite.getMatch_id();
            }
            if (Integer.parseInt(matchLite.getKills()) > mostKills) {
                mostKills = Integer.parseInt(matchLite.getKills());
                mostKillsID = matchLite.getMatch_id();
            }
            if (Integer.parseInt(matchLite.getDeaths()) > mostDeaths) {
                mostDeaths = Integer.parseInt(matchLite.getDeaths());
                mostDeathsID = matchLite.getMatch_id();
            }
            if (Integer.parseInt(matchLite.getAssists()) > mostAssists) {
                mostAssists = Integer.parseInt(matchLite.getAssists());
                mostAssistsID = matchLite.getMatch_id();
            }
            if (Integer.parseInt(matchLite.getLast_hits()) > mostLastHits) {
                mostLastHits = Integer.parseInt(matchLite.getLast_hits());
                mostLastHitsID = matchLite.getMatch_id();
            }
            if (Integer.parseInt(matchLite.getDenies()) > mostDenies) {
                mostDenies = Integer.parseInt(matchLite.getDenies());
                mostDeniesID = matchLite.getMatch_id();
            }
            if (Integer.parseInt(matchLite.getHero_damage()) > mostHeroDamage) {
                mostHeroDamage = Integer.parseInt(matchLite.getHero_damage());
                mostHeroDamageID = matchLite.getMatch_id();
            }
            if (Integer.parseInt(matchLite.getTower_damage()) > mostTowerDamage) {
                mostTowerDamage = Integer.parseInt(matchLite.getTower_damage());
                mostTowerDamageID = matchLite.getMatch_id();
            }
            if (Integer.parseInt(matchLite.getGold_per_min()) > mostGPM) {
                mostGPM = Integer.parseInt(matchLite.getGold_per_min());
                mostGPMID = matchLite.getMatch_id();
            }
            if (Integer.parseInt(matchLite.getXp_per_min()) > mostXPM) {
                mostXPM = Integer.parseInt(matchLite.getXp_per_min());
                mostXPMID = matchLite.getMatch_id();
            }

            if (gameModesMap.get(GameModes.getGameMode(matchLite.getGame_mode())) != null) {
                int prevValue = gameModesMap.get(GameModes.getGameMode(matchLite.getGame_mode()));
                gameModesMap.put(GameModes.getGameMode(matchLite.getGame_mode()), prevValue + 1);
            }
            if (heroesMap.get(HeroList.getHeroName(matchLite.getHero_id())) != null) {
                int prevValue = heroesMap.get(HeroList.getHeroName(matchLite.getHero_id()));
                heroesMap.put(HeroList.getHeroName(matchLite.getHero_id()), prevValue + 1);
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
        btnStatsMostTowerDamage.setText("Most tower damage: " + mostTowerDamage);
        btnStatsMostGPM.setText("Highest GPM: " + mostGPM);
        btnStatsMostXPM.setText("Highest XPM: " + mostXPM);

    }

    private void setGameModesGraph() {
        System.out.println("setGameModesGraph");


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
        layStatsGameModes.setVisibility(View.VISIBLE);

    }

    private void setHeroesGraph() {
        System.out.println("setHeroesGraph");
        //System.out.println("matches size: "+matches.size());


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
        layStatsHeroes.setVisibility(View.VISIBLE);
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

    //got matches from database
    @Override
    public void processFinish(ArrayList<DetailMatchLite> result) {
        matches = result;
        updateVisuals();

        //((StatsPagerFragment) getParentFragment()).setMatches(matches);

    }


    private void crossFadeToStats() {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        scrollStats.setAlpha(0f);
        scrollStats.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        scrollStats.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        progressStats.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressStats.setVisibility(View.GONE);
                    }
                });

    }

    private void crossFadeToLoading() {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        progressStats.setAlpha(0f);
        progressStats.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        progressStats.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        scrollStats.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        scrollStats.setVisibility(View.GONE);
                    }
                });

    }
}
