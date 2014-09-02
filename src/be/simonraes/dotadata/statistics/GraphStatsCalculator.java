package be.simonraes.dotadata.statistics;

import android.content.Context;
import android.os.AsyncTask;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.util.AppPreferences;
import be.simonraes.dotadata.util.MatchUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

/**
 * Gets the required matches from the database and generates the stats for the graphs.
 * Created by Simon Raes on 28/08/2014.
 */
public class GraphStatsCalculator extends AsyncTask<Void, Void, ArrayList<GraphStats>> {

    private GraphStatsDelegate delegate;

    public interface GraphStatsDelegate {
        void matchesLoaded(ArrayList<GraphStats> list);
    }

    private Context context;

    public GraphStatsCalculator(GraphStatsDelegate delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected ArrayList<GraphStats> doInBackground(Void... voids) {
        return getGraphStats();
    }

    public ArrayList<GraphStats> getGraphStats() {
        TreeMap<String, GraphStats> mapStats = new TreeMap<String, GraphStats>();
        ArrayList<GraphStats> statsList = null;
        ArrayList<GraphStats> finalStatsList = null;

        MatchesDataSource mds = new MatchesDataSource(context, AppPreferences.getActiveAccountId(context));
        ArrayList<DetailMatchLite> matches = mds.getAllRealDetailMatchesLite();

        if (matches != null && matches.size() > 0) {

            for (DetailMatchLite match : matches) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(Long.parseLong(match.getStart_time()) * 1000);

                // Calculate the week of the year manually, since WEEK_OF_YEAR is unreliable for the requirements here.
                int weekOfYear = cal.get(Calendar.DAY_OF_YEAR) - 1;
                weekOfYear = (weekOfYear / 7) + 1;

                // Group games by week
                String date = Integer.toString(cal.get(Calendar.YEAR));
                date += " - week ";
                if (weekOfYear < 10) {
                    date += "0" + weekOfYear;
                } else {
                    date += weekOfYear;
                }

                // Group matches by period in a hashmap <Date, stats>
                GraphStats graphStatsForWeek;
                int cumulativeNumberOfGames = 0;
                int cumulativeWins = 0;
                int cumulativeLosses = 0;
                int cumulativeGPM = 0;
                int cumulativeLastHits = 0;
                int cumulativeKills = 0;
                int cumulativeDeaths = 0;
                int cumulativeAssists = 0;

                if (mapStats.containsKey(date)) {
                    // Already contains info for this period, get that info so it can be added to the the new data.
                    graphStatsForWeek = mapStats.get(date);
                    cumulativeNumberOfGames = graphStatsForWeek.getNumberOfGamesPeriod();
                    cumulativeWins = graphStatsForWeek.getWinsPeriod();
                    cumulativeLosses = graphStatsForWeek.getLossesPeriod();
                    cumulativeGPM = graphStatsForWeek.getGpmTotalPeriod();
                    cumulativeLastHits = graphStatsForWeek.getLastHitsTotalPeriod();
                    cumulativeKills = graphStatsForWeek.getKillsTotalPeriod();
                    cumulativeDeaths = graphStatsForWeek.getDeathsTotalPeriod();
                    cumulativeAssists = graphStatsForWeek.getAssistsTotalPeriod();
                } else {
                    graphStatsForWeek = new GraphStats();
                    graphStatsForWeek.setDateString(date);
                    mapStats.put(date, graphStatsForWeek);
                }

                // Total games
                graphStatsForWeek.setNumberOfGamesPeriod(cumulativeNumberOfGames + 1);
                // Wins & losses
                if (MatchUtils.isUser_win(match)) {
                    graphStatsForWeek.setWinsPeriod(cumulativeWins + 1);
                } else {
                    graphStatsForWeek.setLossesPeriod(cumulativeLosses + 1);
                }
                // GPM
                graphStatsForWeek.setGpmTotalPeriod(cumulativeGPM + Integer.parseInt(match.getGold_per_min()));
                // Last hits
                graphStatsForWeek.setLastHitsTotalPeriod(cumulativeLastHits + Integer.parseInt(match.getLast_hits()));
                // Kills
                graphStatsForWeek.setKillsTotalPeriod(cumulativeKills + Integer.parseInt(match.getKills()));
                // Deaths
                graphStatsForWeek.setDeathsTotalPeriod(cumulativeDeaths + Integer.parseInt(match.getDeaths()));
                // Assists
                graphStatsForWeek.setAssistsTotalPeriod(cumulativeAssists + Integer.parseInt(match.getAssists()));
                // Date
                graphStatsForWeek.setYear(cal.get(Calendar.YEAR));
                graphStatsForWeek.setWeek(weekOfYear);
            }

            statsList = new ArrayList<GraphStats>(mapStats.values());

            //set the first element
            statsList.get(0).setSequenceNumber(0);
            // Winrate
            statsList.get(0).setWinrateCumulative(((double) statsList.get(0).getWinsPeriod() / (double) statsList.get(0).getNumberOfGamesPeriod()) * 100);
            statsList.get(0).setNumberOfGamesCumulative(statsList.get(0).getNumberOfGamesPeriod());
            // GPM
            statsList.get(0).setGpmTotalCumulative(statsList.get(0).getGpmTotalPeriod());
            statsList.get(0).setGpmAveragedCumulative(statsList.get(0).getGpmTotalPeriod() / statsList.get(0).getNumberOfGamesCumulative());
            // Last hits
            statsList.get(0).setLastHitsTotalCumulative(statsList.get(0).getLastHitsTotalPeriod());
            statsList.get(0).setLastHitsAveragedCumulative(statsList.get(0).getLastHitsTotalPeriod() / statsList.get(0).getNumberOfGamesCumulative());
            // Kills
            statsList.get(0).setKillsTotalCumulative(statsList.get(0).getKillsTotalPeriod());
            statsList.get(0).setKillsAveragedCumulative(statsList.get(0).getKillsTotalPeriod() / statsList.get(0).getNumberOfGamesCumulative());
            // Deaths
            statsList.get(0).setDeathsTotalCumulative(statsList.get(0).getDeathsTotalPeriod());
            statsList.get(0).setDeathsAveragedCumulative(statsList.get(0).getDeathsTotalPeriod() / statsList.get(0).getNumberOfGamesCumulative());
            // Assists
            statsList.get(0).setAssistsTotalCumulative(statsList.get(0).getAssistsTotalPeriod());
            statsList.get(0).setAssistsAveragedCumulative(statsList.get(0).getAssistsTotalPeriod() / statsList.get(0).getNumberOfGamesCumulative());

            //calculate the others
            for (int i = 1; i < statsList.size(); i++) {


                    statsList.get(i).setSequenceNumber(i);
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

                    double gpmCumulativeAveraged = (double)gpmTotalCumulative / (double)statsList.get(i).getNumberOfGamesCumulative();
                    statsList.get(i).setGpmAveragedCumulative(gpmCumulativeAveraged);

                    // Last hits
                    int lastHitsTotalCumulative = statsList.get(i - 1).getLastHitsTotalCumulative() + statsList.get(i).getLastHitsTotalPeriod();
                    statsList.get(i).setLastHitsTotalCumulative(lastHitsTotalCumulative);

                    double lastHitsCumulativeAveraged = (double)lastHitsTotalCumulative / (double)statsList.get(i).getNumberOfGamesCumulative();
                    statsList.get(i).setLastHitsAveragedCumulative(lastHitsCumulativeAveraged);

                    // Kills
                    int killsTotalCumulative = statsList.get(i - 1).getKillsTotalCumulative() + statsList.get(i).getKillsTotalPeriod();
                    statsList.get(i).setKillsTotalCumulative(killsTotalCumulative);

                    double killsCumulativeAveraged = (double)killsTotalCumulative / (double)statsList.get(i).getNumberOfGamesCumulative();
                    statsList.get(i).setKillsAveragedCumulative(killsCumulativeAveraged);

                    // Deaths
                    int deathsTotalCumulative = statsList.get(i - 1).getDeathsTotalCumulative() + statsList.get(i).getDeathsTotalPeriod();
                    statsList.get(i).setDeathsTotalCumulative(deathsTotalCumulative);

                    double deathsCumulativeAveraged = (double)deathsTotalCumulative / (double)statsList.get(i).getNumberOfGamesCumulative();
                    statsList.get(i).setDeathsAveragedCumulative(deathsCumulativeAveraged);

                    // Assists
                    int assistsTotalCumulative = statsList.get(i - 1).getAssistsTotalCumulative() + statsList.get(i).getAssistsTotalPeriod();
                    statsList.get(i).setAssistsTotalCumulative(assistsTotalCumulative);

                    double assistsCumulativeAveraged = (double)assistsTotalCumulative / (double)statsList.get(i).getNumberOfGamesCumulative();
                    statsList.get(i).setAssistsAveragedCumulative(assistsCumulativeAveraged);
            }


            finalStatsList = new ArrayList<GraphStats>();
            finalStatsList.add(statsList.get(0));

            for (int i = 1; i < statsList.size(); i++) {
                int weekDifference = 0;
                int yearDifference = statsList.get(i).getYear() - statsList.get(i - 1).getYear();

                if (yearDifference == 0) {
                    weekDifference = (statsList.get(i).getWeek() - statsList.get(i - 1).getWeek()) - 1;
                } else {
                    for (int j = 0; j < yearDifference; j++) {
                        int weeksInFirstYear = 0;
                        if (statsList.get(i - 1).getWeek() == 53) {
                            weeksInFirstYear = 53 - statsList.get(i - 1).getWeek();

                        } else {
                            weeksInFirstYear = 52 - statsList.get(i - 1).getWeek();

                        }
//                        int weeksInFirstYear = 52 - statsList.get(i - 1).getWeek();
                        int weeksInSecondYear = statsList.get(i).getWeek() - 1;
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

    @Override
    protected void onPostExecute(ArrayList<GraphStats> graphStatses) {
        super.onPostExecute(graphStatses);
        delegate.matchesLoaded(graphStatses);
    }
}
