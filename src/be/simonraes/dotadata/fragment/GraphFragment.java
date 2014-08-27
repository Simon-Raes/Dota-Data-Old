package be.simonraes.dotadata.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.holograph.Line;
import be.simonraes.dotadata.holograph.LineGraph;
import be.simonraes.dotadata.holograph.LinePoint;
import be.simonraes.dotadata.statistics.GraphStats;
import be.simonraes.dotadata.util.AppPreferences;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.MatchUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

/**
 * Created by Simon Raes on 18/04/2014.
 */
public class GraphFragment extends Fragment implements LineGraph.OnPointClickedListener {

    private LineGraph li;
    private TextView txtTopX, txtMidX, txtBottomX, txtLeftY, txtRightY;
    private ArrayList<DetailMatchLite> matches;
    private ArrayList<GraphStats> statsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graph_fragment, null);
        li = (LineGraph) view.findViewById(R.id.lineGraph);
        txtTopX = (TextView) view.findViewById(R.id.txtGraphTopX);
        txtMidX = (TextView) view.findViewById(R.id.txtGraphMidX);
        txtBottomX = (TextView) view.findViewById(R.id.txtGraphBottomX);
        txtLeftY = (TextView) view.findViewById(R.id.txtGraphLeftY);
        txtRightY = (TextView) view.findViewById(R.id.txtGraphRightY);

        getActivity().setTitle("Winrate (wip)");
        setHasOptionsMenu(true);

        //update active drawer item
        ((DrawerController) getActivity()).setActiveDrawerItem(5);

        TreeMap<String, GraphStats> mapStats = new TreeMap<String, GraphStats>();

        MatchesDataSource mds = new MatchesDataSource(getActivity(), AppPreferences.getAccountID(getActivity()));
        matches = mds.getAllRealDetailMatchesLite();

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


                if (mapStats.containsKey(date)) {
                    GraphStats stats = mapStats.get(date);
                    stats.setNumberOfGames(stats.getNumberOfGames() + 1);
                    if (MatchUtils.isUser_win(match)) {
                        stats.setWins(stats.getWins() + 1);
                    } else {
                        stats.setLosses(stats.getLosses() + 1);
                    }

                } else {
                    GraphStats newStats = new GraphStats();
                    newStats.setNumberOfGames(1);
                    newStats.setDateString(date);
                    if (MatchUtils.isUser_win(match)) {
                        newStats.setWins(1);
                    } else {
                        newStats.setLosses(1);
                    }
                    mapStats.put(date, newStats);
                }
            }

            statsList = new ArrayList<GraphStats>(mapStats.values());


            //set the first element
            statsList.get(0).setWinrateCalc(((double) statsList.get(0).getWins() / (double) statsList.get(0).getNumberOfGames()) * 100);
            statsList.get(0).setNumberOfGamesCalc(statsList.get(0).getNumberOfGames());

            //calculate the others
            for (int i = 1; i < statsList.size(); i++) {

                //set current item's winrate and number of games
                statsList.get(i).setWinrateCalc(((double) statsList.get(i).getWins() / (double) statsList.get(i).getNumberOfGames()) * 100);
                statsList.get(i).setNumberOfGamesCalc(statsList.get(i).getNumberOfGames());

                int numberOfGamesCombined = statsList.get(i - 1).getNumberOfGamesCalc() + statsList.get(i).getNumberOfGamesCalc();

                double winrateCombined = ((statsList.get(i - 1).getWinrateCalc() * statsList.get(i - 1).getNumberOfGamesCalc())
                        + (statsList.get(i).getWinrateCalc() * statsList.get(i).getNumberOfGamesCalc()))
                        / numberOfGamesCombined;

                //save newly calculated data
                statsList.get(i).setWinrateCalc(winrateCombined);
                statsList.get(i).setNumberOfGamesCalc(numberOfGamesCombined);

                //print out combined winrate
//            System.out.println(statsList.get(i).getDateString() + " - " + statsList.get(i).getWinrateCalc());
            }


            createGraph();

        }
        return view;
    }

    @Override
    public void onClick(int lineIndex, int pointIndex) {
        //graph point 0 does not come from the statsList
        if (pointIndex > 0) {
            Toast.makeText(getActivity(), Conversions.roundDouble(statsList.get(pointIndex - 1).getWinrateCalc(), 2) + "% "
                    + "(" + statsList.get(pointIndex - 1).getDateString() + ")", Toast.LENGTH_SHORT).show();
        }

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
            spinHeroes.setVisible(false);
        }
        MenuItem spinGameModes = menu.findItem(R.id.spinGameModes);
        if (spinGameModes != null) {
            spinGameModes.setVisible(false);
        }
    }

    private void createGraph() {
        li.setOnPointClickedListener(this);
        li.setUsingDips(true);

        Line l = new Line();

        //add 1 startpoint so the graph starts at X 0
        LinePoint p;
        p = new LinePoint();
        p.setX(0);
        p.setY(50);
        l.addPoint(p);


        int counter = 1;

        for (GraphStats stats : statsList) {

            p = new LinePoint();
            p.setX(counter);
            p.setY(stats.getWinrateCalc());
            l.addPoint(p);

            counter++;

            l.setColor(Color.parseColor("#B8DC70"));
        }

        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(Long.parseLong(matches.get(matches.size() - 1).getStart_time()) * 1000);

        System.out.println("first date is " + cal.get(Calendar.YEAR) + " " + cal.get(Calendar.MONTH) + " " + cal.get(Calendar.DAY_OF_MONTH));

        //todo: should use date instead of list index for X
        // aka
        //todo: needs to loop over all weeks between first date and last and generate a graphstats object for each week
        //then check if the object is not null -> place line point
        //if null, place nothing, leave empty spot on that X point


        li.addLine(l);

        li.setLineToFill(0);

        double maxWinrate = 0;
        double minWinrate = 100;

        System.out.println(statsList.size());
        System.out.println(statsList.size() * .75);

        for (int i = (int) (statsList.size() * .25); i < statsList.size(); i++) {
            System.out.println("winrate of element " + i + " is " + statsList.get(i).getWinrateCalc());

            if (statsList.get(i).getWinrateCalc() > maxWinrate) {
                System.out.println("replacing max " + maxWinrate + " with " + statsList.get(i).getWinrateCalc());
                maxWinrate = statsList.get(i).getWinrateCalc();
            }
            if (statsList.get(i).getWinrateCalc() < minWinrate) {
                System.out.println("replacing min " + minWinrate + " with " + statsList.get(i).getWinrateCalc());

                minWinrate = statsList.get(i).getWinrateCalc();
            }
        }

        double offset = 0;
        if (maxWinrate - 50 > 50 - minWinrate) {
            offset = maxWinrate - 50;
        } else {
            offset = 50 - minWinrate;
        }
        maxWinrate = 50 + offset;
        minWinrate = 50 - offset;

        li.setRangeY((float) Math.floor(minWinrate), (float) Math.ceil(maxWinrate));

        txtTopX.setText(Double.toString(Math.ceil(maxWinrate)));
        txtBottomX.setText(Double.toString(Math.floor(minWinrate)));

        Double midRate = (Math.ceil(maxWinrate) + Math.floor(minWinrate)) / 2;
        txtMidX.setText(Double.toString(Conversions.roundDouble(midRate, 2)));

        txtLeftY.setText(statsList.get(0).getDateString());
        txtRightY.setText(statsList.get(statsList.size() - 1).getDateString());
    }
}
