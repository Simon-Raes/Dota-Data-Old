package be.simonraes.dotadata.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.detailmatch.DetailPlayer;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.GameModes;
import be.simonraes.dotadata.util.HeroList;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Simon on 18/02/14.
 */
public class RecentGamesAdapter extends ArrayAdapter<DetailMatch> {

    private Context context;
    private ArrayList<DetailMatch> matches;
    private String prefAccountID;

    public RecentGamesAdapter(Context context, ArrayList<DetailMatch> objects) {
        super(context, R.layout.matches_list_row, objects);
        this.context = context;
        this.matches = objects;

        prefAccountID = PreferenceManager.getDefaultSharedPreferences(context).getString("be.simonraes.dotadata.accountid", "");
    }

//    //can probably be deleted
//    @Override
//    public void notifyDataSetChanged() {
//        super.notifyDataSetChanged();
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DetailMatch match = matches.get(position);
        View view = convertView;
        final ViewHolder viewholder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.matches_list_row, parent, false);
            viewholder = new ViewHolder();
            viewholder.imgHero = (ImageView) view.findViewById(R.id.imgRowHero);
            viewholder.imgFavourite = (ImageView) view.findViewById(R.id.imgFavourite);
            viewholder.txtGameMode = (TextView) view.findViewById(R.id.txtRowGameMode);
            viewholder.txtDate = (TextView) view.findViewById(R.id.txtRowDate);
            viewholder.txtVictoryLoss = (TextView) view.findViewById(R.id.txtRowVictoryLoss);
            view.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) view.getTag();
        }

        if (match.getLobby_type().equals("7")) {
            //is ranked match
            viewholder.txtGameMode.setText(GameModes.getGameMode(match.getGame_mode()) + " (Ranked)");
        } else {
            viewholder.txtGameMode.setText(GameModes.getGameMode(match.getGame_mode()));
        }

        viewholder.txtDate.setText(Conversions.millisToDate(match.getStart_time()));


        //find the user's hero, set as image
        String playerHeroID = "1";
        for (DetailPlayer player : match.getPlayers()) {
            if (player.getAccount_id() != null) {
                if (player.getAccount_id().equals(prefAccountID)) {
                    playerHeroID = player.getHero_id();//
                }
            }
        }
        viewholder.imgHero.setImageResource(context.getResources().getIdentifier(HeroList.getHeroImageName(playerHeroID), "drawable", context.getPackageName()));

        if (match.isFavourite()) {

            viewholder.imgFavourite.setVisibility(View.VISIBLE);
        } else {
            //must be hidden again because this view can be re-used for other (non-favourited) matches
            viewholder.imgFavourite.setVisibility(View.GONE);
        }

        //set victory or defeat text
        if (match.isUser_win()) {
            viewholder.txtVictoryLoss.setText("Victory");
            viewholder.txtVictoryLoss.setTextColor(context.getResources().getColor(R.color.ForestGreen));
        } else {
            viewholder.txtVictoryLoss.setText("Defeat");
            viewholder.txtVictoryLoss.setTextColor(context.getResources().getColor(R.color.Crimson));
        }


        return view;
    }



    private class ViewHolder {
        public ImageView imgHero;
        public ImageView imgFavourite;
        public TextView txtGameMode;
        public TextView txtDate;
        public TextView txtVictoryLoss;
    }
}