package be.simonraes.dotadata.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import be.simonraes.dotadata.teamlogo.TeamLogoContainer;
import be.simonraes.dotadata.util.AnimateFirstDisplayListenerToo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

            viewholder.imgLogoRadiant = (ImageView) view.findViewById(R.id.imgLiveLeagueRowRadiantLogo);
            viewholder.imgLogoDire = (ImageView) view.findViewById(R.id.imgLiveLeagueRowDireLogo);
            view.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) view.getTag();
        }


        viewholder.txtMatchID.setText(Html.fromHtml("<b>" + matches.get(position).getRadiantTeam().getTeam_name() + "</b>" + " vs " + "<b>" + matches.get(position).getDireTeam().getTeam_name() + "</b>"));

//        SteamRemoteStorageParser logoParser;
//

        viewholder.position = position;


        if (!matches.get(position).getRadiantTeam().getTeam_logo().equals("0")) {
            ThumbnailTask task = new ThumbnailTask(position, viewholder);
            task.execute(matches.get(position).getRadiantTeam().getTeam_logo(), "radiant");
        }
        if (!matches.get(position).getDireTeam().getTeam_logo().equals("0")) {
            ThumbnailTask task = new ThumbnailTask(position, viewholder);
            task.execute(matches.get(position).getDireTeam().getTeam_logo(), "dire");
        }


        return view;
    }


    private static class ThumbnailTask extends AsyncTask<String, Void, TeamLogoContainer> {
        private int mPosition;
        private ViewHolder mHolder;
        private String team;

        public ThumbnailTask(int position, ViewHolder holder) {
            mPosition = position;
            mHolder = holder;
        }

        @Override
        protected TeamLogoContainer doInBackground(String... params) {
            ObjectMapper mapper = new ObjectMapper();

            String ugcid = params[0];
            team = params[1];

            TeamLogoContainer container = new TeamLogoContainer();

            try {
                container = mapper.readValue(new URL("http://api.steampowered.com/ISteamRemoteStorage/GetUGCFileDetails/v1/?key=EB5773FAAF039592D9383FA104EEA55D&appid=570&ugcid=" + ugcid), TeamLogoContainer.class);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return container;
        }

        @Override
        protected void onPostExecute(TeamLogoContainer container) {
            if (mHolder.position == mPosition) {
                ImageLoader imageLoader = ImageLoader.getInstance();
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .resetViewBeforeLoading(true)
                        .cacheInMemory(true)
                        .showImageOnLoading(R.drawable.item_lg_unknown)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .build();
                AnimateFirstDisplayListenerToo animateFirstListener = new AnimateFirstDisplayListenerToo();

                if (container.getTeamlogo() != null) {
                    if (team.equals("radiant")) {
                        imageLoader.displayImage(container.getTeamlogo().getUrl(), mHolder.imgLogoRadiant, options, animateFirstListener);

                    } else {
                        imageLoader.displayImage(container.getTeamlogo().getUrl(), mHolder.imgLogoDire, options, animateFirstListener);
                    }
                }
            }
        }
    }


    private class ViewHolder {
        public int position;
        public TextView txtMatchID;
        public ImageView imgLogoRadiant, imgLogoDire;
    }
}
