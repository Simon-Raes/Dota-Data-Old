package be.simonraes.dotadata.util;

import be.simonraes.dotadata.statistics.HeroStats;

import java.util.Comparator;

/**
 * Created by Simon Raes on 18/04/2014.
 */
public class HeroStatsWinrateComparator implements Comparator<HeroStats> {

    @Override
    public int compare(HeroStats heroStats, HeroStats heroStats2) {
        if (heroStats.getWinrate() < heroStats2.getWinrate()) return 1;
        if (heroStats.getWinrate() > heroStats2.getWinrate()) return -1;
        return 0;
    }
}
