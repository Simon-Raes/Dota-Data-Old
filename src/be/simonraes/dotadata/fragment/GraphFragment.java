package be.simonraes.dotadata.fragment;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
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
public class GraphFragment extends Fragment implements LineGraph.OnPointClickedListener {

    private LineGraph li;
    private TextView txtTopX, txtMidX, txtBottomX, txtLeftY, txtRightY;
    private ArrayList<DetailMatchLite> matches;
    private ArrayList<GraphStats> statsList, gpmStatsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graph_fragment, null);



        li = (LineGraph) view.findViewById(R.id.lineGraph);
        txtTopX = (TextView) view.findViewById(R.id.txtGraphTopX);
        txtMidX = (TextView) view.findViewById(R.id.txtGraphMidX);
        txtBottomX = (TextView) view.findViewById(R.id.txtGraphBottomX);
        txtLeftY = (TextView) view.findViewById(R.id.txtGraphLeftY);
        txtRightY = (TextView) view.findViewById(R.id.txtGraphRightY);

        getActivity().setTitle("Winrate (WIP)");
        setHasOptionsMenu(true);

        // Update active drawer item.
        ((DrawerController) getActivity()).setActiveDrawerItem(5);

        GraphStatsCalculator calculator = new GraphStatsCalculator(getActivity());
        statsList = calculator.getGraphStats();
        if (statsList != null && statsList.size() > 0) {
            if (OrientationHelper.getScreenOrientation(getActivity()) == Configuration.ORIENTATION_LANDSCAPE) {
                System.out.println("landscape");
            } else {
                System.out.println("portrait");
            }
            createGraph();
        }

        return view;
    }



    private void createPortraitLayout() {

    }

    private void createLandscapeLayout() {

    }

    /**Creates a Toast message with info about the clicked graph node.*/
    @Override
    public void onClick(int lineIndex, int pointIndex) {
        int numberOfNulls = 0;

        if (pointIndex > 0) {
            for (int i = 1; i < pointIndex+numberOfNulls; i++) {
                if (statsList.get(i) == null) {
                    numberOfNulls++;
                }
            }

            Toast.makeText(getActivity(), Conversions.roundDouble(statsList.get(pointIndex + numberOfNulls-1).getWinrateCumulative(), 2) + "% "
                    + "(" + statsList.get(pointIndex + numberOfNulls-1).getDateString() + ")", Toast.LENGTH_SHORT).show();
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

        for (int i = 0; i < statsList.size(); i++) {
            // Only create a graph node if their is info for that period, leave an empty space if not.
            if (statsList.get(i) != null) {
                System.out.println(i + ": " + statsList.get(i).getWeek() + "/" + statsList.get(i).getYear());
                p = new LinePoint();
                p.setX(i);
                p.setY(statsList.get(i).getWinrateCumulative());
                l.addPoint(p);
                l.setColor(Color.parseColor("#B8DC70"));
            }
        }

        li.addLine(l);

        li.setLineToFill(0);

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

        li.setRangeY((float) Math.floor(minWinrate), (float) Math.ceil(maxWinrate));

        // Set labels
        txtTopX.setText(Double.toString(Math.ceil(maxWinrate)));
        txtBottomX.setText(Double.toString(Math.floor(minWinrate)));

        Double midRate = (Math.ceil(maxWinrate) + Math.floor(minWinrate)) / 2;
        txtMidX.setText(Double.toString(Conversions.roundDouble(midRate, 2)));

        txtLeftY.setText(statsList.get(0).getDateString());
        txtRightY.setText(statsList.get(statsList.size() - 1).getDateString());
    }
}
