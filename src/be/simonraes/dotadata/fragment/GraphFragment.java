package be.simonraes.dotadata.fragment;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.holograph.Line;
import be.simonraes.dotadata.holograph.LineGraph;
import be.simonraes.dotadata.holograph.LinePoint;
import be.simonraes.dotadata.statistics.GraphStats;
import be.simonraes.dotadata.statistics.GraphStatsCalculator;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.OrientationHelper;

import java.util.ArrayList;

/**
 * Created by Simon Raes on 18/04/2014.
 */
public class GraphFragment extends Fragment implements LineGraph.OnPointClickedListener, GraphStatsCalculator.GraphStatsDelegate {

    private int GPM_GRAPH_OFFSET = 5;
    private int LH_GRAPH_OFFSET = 5;
    private int KDA_GRAPH_OFFSET = 1;

    private LineGraph graphKDA;
    private TextView txtTopX, txtMidX, txtBottomX, txtLeftY, txtRightY;
    private ArrayList<GraphStats> statsList;
    private LinearLayout layGraphs;
    private ProgressBar progressBarGraphs;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.graph_fragment, null);

        if (savedInstanceState != null) {
            statsList = savedInstanceState.getParcelableArrayList("statsList");
        }

        layGraphs = (LinearLayout) view.findViewById(R.id.layMainGraph);
        progressBarGraphs = (ProgressBar) view.findViewById(R.id.pbGraphs);

        graphKDA = (LineGraph) view.findViewById(R.id.lineGraph);
        txtTopX = (TextView) view.findViewById(R.id.txtGraphTopX);
        txtMidX = (TextView) view.findViewById(R.id.txtGraphMidX);
        txtBottomX = (TextView) view.findViewById(R.id.txtGraphBottomX);
        txtLeftY = (TextView) view.findViewById(R.id.txtGraphLeftY);
        txtRightY = (TextView) view.findViewById(R.id.txtGraphRightY);

        getActivity().setTitle("Graphs (WIP)");
        setHasOptionsMenu(true);

        // Update active drawer item.
        ((DrawerController) getActivity()).setActiveDrawerItem(5);

        // Get the data for the graphs.
        if (statsList != null && statsList.size() > 0) {
            setLayout();
        } else {
            layGraphs.setVisibility(View.GONE);
            progressBarGraphs.setVisibility(View.VISIBLE);

            GraphStatsCalculator calculator = new GraphStatsCalculator(this, getActivity());
            calculator.execute();
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("statsList", statsList);
    }

    private void setLayout() {
        layGraphs.setVisibility(View.VISIBLE);
        progressBarGraphs.setVisibility(View.GONE);

        if (OrientationHelper.getScreenOrientation(getActivity()) == Configuration.ORIENTATION_LANDSCAPE) {
            createWinrateGraph();
        } else {
            createWinrateGraph();
            createGPMGraph(view);
            createLHGraph(view);
            createKDAGraph(view);
        }
    }

    private void createPortraitLayout() {

    }

    private void createLandscapeLayout() {

    }

    /**
     * Creates a Toast message with info about the clicked graph node.
     */
    @Override
    public void onClick(String tag, int lineIndex, int pointIndex) {

        // Todo: these toasts display incorrect info.

        // The values list has more points than the graph (it also contains the null values that aren't drawn), account for that here.
        int numberOfNulls = 0;

        if (pointIndex > 0) {
            for (int i = 1; i < pointIndex + numberOfNulls; i++) {
                if (statsList.get(i) == null) {
                    numberOfNulls++;
                }
            }
        }

        // Display message for the clicked graph.
        if (tag.equals("Winrate")) {
            Toast.makeText(getActivity(), Conversions.roundDouble(statsList.get(pointIndex + numberOfNulls - 1).getWinrateCumulative(), 2) + "% "
                    + "(" + statsList.get(pointIndex + numberOfNulls - 1).getDateString() + ")", Toast.LENGTH_SHORT).show();
        } else if (tag.equals("GPM")) {
            Toast.makeText(getActivity(), Conversions.roundDouble(statsList.get(pointIndex + numberOfNulls - 1).getGpmAveragedCumulative(), 0)
                    + "GPM (" + statsList.get(pointIndex + numberOfNulls - 1).getDateString() + ")", Toast.LENGTH_SHORT).show();
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

    private void createWinrateGraph() {
        graphKDA.setOnPointClickedListener(this);
        graphKDA.setTag("Winrate");
        graphKDA.setUsingDips(true);

        Line l = new Line();

        //add 1 startpoint so the graph starts at X 0
        LinePoint p;
//        p = new LinePoint();
//        p.setX(0);
//        p.setY(50);
//        l.addPoint(p);

        for (int i = 0; i < statsList.size(); i++) {
            // Only create a graph node if there is info for that period, leave an empty space if not.
            if (statsList.get(i) != null) {
                p = new LinePoint();
                p.setX(i);
                p.setY(statsList.get(i).getWinrateCumulative());
                l.addPoint(p);
                l.setColor(Color.parseColor("#B8DC70"));
            }
        }

        graphKDA.addLine(l);
        graphKDA.setLineToFill(0);

        double maxWinrate = 0;
        double minWinrate = 100;

        // Determine graph Y-min and Y-max
        // (only take the last 75% of values into account because the first 25 will be going up and down a lot)
        for (int i = (int) (statsList.size() * .25); i < statsList.size(); i++) {
            if (statsList.get(i) != null) {
                if (statsList.get(i).getWinrateCumulative() > maxWinrate) {
                    maxWinrate = statsList.get(i).getWinrateCumulative();
                }
                if (statsList.get(i).getWinrateCumulative() < minWinrate) {
                    minWinrate = statsList.get(i).getWinrateCumulative();
                }
            }
        }

        double offset;
        if (maxWinrate - 50 > 50 - minWinrate) {
            offset = maxWinrate - 50;
        } else {
            offset = 50 - minWinrate;
        }
        maxWinrate = 50 + offset;
        minWinrate = 50 - offset;

        graphKDA.setRangeY((float) Math.floor(minWinrate), (float) Math.ceil(maxWinrate));

        // Set labels
        txtTopX.setText(Double.toString(Math.ceil(maxWinrate)));
        txtBottomX.setText(Double.toString(Math.floor(minWinrate)));

        Double midRate = (Math.ceil(maxWinrate) + Math.floor(minWinrate)) / 2;
        txtMidX.setText(Double.toString(Conversions.roundDouble(midRate, 2)));

        txtLeftY.setText(statsList.get(0).getDateString());
        txtRightY.setText(statsList.get(statsList.size() - 1).getDateString());
    }


    private void createGPMGraph(View view) {

        graphKDA = (LineGraph) view.findViewById(R.id.lineGraphGPM);
        txtTopX = (TextView) view.findViewById(R.id.txtGraphTopXGPM);
        txtMidX = (TextView) view.findViewById(R.id.txtGraphMidXGPM);
        txtBottomX = (TextView) view.findViewById(R.id.txtGraphBottomXGPM);
        txtLeftY = (TextView) view.findViewById(R.id.txtGraphLeftYGPM);
        txtRightY = (TextView) view.findViewById(R.id.txtGraphRightYGPM);

        graphKDA.setOnPointClickedListener(this);
        graphKDA.setTag("GPM");
        graphKDA.setUsingDips(true);

        Line l = new Line();
        LinePoint p;

        for (int i = 0; i < statsList.size(); i++) {
            // Only create a graph node if there is info for that period, leave an empty space if not.
            if (statsList.get(i) != null) {
                p = new LinePoint();
                p.setX(i);
                p.setY(statsList.get(i).getGpmAveragedCumulative());
                p.setColor("#FFBE00");
                l.addPoint(p);
                l.setColor(Color.parseColor("#FFD700"));
            }
        }

        graphKDA.addLine(l);
        graphKDA.setLineToFill(0);

        double maxGpm = 0;
        double minGpm = 100000;

        // Determine graph Y-min and Y-max
        for (int i = 0; i < statsList.size(); i++) {
            if (statsList.get(i) != null) {
                if (statsList.get(i).getGpmAveragedCumulative() > maxGpm) {
                    maxGpm = statsList.get(i).getGpmAveragedCumulative();
                }
                if (statsList.get(i).getGpmAveragedCumulative() < minGpm) {
                    minGpm = statsList.get(i).getGpmAveragedCumulative();
                }
            }
        }

        maxGpm += GPM_GRAPH_OFFSET;
        minGpm -= GPM_GRAPH_OFFSET;

        graphKDA.setRangeY((float) Math.floor(minGpm), (float) Math.ceil(maxGpm));

        // Set labels
        txtTopX.setText(Double.toString(Math.ceil(maxGpm)));
        txtBottomX.setText(Double.toString(Math.floor(minGpm)));

        Double midRate = (Math.ceil(maxGpm) + Math.floor(minGpm)) / 2;
        txtMidX.setText(Double.toString(Conversions.roundDouble(midRate, 2)));

        txtLeftY.setText(statsList.get(0).getDateString());
        txtRightY.setText(statsList.get(statsList.size() - 1).getDateString());
    }


    private void createLHGraph(View view) {

        graphKDA = (LineGraph) view.findViewById(R.id.lineGraphLH);
        txtTopX = (TextView) view.findViewById(R.id.txtGraphTopXLH);
        txtMidX = (TextView) view.findViewById(R.id.txtGraphMidXLH);
        txtBottomX = (TextView) view.findViewById(R.id.txtGraphBottomXLH);
        txtLeftY = (TextView) view.findViewById(R.id.txtGraphLeftYLH);
        txtRightY = (TextView) view.findViewById(R.id.txtGraphRightYLH);

        graphKDA.setOnPointClickedListener(this);
        graphKDA.setTag("LH");
        graphKDA.setUsingDips(true);

        Line l = new Line();
        LinePoint p;

        for (int i = 0; i < statsList.size(); i++) {
            // Only create a graph node if there is info for that period, leave an empty space if not.
            if (statsList.get(i) != null) {
                p = new LinePoint();
                p.setX(i);
                p.setY(statsList.get(i).getLastHitsAveragedCumulative());
                p.setColor("#FFBE00");
                l.addPoint(p);
                l.setColor(Color.parseColor("#FFD700"));
            }
        }

        graphKDA.addLine(l);
        graphKDA.setLineToFill(0);

        double maxLH = 0;
        double minLH = 100000;

        // Determine graph Y-min and Y-max
        for (int i = 0; i < statsList.size(); i++) {
            if (statsList.get(i) != null) {
                if (statsList.get(i).getLastHitsAveragedCumulative() > maxLH) {
                    maxLH = statsList.get(i).getLastHitsAveragedCumulative();
                }
                if (statsList.get(i).getLastHitsAveragedCumulative() < minLH) {
                    minLH = statsList.get(i).getLastHitsAveragedCumulative();
                }
            }
        }

        maxLH += LH_GRAPH_OFFSET;
        minLH -= LH_GRAPH_OFFSET;

        graphKDA.setRangeY((float) Math.floor(minLH), (float) Math.ceil(maxLH));

        // Set labels
        txtTopX.setText(Double.toString(Math.ceil(maxLH)));
        txtBottomX.setText(Double.toString(Math.floor(minLH)));

        Double midRate = (Math.ceil(maxLH) + Math.floor(minLH)) / 2;
        txtMidX.setText(Double.toString(Conversions.roundDouble(midRate, 2)));

        txtLeftY.setText(statsList.get(0).getDateString());
        txtRightY.setText(statsList.get(statsList.size() - 1).getDateString());
    }

    private void createKDAGraph(View view) {
        graphKDA = (LineGraph) view.findViewById(R.id.lineGraphKDA);
        txtTopX = (TextView) view.findViewById(R.id.txtGraphTopXKDA);
        txtMidX = (TextView) view.findViewById(R.id.txtGraphMidXKDA);
        txtBottomX = (TextView) view.findViewById(R.id.txtGraphBottomXKDA);
        txtLeftY = (TextView) view.findViewById(R.id.txtGraphLeftYKDA);
        txtRightY = (TextView) view.findViewById(R.id.txtGraphRightYKDA);

        graphKDA.setOnPointClickedListener(this);
        graphKDA.setTag("KDA");
        graphKDA.setUsingDips(true);

        Line lineAssists = new Line();
        LinePoint p;

        for (int i = 0; i < statsList.size(); i++) {
            // Only create a graph node if there is info for that period, leave an empty space if not.
            if (statsList.get(i) != null) {
                p = new LinePoint();
                p.setX(i);
                p.setY(statsList.get(i).getAssistsAveragedCumulative());
                p.setColor("#FFBE00");
                lineAssists.addPoint(p);
                lineAssists.setColor(Color.parseColor("#FFD700"));
            }
        }

        Line lineDeaths = new Line();

        for (int i = 0; i < statsList.size(); i++) {
            // Only create a graph node if there is info for that period, leave an empty space if not.
            if (statsList.get(i) != null) {
                p = new LinePoint();
                p.setX(i);
                p.setY(statsList.get(i).getDeathsAveragedCumulative());
                p.setColor("#B0311E");
                lineDeaths.addPoint(p);
                lineDeaths.setColor(Color.parseColor("#B0311E"));
            }
        }

        Line lineKills = new Line();

        for (int i = 0; i < statsList.size(); i++) {
            // Only create a graph node if there is info for that period, leave an empty space if not.
            if (statsList.get(i) != null) {
                p = new LinePoint();
                p.setX(i);
                p.setY(statsList.get(i).getKillsAveragedCumulative());
                p.setColor("#668042");
                lineKills.addPoint(p);
                lineKills.setColor(Color.parseColor("#668042"));
            }
        }

        graphKDA.addLine(lineAssists);
        graphKDA.addLine(lineDeaths);
        graphKDA.addLine(lineKills);
        graphKDA.setLineToFill(0);


        double maxKDA = 0;
        double minKDA = 100000;

        // Determine graph Y-min and Y-max
        for (int i = 0; i < statsList.size(); i++) {
            if (statsList.get(i) != null) {
                if (statsList.get(i).getKillsAveragedCumulative() > maxKDA) {
                    maxKDA = statsList.get(i).getKillsAveragedCumulative();
                }
                if (statsList.get(i).getDeathsAveragedCumulative() > maxKDA) {
                    maxKDA = statsList.get(i).getDeathsAveragedCumulative();
                }
                if (statsList.get(i).getAssistsAveragedCumulative() > maxKDA) {
                    maxKDA = statsList.get(i).getAssistsAveragedCumulative();
                }

                if (statsList.get(i).getKillsAveragedCumulative() < minKDA) {
                    minKDA = statsList.get(i).getKillsAveragedCumulative();
                }
                if (statsList.get(i).getDeathsAveragedCumulative() < minKDA) {
                    minKDA = statsList.get(i).getDeathsAveragedCumulative();
                }
                if (statsList.get(i).getAssistsAveragedCumulative() < minKDA) {
                    minKDA = statsList.get(i).getAssistsAveragedCumulative();
                }
            }
        }

        maxKDA += KDA_GRAPH_OFFSET;
        minKDA -= KDA_GRAPH_OFFSET;
        if (minKDA < 0) {
            minKDA = 0;
        }

        graphKDA.setRangeY((float) Math.floor(minKDA), (float) Math.ceil(maxKDA));

        // Set labels
        txtTopX.setText(Double.toString(Math.ceil(maxKDA)));
        txtBottomX.setText(Double.toString(Math.floor(minKDA)));

        Double midRate = (Math.ceil(maxKDA) + Math.floor(minKDA)) / 2;
        txtMidX.setText(Double.toString(Conversions.roundDouble(midRate, 2)));

        txtLeftY.setText(statsList.get(0).getDateString());
        txtRightY.setText(statsList.get(statsList.size() - 1).getDateString());
    }

    /**
     * Receives graph data.
     */
    @Override
    public void matchesLoaded(ArrayList<GraphStats> list) {
        statsList = list;
        if (statsList != null && statsList.size() > 0) {
            setLayout();
        }
    }
}
