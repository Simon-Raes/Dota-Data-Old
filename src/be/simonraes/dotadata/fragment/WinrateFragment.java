package be.simonraes.dotadata.fragment;

import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.adapter.WinrateAdapter;
import be.simonraes.dotadata.async.StatsMatchesLoader;
import be.simonraes.dotadata.delegates.ASyncResponseStatsLoader;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.statistics.HeroStats;
import be.simonraes.dotadata.util.HeroList;
import be.simonraes.dotadata.util.HeroStatsWinrateComparator;
import be.simonraes.dotadata.util.MatchUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Simon Raes on 18/04/2014.
 */
public class WinrateFragment extends Fragment implements ASyncResponseStatsLoader {

    private ArrayList<HeroStats> heroes = new ArrayList<HeroStats>();
    private HashMap<String, HeroStats> heroesMap;
    private ListView lvWinrate;
    private WinrateAdapter listAdapter;


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        //onCreate() will still be called when fragment is in the background, save heroes here for when user returns to this screen
//        if (savedInstanceState != null) {
//            heroes = savedInstanceState.getParcelableArrayList("heroes");
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matches_list_layout, null);

        getActivity().setTitle("Winrate");

        lvWinrate = (ListView) view.findViewById(R.id.lvRecentGames);
        lvWinrate.setSelector(new StateListDrawable());

        listAdapter = new WinrateAdapter(getActivity(), heroes);
        lvWinrate.setAdapter(listAdapter);

        StatsMatchesLoader statsMatchesLoader = new StatsMatchesLoader(this, getActivity());
        statsMatchesLoader.execute("-1", "-1");


        return view;
    }

    @Override
    public void processFinish(ArrayList<DetailMatchLite> result) {
        heroesMap = new HashMap<String, HeroStats>();

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
        Collections.sort(heroes, new HeroStatsWinrateComparator());
        listAdapter = new WinrateAdapter(getActivity(), heroes);
        lvWinrate.setAdapter(listAdapter);
    }
}
