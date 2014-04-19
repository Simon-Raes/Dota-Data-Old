package be.simonraes.dotadata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.statistics.HeroStats;
import be.simonraes.dotadata.util.AnimateFirstDisplayListenerToo;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.HeroList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

/**
 * Created by Simon Raes on 18/04/2014.
 */
public class HeroStatsGamesPlayedAdapter extends ArrayAdapter<HeroStats> {

    private Context context;
    private ArrayList<HeroStats> heroes;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener;

    private int higestGames = 0;

    public HeroStatsGamesPlayedAdapter(Context context, ArrayList<HeroStats> objects) {
        super(context, R.layout.winrate_hero_row, objects);
        this.context = context;
        this.heroes = objects;

        for (HeroStats stats : objects) {
            if (stats.getNumberOfGames() > higestGames) {
                higestGames = stats.getNumberOfGames();
            }
        }

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
        HeroStats heroStats = heroes.get(position);
        View view = convertView;
        final ViewHolder viewholder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.winrate_hero_row, parent, false);
            viewholder = new ViewHolder();
            viewholder.imgHero = (ImageView) view.findViewById(R.id.imgWinrateHero);
            viewholder.txtHero = (TextView) view.findViewById(R.id.txtWinrateHero);
            viewholder.txtHeroTwo = (TextView) view.findViewById(R.id.txtWinrateHeroTwo);
            viewholder.layWin = (LinearLayout) view.findViewById(R.id.layWinrateBarWin);
            viewholder.layLoss = (LinearLayout) view.findViewById(R.id.layWinrateBarLoss);
            view.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) view.getTag();
        }

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT, heroStats.getNumberOfGames());
        viewholder.layWin.setLayoutParams(param);
        viewholder.layWin.setBackgroundColor(context.getResources().getColor(R.color.LightOrange));
        param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT, higestGames - heroStats.getNumberOfGames());
        viewholder.layLoss.setLayoutParams(param);

        imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(heroStats.getHero_id()) + "_lg.png", viewholder.imgHero, options, animateFirstListener);

        viewholder.txtHero.setText("Winrate: " + Double.toString(Conversions.roundDouble(heroStats.getWinrate(), 2)) + "%");
        viewholder.txtHeroTwo.setText("Games: " + heroStats.getNumberOfGames());

        return view;
    }


    private class ViewHolder {
        public ImageView imgHero;
        public TextView txtHero;
        public TextView txtHeroTwo;
        public LinearLayout layWin;
        public LinearLayout layLoss;
    }
}
