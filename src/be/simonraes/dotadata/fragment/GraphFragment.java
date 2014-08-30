package be.simonraes.dotadata.fragment;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.*;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.adapter.RankingSpinnerAdapter;
import be.simonraes.dotadata.holograph.Line;
import be.simonraes.dotadata.holograph.LineGraph;
import be.simonraes.dotadata.holograph.LinePoint;
import be.simonraes.dotadata.statistics.GraphStats;
import be.simonraes.dotadata.statistics.GraphStatsCalculator;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.OrientationHelper;

import java.util.ArrayList;

/**
 * Fragment that display graphs based on the stored games.
 * Uses 2 layouts: layout/graph_fragment and layout-lang/graph_fragment.
 * Created by Simon Raes on 18/04/2014.
 */
@SuppressWarnings("FieldCanBeLocal")
public class GraphFragment extends Fragment implements LineGraph.OnPointClickedListener, GraphStatsCalculator.GraphStatsDelegate, AdapterView.OnItemSelectedListener {

    private final int GPM_GRAPH_OFFSET = 5;
    private final int LH_GRAPH_OFFSET = 5;
    private final int KDA_GRAPH_OFFSET = 1;

    private final String COLOR_KILLS = "#668042";
    private final String COLOR_DEATHS = "#B0311E";
    private final String COLOR_ASSISTS = "#F4A460";


    private LineGraph lineGraph;
    private TextView txtTopX, txtMidX, txtBottomX, txtLeftY, txtRightY;
    private ArrayList<GraphStats> statsList;
    private LinearLayout layGraphs;
    private ProgressBar progressBarGraphs;
    private View view;

    private Spinner spinnerGraphs;
    private int spinnerSelectedIndex = -1;
    long lastUpdate = 0; // Prevent the spinner from firing during view creation.


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            statsList = savedInstanceState.getParcelableArrayList("statsList");
            spinnerSelectedIndex = savedInstanceState.getInt("spinnerSelectedIndex");
        }
        lastUpdate = System.currentTimeMillis();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.graph_fragment, null);

        layGraphs = (LinearLayout) view.findViewById(R.id.layMainGraph);
        progressBarGraphs = (ProgressBar) view.findViewById(R.id.pbGraphs);

        getActivity().setTitle("Graphs");
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
        outState.putInt("spinnerSelectedIndex", spinnerSelectedIndex);
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

        MenuItem spinGraphs = menu.findItem(R.id.spinGraphs);
        if (OrientationHelper.getScreenOrientation(getActivity()) == Configuration.ORIENTATION_LANDSCAPE) {
            spinGraphs.setVisible(true);
            spinnerGraphs = (Spinner) spinGraphs.getActionView();
            if (spinnerGraphs != null) {

                spinnerGraphs.setAdapter(new RankingSpinnerAdapter(getActivity(), new String[]{"Winrate", "GPM", "Last hits", "KDA"}));
                spinnerGraphs.setOnItemSelectedListener(this);

                if (spinnerSelectedIndex >= 0) {
                    spinnerGraphs.setSelection(spinnerSelectedIndex);
                }
            }
        } else {
            spinGraphs.setVisible(false);
        }
        lastUpdate = System.currentTimeMillis();
    }


    /**
     * Spinner item selected
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (System.currentTimeMillis() - lastUpdate > 100) {
            setLargeGraph();
            spinnerSelectedIndex = spinnerGraphs.getSelectedItemPosition();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private void setLayout() {
        layGraphs.setVisibility(View.VISIBLE);
        progressBarGraphs.setVisibility(View.GONE);

        if (OrientationHelper.getScreenOrientation(getActivity()) == Configuration.ORIENTATION_LANDSCAPE) {
            setWinrateGraphActive();
            createWinrateGraph();
        } else {
            setWinrateGraphActive();
            createWinrateGraph();

            setGpmGraphActive();
            createGPMGraph();

            setLhGraphActive();
            createLHGraph();

            setKdaGraphActive();
            createKDAGraph();
        }
    }

    private void setWinrateGraphActive() {
        lineGraph = (LineGraph) view.findViewById(R.id.lineGraph);
        setLineGraphSettings();
        txtTopX = (TextView) view.findViewById(R.id.txtGraphTopX);
        txtMidX = (TextView) view.findViewById(R.id.txtGraphMidX);
        txtBottomX = (TextView) view.findViewById(R.id.txtGraphBottomX);
        txtLeftY = (TextView) view.findViewById(R.id.txtGraphLeftY);
        txtRightY = (TextView) view.findViewById(R.id.txtGraphRightY);
    }

    private void setGpmGraphActive() {
        lineGraph = (LineGraph) view.findViewById(R.id.lineGraphGPM);
        setLineGraphSettings();
        txtTopX = (TextView) view.findViewById(R.id.txtGraphTopXGPM);
        txtMidX = (TextView) view.findViewById(R.id.txtGraphMidXGPM);
        txtBottomX = (TextView) view.findViewById(R.id.txtGraphBottomXGPM);
        txtLeftY = (TextView) view.findViewById(R.id.txtGraphLeftYGPM);
        txtRightY = (TextView) view.findViewById(R.id.txtGraphRightYGPM);
    }

    private void setLhGraphActive() {
        lineGraph = (LineGraph) view.findViewById(R.id.lineGraphLH);
        setLineGraphSettings();
        txtTopX = (TextView) view.findViewById(R.id.txtGraphTopXLH);
        txtMidX = (TextView) view.findViewById(R.id.txtGraphMidXLH);
        txtBottomX = (TextView) view.findViewById(R.id.txtGraphBottomXLH);
        txtLeftY = (TextView) view.findViewById(R.id.txtGraphLeftYLH);
        txtRightY = (TextView) view.findViewById(R.id.txtGraphRightYLH);
    }

    private void setKdaGraphActive() {
        lineGraph = (LineGraph) view.findViewById(R.id.lineGraphKDA);
        setLineGraphSettings();
        txtTopX = (TextView) view.findViewById(R.id.txtGraphTopXKDA);
        txtMidX = (TextView) view.findViewById(R.id.txtGraphMidXKDA);
        txtBottomX = (TextView) view.findViewById(R.id.txtGraphBottomXKDA);
        txtLeftY = (TextView) view.findViewById(R.id.txtGraphLeftYKDA);
        txtRightY = (TextView) view.findViewById(R.id.txtGraphRightYKDA);
    }

    private void setLineGraphSettings() {
        lineGraph.removeAllLines();
        lineGraph.setOnPointClickedListener(this);
        lineGraph.setUsingDips(true);
        lineGraph.setLineToFill(0);
    }

    /**
     * Creates a Toast message with info about the clicked graph node.
     */
    @Override
    public void onClick(String tag, int lineIndex, int pointIndex) {

        GraphStats selectedGraphStat = null;
        for (GraphStats stat : statsList) {
            if (stat != null && stat.getSequenceNumber() == pointIndex) {
                selectedGraphStat = stat;
                break;
            }
        }

        // Display message for the clicked graph.
        if (selectedGraphStat != null) {
            String toastText = "";
            if (tag.equals("Winrate")) {
                toastText = Conversions.roundDouble(selectedGraphStat.getWinrateCumulative(), 2) + "%";
            } else if (tag.equals("GPM")) {
                toastText = Conversions.roundDouble(selectedGraphStat.getGpmAveragedCumulative(), 0) + " GPM";
            } else if (tag.equals("LH")) {
                toastText = Conversions.roundDouble(selectedGraphStat.getLastHitsAveragedCumulative(), 2) + " last hits";
            } else if (tag.equals("KDA")) {
                switch (lineIndex) {
                    case 0:
                        toastText = Conversions.roundDouble(selectedGraphStat.getAssistsAveragedCumulative(), 2) + " assists";
                        break;
                    case 1:
                        toastText = Conversions.roundDouble(selectedGraphStat.getDeathsAveragedCumulative(), 2) + " deaths";
                        break;
                    case 2:
                        toastText = Conversions.roundDouble(selectedGraphStat.getKillsAveragedCumulative(), 2) + " kills";
                        break;
                }
            }
            toastText += " (" + selectedGraphStat.getDateString() + ")";
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
        }
    }

    private void setLargeGraph() {
        setWinrateGraphActive();
        switch (spinnerGraphs.getSelectedItemPosition()) {
            case 0:
                createWinrateGraph();
                break;
            case 1:
                createGPMGraph();
                break;
            case 2:
                createLHGraph();
                break;
            case 3:
                createKDAGraph();
                break;
        }
    }

    private void createWinrateGraph() {

        lineGraph.setTag("Winrate");

        Line l = new Line();
        LinePoint p;

        for (int i = 0; i < statsList.size(); i++) {
            // Only create a graph node if there is info for that period, leave an empty space if not.
            if (statsList.get(i) != null) {
                p = new LinePoint();
                p.setX(i);
                p.setY(statsList.get(i).getWinrateCumulative());
                p.setColor("#536D36");
                l.addPoint(p);
                l.setColor(Color.parseColor("#668042"));
            }
        }

        lineGraph.addLine(l);

        double maxWin = 0;
        double minWin = 100;

        // Determine graph Y-min and Y-max
        // (only take the last 75% of values into account because the first 25 will be going up and down a lot)
        for (int i = (int) (statsList.size() * .25); i < statsList.size(); i++) {
            if (statsList.get(i) != null) {
                if (statsList.get(i).getWinrateCumulative() > maxWin) {
                    maxWin = statsList.get(i).getWinrateCumulative();
                }
                if (statsList.get(i).getWinrateCumulative() < minWin) {
                    minWin = statsList.get(i).getWinrateCumulative();
                }
            }
        }

        double offset;
        if (maxWin - 50 > 50 - minWin) {
            offset = maxWin - 50;
        } else {
            offset = 50 - minWin;
        }
        maxWin = 50 + offset;
        minWin = 50 - offset;

        lineGraph.setRangeY((float) Math.floor(minWin), (float) Math.ceil(maxWin));

        // Set labels
        String maxWinrate = Double.toString(Math.ceil(maxWin));
        Double midRate = (Math.ceil(maxWin) + Math.floor(minWin)) / 2;
        String midWinrate = Double.toString(Conversions.roundDouble(midRate, 2));
        String minWinrate = Double.toString(Math.floor(minWin));
        String minDate = statsList.get(0).getDateString();
        String maxDate = statsList.get(statsList.size() - 1).getDateString();

        setLabels(maxWinrate, midWinrate, minWinrate, minDate, maxDate);
    }


    private void createGPMGraph() {

        lineGraph.setTag("GPM");

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

        lineGraph.addLine(l);

        double maxGpm = 0;
        double minGpm = 100000;

        // Determine graph Y-min and Y-max
        for (GraphStats stat : statsList) {
            if (stat != null) {
                if (stat.getGpmAveragedCumulative() > maxGpm) {
                    maxGpm = stat.getGpmAveragedCumulative();
                }
                if (stat.getGpmAveragedCumulative() < minGpm) {
                    minGpm = stat.getGpmAveragedCumulative();
                }
            }
        }

        maxGpm += GPM_GRAPH_OFFSET;
        minGpm -= GPM_GRAPH_OFFSET;

        lineGraph.setRangeY((float) Math.floor(minGpm), (float) Math.ceil(maxGpm));

        // Set labels
        Double midRate = (Math.ceil(maxGpm) + Math.floor(minGpm)) / 2;

        setLabels(Double.toString(Math.ceil(maxGpm)),
                Double.toString(Conversions.roundDouble(midRate, 2)),
                Double.toString(Math.floor(minGpm)),
                statsList.get(0).getDateString(),
                statsList.get(statsList.size() - 1).getDateString());
    }


    private void createLHGraph() {

        lineGraph.setTag("LH");

        Line l = new Line();
        LinePoint p;

        for (int i = 0; i < statsList.size(); i++) {
            // Only create a graph node if there is info for that period, leave an empty space if not.
            if (statsList.get(i) != null) {
                p = new LinePoint();
                p.setX(i);
                p.setY(statsList.get(i).getLastHitsAveragedCumulative());
                p.setColor("#4169E1");
                l.addPoint(p);
                l.setColor(Color.parseColor("#4682B4"));
            }
        }

        lineGraph.addLine(l);

        double maxLH = 0;
        double minLH = 100000;

        // Determine graph Y-min and Y-max
        for (GraphStats stat : statsList) {
            if (stat != null) {
                if (stat.getLastHitsAveragedCumulative() > maxLH) {
                    maxLH = stat.getLastHitsAveragedCumulative();
                }
                if (stat.getLastHitsAveragedCumulative() < minLH) {
                    minLH = stat.getLastHitsAveragedCumulative();
                }
            }
        }

        maxLH += LH_GRAPH_OFFSET;
        minLH -= LH_GRAPH_OFFSET;

        lineGraph.setRangeY((float) Math.floor(minLH), (float) Math.ceil(maxLH));

        // Set labels
        Double midRate = (Math.ceil(maxLH) + Math.floor(minLH)) / 2;

        setLabels(Double.toString(Math.ceil(maxLH)),
                Double.toString(Conversions.roundDouble(midRate, 2)),
                Double.toString(Math.floor(minLH)),
                statsList.get(0).getDateString(),
                statsList.get(statsList.size() - 1).getDateString());
    }

    private void createKDAGraph() {

        lineGraph.setTag("KDA");

        if(OrientationHelper.getScreenOrientation(getActivity()) == Configuration.ORIENTATION_PORTRAIT){
            // Also set the header for the KDA graph
            TextView txtKdaHeader = (TextView) view.findViewById(R.id.txtGraphHeaderKDA);
            String headerText = "<font color=\""+COLOR_KILLS+"\">Kills</font>" + ", " +
                    "<font color=\""+COLOR_DEATHS+"\">Deaths</font>" + " and " +
                    "<font color=\""+COLOR_ASSISTS+"\">Assists</font>";
            txtKdaHeader.setText(Html.fromHtml(headerText));
        }

        Line lineAssists = new Line();
        LinePoint p;

        for (int i = 0; i < statsList.size(); i++) {
            // Only create a graph node if there is info for that period, leave an empty space if not.
            if (statsList.get(i) != null) {
                p = new LinePoint();
                p.setX(i);
                p.setY(statsList.get(i).getAssistsAveragedCumulative());
                p.setColor(COLOR_ASSISTS);
                lineAssists.addPoint(p);
                lineAssists.setColor(Color.parseColor(COLOR_ASSISTS));
            }
        }

        Line lineDeaths = new Line();

        for (int i = 0; i < statsList.size(); i++) {
            // Only create a graph node if there is info for that period, leave an empty space if not.
            if (statsList.get(i) != null) {
                p = new LinePoint();
                p.setX(i);
                p.setY(statsList.get(i).getDeathsAveragedCumulative());
                p.setColor(COLOR_DEATHS);
                lineDeaths.addPoint(p);
                lineDeaths.setColor(Color.parseColor(COLOR_DEATHS));
            }
        }

        Line lineKills = new Line();

        for (int i = 0; i < statsList.size(); i++) {
            // Only create a graph node if there is info for that period, leave an empty space if not.
            if (statsList.get(i) != null) {
                p = new LinePoint();
                p.setX(i);
                p.setY(statsList.get(i).getKillsAveragedCumulative());
                p.setColor(COLOR_KILLS);
                lineKills.addPoint(p);
                lineKills.setColor(Color.parseColor(COLOR_KILLS));
            }
        }

        lineGraph.addLine(lineAssists);
        lineGraph.addLine(lineDeaths);
        lineGraph.addLine(lineKills);

        double maxKDA = 0;
        double minKDA = 100000;

        // Determine graph Y-min and Y-max
        for (GraphStats stat : statsList) {
            if (stat != null) {
                if (stat.getKillsAveragedCumulative() > maxKDA) {
                    maxKDA = stat.getKillsAveragedCumulative();
                }
                if (stat.getDeathsAveragedCumulative() > maxKDA) {
                    maxKDA = stat.getDeathsAveragedCumulative();
                }
                if (stat.getAssistsAveragedCumulative() > maxKDA) {
                    maxKDA = stat.getAssistsAveragedCumulative();
                }

                if (stat.getKillsAveragedCumulative() < minKDA) {
                    minKDA = stat.getKillsAveragedCumulative();
                }
                if (stat.getDeathsAveragedCumulative() < minKDA) {
                    minKDA = stat.getDeathsAveragedCumulative();
                }
                if (stat.getAssistsAveragedCumulative() < minKDA) {
                    minKDA = stat.getAssistsAveragedCumulative();
                }
            }
        }

        maxKDA += KDA_GRAPH_OFFSET;
        minKDA -= KDA_GRAPH_OFFSET;
        if (minKDA < 0) {
            minKDA = 0;
        }

        lineGraph.setRangeY((float) Math.floor(minKDA), (float) Math.ceil(maxKDA));

        // Set labels
        Double midRate = (Math.ceil(maxKDA) + Math.floor(minKDA)) / 2;

        setLabels(Double.toString(Math.ceil(maxKDA)), Double.toString(Conversions.roundDouble(midRate, 2)), Double.toString(Math.floor(minKDA)),
                statsList.get(0).getDateString(), statsList.get(statsList.size() - 1).getDateString());
    }

    /**
     * Sets the labels for the active graph.
     */
    private void setLabels(String topX, String midX, String bottomX, String leftY, String rightY) {
        txtTopX.setText(topX);
        txtMidX.setText(midX);
        txtBottomX.setText(bottomX);

        txtLeftY.setText(leftY);
        txtRightY.setText(rightY);


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
