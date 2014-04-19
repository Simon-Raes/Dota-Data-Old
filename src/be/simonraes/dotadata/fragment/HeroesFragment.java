package be.simonraes.dotadata.fragment;

import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.*;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.adapter.HeroStatsGamesPlayedAdapter;
import be.simonraes.dotadata.adapter.HeroStatsWinrateAdapter;
import be.simonraes.dotadata.adapter.RankingSpinnerAdapter;
import be.simonraes.dotadata.async.StatsMatchesLoader;
import be.simonraes.dotadata.delegates.ASyncResponseStatsLoader;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.statistics.HeroStats;
import be.simonraes.dotadata.util.HeroStatsGamesPlayedComparator;
import be.simonraes.dotadata.util.HeroStatsWinrateComparator;
import be.simonraes.dotadata.util.MatchUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Simon Raes on 18/04/2014.
 */
public class HeroesFragment extends Fragment implements ASyncResponseStatsLoader, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private ArrayList<HeroStats> heroes = new ArrayList<HeroStats>();
    private HashMap<String, HeroStats> heroesMap;
    private ListView lvHeroes;
    private HeroStatsWinrateAdapter listAdapter;

    private ListView listView;
    private ProgressBar progressBar;

    private Spinner spinnerRanking;
    private int spinnerRankingSelectedIndex = -1;

    long lastUpdate = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            spinnerRankingSelectedIndex = savedInstanceState.getInt("spinnerSelection");
        }
        lastUpdate = System.currentTimeMillis();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matches_list_layout, null);

        listView = (ListView) view.findViewById(R.id.lvRecentGames);
        progressBar = (ProgressBar) view.findViewById(R.id.pbRecentGames);

        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        getActivity().setTitle("Heroes");

        //update active drawer item
        if (getActivity() instanceof DrawerController) {
            ((DrawerController) getActivity()).setActiveDrawerItem(4);

            //make actionbar show drawer icon
            ((DrawerController) getActivity()).getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
            //update the actionbar to show the up carat
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setHasOptionsMenu(true);

        lvHeroes = (ListView) view.findViewById(R.id.lvRecentGames);
        lvHeroes.setSelector(new StateListDrawable());
        lvHeroes.setAdapter(new HeroStatsWinrateAdapter(getActivity(), heroes));
        lvHeroes.setOnItemClickListener(this);

        StatsMatchesLoader statsMatchesLoader = new StatsMatchesLoader(this, getActivity());
        statsMatchesLoader.execute("-1", "-1");


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("spinnerSelection", spinnerRankingSelectedIndex);
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
        MenuItem spinGameModes = menu.findItem(R.id.spinGameModes);
        if (spinGameModes != null) {
            spinGameModes.setVisible(false);
        }
        MenuItem spinHeroes = menu.findItem(R.id.spinHeroes);
        if (spinHeroes != null) {
            spinHeroes.setVisible(false);
        }

        MenuItem spinRanking = menu.findItem(R.id.spinRanking);
        if (spinRanking != null) {
            spinRanking.setVisible(true);
            spinnerRanking = (Spinner) spinRanking.getActionView();
            if (spinnerRanking != null) {

                spinnerRanking.setAdapter(new RankingSpinnerAdapter(getActivity(), new String[]{"Games played", "Winrate"}));
                spinnerRanking.setOnItemSelectedListener(this);

                if (spinnerRankingSelectedIndex >= 0) {
                    spinnerRanking.setSelection(spinnerRankingSelectedIndex);
                }
            }
        }
        lastUpdate = System.currentTimeMillis();
    }

    //received list of matches, now generate HeroStats objects
    @Override
    public void processFinish(ArrayList<DetailMatchLite> result) {
        heroesMap = new HashMap<String, HeroStats>();
        System.out.println("got my tgames");
        for (DetailMatchLite match : result) {

            if (heroesMap.containsKey(match.getHero_id())) {
                HeroStats stats = heroesMap.get(match.getHero_id());

                stats.setNumberOfGames(stats.getNumberOfGames() + 1);
                if (MatchUtils.isUser_win(match)) {
                    stats.setWins(stats.getWins() + 1);
                } else {
                    stats.setLosses(stats.getLosses() + 1);
                }
            } else {
                HeroStats stats = new HeroStats();

                stats.setHero_id(match.getHero_id());
                stats.setNumberOfGames(stats.getNumberOfGames() + 1);
                if (MatchUtils.isUser_win(match)) {
                    stats.setWins(stats.getWins() + 1);
                } else {
                    stats.setLosses(stats.getLosses() + 1);
                }

                heroesMap.put(match.getHero_id(), stats);
            }
        }

        heroes = new ArrayList<HeroStats>(heroesMap.values());
        if (spinnerRanking.getSelectedItemPosition() == 0) {
            //sort on games played
            Collections.sort(heroes, new HeroStatsGamesPlayedComparator());
            lvHeroes.setAdapter(new HeroStatsGamesPlayedAdapter(getActivity(), heroes));
        } else if (spinnerRanking.getSelectedItemPosition() == 1) {
            //sort on winrate
            Collections.sort(heroes, new HeroStatsWinrateComparator());
            lvHeroes.setAdapter(new HeroStatsWinrateAdapter(getActivity(), heroes));
        }
        listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (System.currentTimeMillis() - lastUpdate > 100) {
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            switch (adapterView.getId()) {
                case R.id.spinRanking:
                    spinnerRankingSelectedIndex = spinnerRanking.getSelectedItemPosition();
                    StatsMatchesLoader statsMatchesLoader = new StatsMatchesLoader(this, getActivity());
                    statsMatchesLoader.execute("-1", "-1");

                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        HeroStats stats = (HeroStats) lvHeroes.getAdapter().getItem(i);

        Fragment fragment = new StatsPagerFragment();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);

        //send object to fragment
        Bundle bundle = new Bundle();
        bundle.putString("hero_id", stats.getHero_id());
        fragment.setArguments(bundle);

        transaction.addToBackStack(null).commit();
    }
}
