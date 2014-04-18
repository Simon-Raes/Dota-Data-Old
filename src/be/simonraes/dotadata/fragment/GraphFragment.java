package be.simonraes.dotadata.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.holograph.Line;
import be.simonraes.dotadata.holograph.LineGraph;
import be.simonraes.dotadata.holograph.LinePoint;

/**
 * Created by Simon Raes on 18/04/2014.
 */
public class GraphFragment extends Fragment implements LineGraph.OnPointClickedListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graph_fragment, null);
        LineGraph li = (LineGraph) view.findViewById(R.id.lineGraph);
        li.setOnPointClickedListener(this);
        li.setUsingDips(true);
        Line l = new Line();
        LinePoint p = new LinePoint();
        p.setX(0);
        p.setY(5);
        l.addPoint(p);

        p = new LinePoint();
        p.setX(8);
        p.setY(8);
        l.addPoint(p);

        p = new LinePoint();
        p.setX(10);
        p.setY(4);
        l.addPoint(p);

        l.setColor(Color.parseColor("#FFBB33"));


        li.addLine(l);
        li.setRangeY(0, 10);
        li.setLineToFill(0);

        return view;
    }

    @Override
    public void onClick(int lineIndex, int pointIndex) {
        Toast.makeText(getActivity(), Integer.toString(lineIndex) + " " + Integer.toString(pointIndex), Toast.LENGTH_SHORT).show();
    }
}
