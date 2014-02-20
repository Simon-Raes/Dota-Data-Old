package be.simonraes.dotadata.adapter;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
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

//    private DisplayImageOptions options;
//    private ImageLoader imageLoader;
//    private ImageLoadingListener animateFirstListener;

    String prefAccountID;

    public RecentGamesAdapter(Context context, ArrayList<DetailMatch> objects) {
        super(context, R.layout.historygames_row, objects);
        this.context = context;
        this.matches = objects;


        prefAccountID = PreferenceManager.getDefaultSharedPreferences(context).getString("be.simonraes.dotadata.accountid", "");


//        options = new DisplayImageOptions.Builder()
//                .resetViewBeforeLoading(true)
//                .cacheInMemory(true)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .showImageOnLoading(R.drawable.item_lg_loading)
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .build();

//        animateFirstListener = new AnimateFirstDisplayListener();
    }

    //can probably be deleted
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DetailMatch match = matches.get(position);
        View view = convertView;
        final ViewHolder viewholder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.historygames_row, parent, false);
            viewholder = new ViewHolder();
            viewholder.imgHero = (ImageView) view.findViewById(R.id.imgRowHero);
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

//        imageLoader = ImageLoader.getInstance();

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


        //set victory or defeat text
        if (match.isUser_win()) {
            viewholder.txtVictoryLoss.setText("Victory");
            viewholder.txtVictoryLoss.setTextColor(context.getResources().getColor(R.color.ForestGreen));
        } else {
            viewholder.txtVictoryLoss.setText("Defeat");
            viewholder.txtVictoryLoss.setTextColor(context.getResources().getColor(R.color.Crimson));
        }

        //old
        //imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(playerHeroID) + "_hphover.png", viewholder.imgHero, options, animateFirstListener);

        return view;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    private class ViewHolder {
        public ImageView imgHero;
        public TextView txtGameMode;
        public TextView txtDate;
        public TextView txtVictoryLoss;

    }
}