package be.simonraes.dotadata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.util.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;


/**
 * Created by Simon on 18/02/14.
 */
public class RecentGamesAdapter extends ArrayAdapter<DetailMatchLite> {

    private Context context;
    private ArrayList<DetailMatchLite> matches;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener;

    public RecentGamesAdapter(Context context, ArrayList<DetailMatchLite> objects) {
        super(context, R.layout.matches_list_row, objects);
        this.context = context;
        this.matches = objects;

        imageLoader = ImageLoader.getInstance();
        animateFirstListener = new AnimateFirstDisplayListenerToo();
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .showImageOnLoading(R.drawable.hero_loading_lg)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DetailMatchLite match = matches.get(position);
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

        //viewholder.imgHero.setImageResource(context.getResources().getIdentifier(HeroList.getHeroImageName(match.getHero_id()), "drawable", context.getPackageName()));

        imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(match.getHero_id()) + "_lg.png", viewholder.imgHero, options, animateFirstListener);


        if (match.isFavourite()) {

            viewholder.imgFavourite.setVisibility(View.VISIBLE);
        } else {
            //must be hidden again because this view can be re-used for other (non-favourited) matches
            viewholder.imgFavourite.setVisibility(View.GONE);
        }

        if (MatchUtils.isUser_win(match)) {
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