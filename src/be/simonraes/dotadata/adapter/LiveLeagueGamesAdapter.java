package be.simonraes.dotadata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.liveleaguegame.LiveLeagueMatch;

import java.util.ArrayList;

/**
 * Created by Simon on 4/02/14.
 */
public class LiveLeagueGamesAdapter extends ArrayAdapter<LiveLeagueMatch> {

    private Context context;
    private ArrayList<LiveLeagueMatch> matches;

    public LiveLeagueGamesAdapter(Context context, ArrayList<LiveLeagueMatch> objects) {
        super(context, R.layout.liveleaguegames_row, objects);
        this.context = context;
        this.matches = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder viewholder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.liveleaguegames_row, parent, false);
            viewholder = new ViewHolder();
            viewholder.txtMatchID = (TextView) view.findViewById(R.id.txtGameID);

            //todo: get team logo from http://dev.dota2.com/archive/index.php/t-71363.html
            viewholder.imgLogoRadiant = (ImageView) view.findViewById(R.id.imgLiveLeagueRowRadiantLogo);
            viewholder.imgLogoDire = (ImageView) view.findViewById(R.id.imgLiveLeagueRowDireLogo);
            view.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) view.getTag();
        }

        viewholder.txtMatchID.setText(matches.get(position).getRadiantTeam().getTeam_name() + " vs " + matches.get(position).getDireTeam().getTeam_name());

        return view;
    }


    private class ViewHolder {
        public TextView txtMatchID;
        public ImageView imgLogoRadiant, imgLogoDire;
    }
}
