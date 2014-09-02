package be.simonraes.dotadata.comparator;

import be.simonraes.dotadata.statistics.HeroStats;

import java.util.Comparator;

/**
 * Created by Simon Raes on 18/04/2014.
 */
public class HeroStatsGamesPlayedComparator implements Comparator<HeroStats> {

    @Override
    public int compare(HeroStats heroStats, HeroStats heroStats2) {
        if (heroStats.getNumberOfGames() < heroStats2.getNumberOfGames()) return 1;
        if (heroStats.getNumberOfGames() > heroStats2.getNumberOfGames()) return -1;
        return 0;
    }
}
