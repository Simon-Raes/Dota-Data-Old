package be.simonraes.dotadata.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.MatchActivity;
import be.simonraes.dotadata.async.DetailMatchLoader;
import be.simonraes.dotadata.async.StatsMatchesLoader;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.holograph.Bar;
import be.simonraes.dotadata.holograph.BarGraph;
import be.simonraes.dotadata.holograph.PieGraph;
import be.simonraes.dotadata.holograph.PieSlice;
import be.simonraes.dotadata.statistics.RecordStats;
import be.simonraes.dotadata.util.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.*;

/**
 * Created by Simon on 14/02/14.
 * Creates the layout for stats and calculates those stats.
 */
public class StatsNumbersFragment extends Fragment implements View.OnClickListener, StatsMatchesLoader.ASyncResponseStatsLoader, DetailMatchLoader.DetailMatchLoaderDelegate {

    private LayoutInflater inflater;

    private ScrollView scrollStats;
    private ProgressBar progressStats;

    private int mShortAnimationDuration;

    private View view;
    private LinearLayout layStatsGameModes, layStatsHeroes, layStatsRecords, layStatsNumbers, layStatsNoGames;

    private PieGraph pieGraph;
    private BarGraph barGraph;
    private ArrayList<Bar> points;

    private ArrayList<DetailMatchLite> matches;
    private HashMap<String, Integer> gameModesMap; //contains played gamemodes, count
    private HashMap<String, Integer> heroesMap; //contains played heroes, count

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
            txtStatsAverageXPM,
            txtStatsTotalLastHits,
            txtStatsTotalDenies,
            txtStatsAverageLastHits,
            txtStatsAverageDenies;

    private ImageButton btnStatsHelp;
    private String gameModeID, heroID; //ID of the selected item in the spinner

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.stats_layout, null);
        this.inflater = inflater;

        scrollStats = (ScrollView) view.findViewById(R.id.svStats);
        progressStats = (ProgressBar) view.findViewById(R.id.pbStats);

        crossFadeToLoading();

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
        txtStatsTotalLastHits = (TextView) view.findViewById(R.id.txtStatsTotalLastHits);
        txtStatsTotalDenies = (TextView) view.findViewById(R.id.txtStatsTotalDenies);
        txtStatsAverageLastHits = (TextView) view.findViewById(R.id.txtStatsAverageLastHits);
        txtStatsAverageDenies = (TextView) view.findViewById(R.id.txtStatsAverageDenies);

        btnStatsHelp = (ImageButton) view.findViewById(R.id.btnStatsHelp);
        btnStatsHelp.setOnClickListener(this);


        layStatsGameModes = (LinearLayout) view.findViewById(R.id.layStatsGameModes);
        layStatsHeroes = (LinearLayout) view.findViewById(R.id.layStatsHeroes);
        layStatsNumbers = (LinearLayout) view.findViewById(R.id.layStatsNumbers);
        layStatsRecords = (LinearLayout) view.findViewById(R.id.layStatsRecords);
        layStatsNoGames = (LinearLayout) view.findViewById(R.id.layStatsNoGames);

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

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
        boolean wasDialog = false;
        switch (v.getId()) {
            case R.id.btnStatsHelp:
                wasDialog = true;
                showInfoDialog();
                break;
            default:
                break;
        }

        // Open the match details screen if a user clicks one of the records.
        if (!wasDialog) {
            if (v.getTag() != null) {
                getActivity().setProgressBarIndeterminateVisibility(true);
                DetailMatchLoader dml = new DetailMatchLoader(getActivity(), this);
                dml.execute(v.getTag().toString());
            }
        }
    }

    /**
     * Received match object from the database
     */
    @Override
    public void loadDone(DetailMatch match) {
        if (match != null) {
            Intent intent = new Intent(getActivity(), MatchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("match", match);
            startActivity(intent);
            getActivity().setProgressBarIndeterminateVisibility(false);
        }
    }

    private void setNumbers() {

        //reset numbers
        double gamesPlayed = 0;
        double gamesWon = 0;
        double gamesLost = 0;
        double winrate;
        double totalDuration = 0;
        double averageDuration;
        double totalKills = 0, totalDeaths = 0, totalAssists = 0;
        double averageKills, averageDeaths, averageAssists;
        double totalGPM = 0, totalXPM = 0;
        double averageGPM, averageXPM;
        double totalLastHits = 0, totalDenies = 0;
        double averageLastHits;
        double averageDenies;

        RecordStats statsLongestGame = new RecordStats("Longest game: ");
        RecordStats statsMostKills = new RecordStats("Most kills: ");
        RecordStats statsMostDeaths = new RecordStats("Most deaths: ");
        RecordStats statsMostAssists = new RecordStats("Most assists: ");
        RecordStats statsMostLastHits = new RecordStats("Most last hits: ");
        RecordStats statsMostDenies = new RecordStats("Most denies; ");
        RecordStats statsMostHeroDamage = new RecordStats("Most hero damage: ");
        RecordStats statsMostHeroHealing = new RecordStats("Most hero healing: ");
        RecordStats statsMostTowerDamage = new RecordStats("Most tower damage: ");
        RecordStats statsMostGpm = new RecordStats("Most GPM: ");
        RecordStats statsMostXpm = new RecordStats("Most XPM: ");

        ArrayList<RecordStats> recordStatsList = new ArrayList<RecordStats>();
        recordStatsList.add(statsLongestGame);
        recordStatsList.add(statsMostKills);
        recordStatsList.add(statsMostDeaths);
        recordStatsList.add(statsMostAssists);
        recordStatsList.add(statsMostLastHits);
        recordStatsList.add(statsMostDenies);
        recordStatsList.add(statsMostHeroDamage);
        recordStatsList.add(statsMostHeroHealing);
        recordStatsList.add(statsMostTowerDamage);
        recordStatsList.add(statsMostGpm);
        recordStatsList.add(statsMostXpm);

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
            totalLastHits += Double.parseDouble(matchLite.getLast_hits());
            totalDenies += Double.parseDouble(matchLite.getDenies());

            // Records
            if (Long.parseLong(matchLite.getDuration()) > Long.parseLong(statsLongestGame.getRecordValue())) {
                statsLongestGame.setRecordValue(matchLite.getDuration());
                setBetterRecord(statsLongestGame, matchLite);
            }
            if (Integer.parseInt(matchLite.getKills()) > Integer.parseInt(statsMostKills.getRecordValue())) {
                statsMostKills.setRecordValue(matchLite.getKills());
                setBetterRecord(statsMostKills, matchLite);
            }
            if (Integer.parseInt(matchLite.getDeaths()) > Integer.parseInt(statsMostDeaths.getRecordValue())) {
                statsMostDeaths.setRecordValue(matchLite.getDeaths());
                setBetterRecord(statsMostDeaths, matchLite);
            }
            if (Integer.parseInt(matchLite.getAssists()) > Integer.parseInt(statsMostAssists.getRecordValue())) {
                statsMostAssists.setRecordValue(matchLite.getAssists());
                setBetterRecord(statsMostAssists, matchLite);
            }
            if (Integer.parseInt(matchLite.getLast_hits()) > Integer.parseInt(statsMostLastHits.getRecordValue())) {
                statsMostLastHits.setRecordValue(matchLite.getLast_hits());
                setBetterRecord(statsMostLastHits, matchLite);
            }
            if (Integer.parseInt(matchLite.getDenies()) > Integer.parseInt(statsMostDenies.getRecordValue())) {
                statsMostDenies.setRecordValue(matchLite.getDenies());
                setBetterRecord(statsMostDenies, matchLite);
            }
            if (Integer.parseInt(matchLite.getHero_damage()) > Integer.parseInt(statsMostHeroDamage.getRecordValue())) {
                statsMostHeroDamage.setRecordValue(matchLite.getHero_damage());
                setBetterRecord(statsMostHeroDamage, matchLite);
            }
            if (Integer.parseInt(matchLite.getHero_healing()) > Integer.parseInt(statsMostHeroHealing.getRecordValue())) {
                statsMostHeroHealing.setRecordValue(matchLite.getHero_healing());
                setBetterRecord(statsMostHeroHealing, matchLite);
            }
            if (Integer.parseInt(matchLite.getTower_damage()) > Integer.parseInt(statsMostTowerDamage.getRecordValue())) {
                statsMostTowerDamage.setRecordValue(matchLite.getTower_damage());
                setBetterRecord(statsMostTowerDamage, matchLite);
            }
            if (Integer.parseInt(matchLite.getGold_per_min()) > Integer.parseInt(statsMostGpm.getRecordValue())) {
                statsMostGpm.setRecordValue(matchLite.getGold_per_min());
                setBetterRecord(statsMostGpm, matchLite);
            }
            if (Integer.parseInt(matchLite.getXp_per_min()) > Integer.parseInt(statsMostXpm.getRecordValue())) {
                statsMostXpm.setRecordValue(matchLite.getXp_per_min());
                setBetterRecord(statsMostXpm, matchLite);
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
        averageLastHits = totalLastHits / gamesPlayed;
        averageDenies = totalDenies / gamesPlayed;

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
        txtStatsTotalLastHits.setText(Html.fromHtml("Total last hits<br>" + (int) totalLastHits));
        txtStatsTotalDenies.setText(Html.fromHtml("Total denies<br>" + (int) totalDenies));
        txtStatsAverageLastHits.setText(Html.fromHtml("Average last hits<br>" + Conversions.roundDouble(averageLastHits, 1)));
        txtStatsAverageDenies.setText(Html.fromHtml("Average denies<br>" + Conversions.roundDouble(averageDenies, 1)));

        // Records
        LinearLayout layRecords = (LinearLayout) view.findViewById(R.id.layStatsRecords);
        layRecords.removeAllViews();
        TextView header = new TextView(getActivity());
        header.setText("Records");
        layRecords.addView(header);
        layRecords.addView(inflater.inflate(R.layout.divider, null));

        ImageLoader imageLoader = ImageLoader.getInstance();
        ImageLoadingListener animateFirstListener = new ImageLoadListener();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.hero_sb_loading)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        for (RecordStats s : recordStatsList) {
            View recordRow = inflater.inflate(R.layout.stats_record_row, null);
            recordRow.setOnClickListener(this);
            recordRow.setTag(s.getMatchId());

            ImageView imgHero = (ImageView) recordRow.findViewById(R.id.imgTestHero);
            TextView txtRecord = (TextView) recordRow.findViewById(R.id.txtTestRecord);
            TextView txtInfo = (TextView) recordRow.findViewById(R.id.txtTestGameMode);


            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(s.getHeroId()) + "_lg.png", imgHero, options, animateFirstListener);

            // Special case: duration record has to be converted from seconds to HH:MM:SS
            if (s.getRecord().contains("Longest")) {
                txtRecord.setText(s.getRecord() + Conversions.secondsToTime(s.getRecordValue()));
            } else {
                txtRecord.setText(s.getRecord() + s.getRecordValue());

            }
            txtInfo.setText(GameModes.getGameMode(s.getGameMode()) + " (" + Conversions.millisToDate(s.getMatchStart()) + ")");

            layRecords.addView(recordRow);
            layRecords.addView(inflater.inflate(R.layout.divider, null));
        }
    }

    private void setBetterRecord(RecordStats stats, DetailMatchLite match) {
        stats.setMatchId(match.getMatch_id());
        stats.setMatchStart(match.getStart_time());
        stats.setGameMode(match.getGame_mode());
        stats.setHeroId(match.getHero_id());
    }

    private void setGameModesGraph() {

        LinearLayout layLegend = (LinearLayout) view.findViewById(R.id.layPieLegend);
        layLegend.removeAllViews();

        pieGraph = (PieGraph) view.findViewById(R.id.pieGameModes);
        pieGraph.removeSlices();
        pieGraph.setOnSliceClickedListener(pieHandler);

        Random rnd;

        // Sort map on games-played
        LinkedHashMap<String, Integer> sortedMap = Conversions.sortHashMapByValues(gameModesMap);
        // Reverse map (to large->small)
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(sortedMap.entrySet());

        for (int i = list.size() - 1; i >= 0; i--) {
            Map.Entry<String, Integer> entry = list.get(i);

            if (entry.getValue() > 0) {
                if (entry.getKey() != null) {

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
        layStatsGameModes.setVisibility(View.VISIBLE);
    }

    private void setHeroesGraph() {

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
                d.setValue(entry.getValue());
                d.setValueString(Integer.toString(entry.getValue()));

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
                .setMessage(Html.fromHtml("'Any gamemode' will show statistics based on all matches that had a first blood. <br>Diretide, Greeviling and custom gamemodes will not be included."))
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
