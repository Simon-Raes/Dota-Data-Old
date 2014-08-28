package be.simonraes.dotadata.statistics;

import android.content.Context;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.util.AppPreferences;
import be.simonraes.dotadata.util.MatchUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

/**
 * Created by Simon Raes on 28/08/2014.
 */
public class GraphStatsCalculator {

    private Context context;

    public GraphStatsCalculator(Context context) {
        this.context = context;
    }


    public ArrayList<GraphStats> getGraphStats() {
        TreeMap<String, GraphStats> mapStats = new TreeMap<String, GraphStats>();
        ArrayList<GraphStats> statsList = null;
        ArrayList<GraphStats> finalStatsList = null;

        MatchesDataSource mds = new MatchesDataSource(context, AppPreferences.getAccountID(context));
        ArrayList<DetailMatchLite> matches = mds.getAllRealDetailMatchesLite();

        if (matches != null && matches.size() > 0) {

            for (DetailMatchLite match : matches) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(Long.parseLong(match.getStart_time()) * 1000);

                //using individual days
//            String date = Integer.toString(cal.get(Calendar.YEAR));
//            date += "-";
//            if (cal.get(Calendar.MONTH) < 10) {
//                date += "0" + cal.get(Calendar.MONTH);
//            } else {
//                date += cal.get(Calendar.MONTH);
//            }
//            date += "-";
//            if (cal.get(Calendar.DAY_OF_MONTH) < 10) { //more precise info with DAY_OF_MONTH
//                date += "0" + cal.get(Calendar.DAY_OF_MONTH);
//            } else {
//                date += cal.get(Calendar.DAY_OF_MONTH);
//            }

                //using weeks of year
                String date = Integer.toString(cal.get(Calendar.YEAR));
                date += " - week ";
                if (cal.get(Calendar.WEEK_OF_YEAR) < 10) {
                    date += "0" + cal.get(Calendar.WEEK_OF_YEAR);
                } else {
                    date += cal.get(Calendar.WEEK_OF_YEAR);
                }

                // Set period values
                if (mapStats.containsKey(date)) {
                    GraphStats stats = mapStats.get(date);
                    stats.setNumberOfGamesPeriod(stats.getNumberOfGamesPeriod() + 1);
                    if (MatchUtils.isUser_win(match)) {
                        stats.setWinsPeriod(stats.getWinsPeriod() + 1);
                    } else {
                        stats.setLossesPeriod(stats.getLossesPeriod() + 1);
                    }
                    //gpm
                    stats.setGpmTotalPeriod(stats.getGpmTotalPeriod() + Integer.parseInt(match.getGold_per_min()));
                    //date
                    stats.setYear(cal.get(Calendar.YEAR));
                    stats.setWeek(cal.get(Calendar.WEEK_OF_YEAR));
                } else {
                    GraphStats newStats = new GraphStats();
                    newStats.setNumberOfGamesPeriod(1);
                    newStats.setDateString(date);
                    if (MatchUtils.isUser_win(match)) {
                        newStats.setWinsPeriod(1);
                    } else {
                        newStats.setLossesPeriod(1);
                    }
                    //gpm
                    newStats.setGpmTotalPeriod(Integer.parseInt(match.getGold_per_min()));
                    mapStats.put(date, newStats);
                    //date
                    newStats.setYear(cal.get(Calendar.YEAR));
                    newStats.setWeek(cal.get(Calendar.WEEK_OF_YEAR));
                }
            }

            statsList = new ArrayList<GraphStats>(mapStats.values());

            //set the first element

            // Winrate
            statsList.get(0).setWinrateCumulative(((double) statsList.get(0).getWinsPeriod() / (double) statsList.get(0).getNumberOfGamesPeriod()) * 100);
            statsList.get(0).setNumberOfGamesCumulative(statsList.get(0).getNumberOfGamesPeriod());
            // GPM
            statsList.get(0).setGpmTotalCumulative(statsList.get(0).getGpmTotalPeriod());
            System.out.println("total gpm cumulative week 0 = " + statsList.get(0).getGpmTotalCumulative());

            //calculate the others
            for (int i = 1; i < statsList.size(); i++) {

                // Number of games
                int numberOfGamesCumulative = statsList.get(i - 1).getNumberOfGamesCumulative() + statsList.get(i).getNumberOfGamesPeriod();
                statsList.get(i).setNumberOfGamesCumulative(numberOfGamesCumulative);

                // Winrate
                double winratePeriod = ((double) statsList.get(i).getWinsPeriod() / (double) statsList.get(i).getNumberOfGamesPeriod()) * 100;

                double winrateCumulative = ((statsList.get(i - 1).getWinrateCumulative() * statsList.get(i - 1).getNumberOfGamesCumulative())
                        + (winratePeriod * statsList.get(i).getNumberOfGamesPeriod())) / numberOfGamesCumulative;
                statsList.get(i).setWinrateCumulative(winrateCumulative);

                // GPM
                int gpmTotalCumulative = statsList.get(i - 1).getGpmTotalCumulative() + statsList.get(i).getGpmTotalPeriod();
                statsList.get(i).setGpmTotalCumulative(gpmTotalCumulative);

                double gpmCumulativeAveraged = gpmTotalCumulative / statsList.get(i).getNumberOfGamesCumulative();
                statsList.get(i).setGpmAveragedCumulative(gpmCumulativeAveraged);

                System.out.println("total gpm this week= " + statsList.get(i).getGpmTotalPeriod() + "(" + statsList.get(i).getNumberOfGamesPeriod() + " games)");
                System.out.println("average gpm this week= " + statsList.get(i).getGpmTotalPeriod() / statsList.get(i).getNumberOfGamesPeriod());
                System.out.println("total gpm cumulative= " + statsList.get(i).getGpmTotalCumulative() + "(" + statsList.get(i).getNumberOfGamesCumulative() + " games)");
                System.out.println("average gpm cumulative= " + statsList.get(i).getGpmAveragedCumulative());
                System.out.println("year: " + statsList.get(i).getYear() + ", week: " + statsList.get(i).getWeek());
                System.out.println("--------");
            }


            finalStatsList = new ArrayList<GraphStats>();

            for (int i = 1; i < statsList.size(); i++) {
                int weekDifference = 0;
                int yearDifference = statsList.get(i).getYear() - statsList.get(i - 1).getYear();

                if (yearDifference == 0) {
                    weekDifference = (statsList.get(i).getWeek() - statsList.get(i - 1).getWeek()) - 1;
                } else {
                    for (int j = 0; j < yearDifference; j++) {
                        int weeksInFirstYear = 52 - statsList.get(i - 1).getWeek();
                        int weeksInSecondYear = statsList.get(i).getWeek()-1;
                        weekDifference += weeksInFirstYear + weeksInSecondYear;
                    }
                }

                // For every missing week (difference more than one, insert a null.
                for (int j = 0; j < weekDifference; j++) {
                    finalStatsList.add(null);
                }

                // Add the week after adding the correct number of padding (null) weeks
                finalStatsList.add(statsList.get(i));
            }
        }
        return finalStatsList;
    }
}
