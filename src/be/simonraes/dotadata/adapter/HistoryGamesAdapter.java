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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import be.simonraes.dotadata.historymatch.HistoryMatch;
import be.simonraes.dotadata.historymatch.HistoryPlayer;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.GameModes;
import be.simonraes.dotadata.util.HeroList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Simon on 25/01/14.
 */
public class HistoryGamesAdapter extends ArrayAdapter<HistoryMatch> {

    private Context context;
    private ArrayList<HistoryMatch> matches;

    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private ImageLoadingListener animateFirstListener;

    String prefAccountID;

    public HistoryGamesAdapter(Context context, ArrayList<HistoryMatch> objects) {
        super(context, R.layout.historygames_row, objects);
        this.context = context;
        this.matches = objects;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        prefAccountID = sharedPref.getString("be.simonraes.dotadata.accountid", "");

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.item_lg_loading)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        animateFirstListener = new AnimateFirstDisplayListener();
    }

    //can probably be deleted
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder viewholder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.historygames_row, parent, false);
            viewholder = new ViewHolder();
            viewholder.txtGameMode = (TextView) view.findViewById(R.id.txtLobbyType);
            viewholder.txtDate = (TextView) view.findViewById(R.id.txtDate);
            viewholder.imgHero = (ImageView) view.findViewById(R.id.imgHero);
            view.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) view.getTag();
        }

        viewholder.txtGameMode.setText(GameModes.getLobbyType(matches.get(position).getLobby_type()));
        viewholder.txtDate.setText(Conversions.millisToDate(matches.get(position).getStart_time()));
        imageLoader = ImageLoader.getInstance();

        String playerHeroID = "1";
        for (HistoryPlayer hp : matches.get(position).getPlayers()) {
            if (hp.getAccount_id() != null) {
                if (hp.getAccount_id().equals(prefAccountID)) {
                    playerHeroID = hp.getHero_id();
                }
            }

        }

        imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(playerHeroID) + "_hphover.png", viewholder.imgHero, options, animateFirstListener);
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
        public TextView txtGameMode;
        public TextView txtDate;
        public ImageView imgHero;
    }
}
