package be.simonraes.dotadata.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.liveleaguegame.LiveLeagueMatch;
import be.simonraes.dotadata.parser.SteamRemoteStorageParser;
import be.simonraes.dotadata.util.AnimateFirstDisplayListenerToo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

/**
 * Created by Simon on 4/02/14.
 */
public class LiveLeagueGamesAdapter extends ArrayAdapter<LiveLeagueMatch> {

    private Context context;
    private ArrayList<LiveLeagueMatch> matches;

    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListenerToo();

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


        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        viewholder.txtMatchID.setText(Html.fromHtml("<b>" + matches.get(position).getRadiantTeam().getTeam_name() + "</b>" + " vs " + "<b>" + matches.get(position).getDireTeam().getTeam_name() + "</b>"));

        SteamRemoteStorageParser logoParser;

        if (!matches.get(position).getRadiantTeam().getTeam_logo().equals("0")) {
            logoParser = new SteamRemoteStorageParser(viewholder.imgLogoRadiant);
            logoParser.execute(matches.get(position).getRadiantTeam().getTeam_logo());
        }
        if (!matches.get(position).getDireTeam().getTeam_logo().equals("0")) {
            logoParser = new SteamRemoteStorageParser(viewholder.imgLogoDire);
            logoParser.execute(matches.get(position).getDireTeam().getTeam_logo());
        }


        return view;
    }


    private class ViewHolder {
        public TextView txtMatchID;
        public ImageView imgLogoRadiant, imgLogoDire;
    }
}
