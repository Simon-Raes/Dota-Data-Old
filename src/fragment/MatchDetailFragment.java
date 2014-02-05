package fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.simonraes.dotadata.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import detailmatch.DetailMatch;
import detailmatch.DetailPlayer;
import util.Conversions;
import util.GameModes;
import util.HeroList;
import util.ItemList;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Simon on 30/01/14.
 */
public class MatchDetailFragment extends Fragment {

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener;

    private DetailMatch match;

    private TextView txtMatchID,txtWinner, txtGameMode, txtDuration;

    private LinearLayout layPlayersRadiant, layPlayersDire;
    private TextView txtPlayerName, txtPlayerKDA, txtPlayerLHDenies, txtPlayerGPMXPM;
    private ImageView imgHero, imgItem1, imgItem2, imgItem3, imgItem4, imgItem5, imgItem6;

    private boolean isRanked=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matchdetails_layout, container, false);

        match = (DetailMatch) getArguments().getSerializable("detailmatch");

        if(match.getLobby_type().equals("7")){
            isRanked = true;
        }

        getActivity().getActionBar().setTitle("Match Details");

        //GEBRUIK EEN HOLDER

        //Match info
        txtMatchID = (TextView) view.findViewById(R.id.txtDetailMatchID);
        txtMatchID.setText("Match ID: "+match.getMatch_id());

        txtGameMode = (TextView) view.findViewById(R.id.txtDetailGameMode);
        txtGameMode.setText(GameModes.getGameMode(match.getGame_mode()));
        if(isRanked){
            txtGameMode.setText(GameModes.getGameMode(match.getGame_mode())+" (Ranked)");
        }

        txtDuration = (TextView) view.findViewById(R.id.txtDetailDuration);
        txtDuration.setText("Duration: "+ Conversions.secondsToTime(match.getDuration()));

        txtWinner = (TextView) view.findViewById(R.id.txtDetailWinner);
        if (match.getRadiant_win()) {
            txtWinner.setText("Radiant Victory");
        } else {
            txtWinner.setText("Dire Victory");
        }

        //Players info
        layPlayersRadiant = (LinearLayout) view.findViewById(R.id.layDetailRadiantPlayers);
        layPlayersDire = (LinearLayout) view.findViewById(R.id.layDetailDirePlayers);

        LayoutInflater inflate = getActivity().getLayoutInflater();
        View playerRow;

        for (DetailPlayer d : match.getPlayers()) {
            playerRow = inflate.inflate(R.layout.matchdetails_player_row, null);

            txtPlayerName = (TextView) playerRow.findViewById(R.id.txtDetailPlayerName);
            txtPlayerName.setText(d.getAccount_id());

            txtPlayerKDA = (TextView) playerRow.findViewById(R.id.txtDetailKDA);
            txtPlayerKDA.setText(d.getKills()+"/"+d.getDeaths()+"/"+d.getAssists());

            txtPlayerLHDenies = (TextView) playerRow.findViewById(R.id.txtDetailLHDenies);
            txtPlayerLHDenies.setText(d.getLast_hits()+"/"+d.getDenies());

            txtPlayerGPMXPM = (TextView) playerRow.findViewById(R.id.txtDetailGPMXPM);
            txtPlayerGPMXPM.setText(d.getGold_per_min()+"/"+d.getXp_per_min());

            //images
            imageLoader = ImageLoader.getInstance();

            //heroloading options
            options = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true)
                    .cacheInMemory(true)

                    .showImageOnLoading(R.drawable.hero_sb_loading)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();
            animateFirstListener = new AnimateFirstDisplayListener();

            imgHero = (ImageView) playerRow.findViewById(R.id.imgDetailHero);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/"+ HeroList.getHeroImageName(d.getHero_id())+"_sb.png", imgHero, options, animateFirstListener);

            //itemloading options
            options = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true)
                    .cacheInMemory(true)
                    .showImageOnLoading(R.drawable.item_lg_loading)
                    .build();

            imgItem1 = (ImageView) playerRow.findViewById(R.id.imgItem1);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/items/" + ItemList.getItem(d.getItem_0()) + "_lg.png", imgItem1, options, animateFirstListener);

            imgItem2 = (ImageView) playerRow.findViewById(R.id.imgItem2);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/items/" + ItemList.getItem(d.getItem_1()) + "_lg.png", imgItem2, options, animateFirstListener);

            imgItem3 = (ImageView) playerRow.findViewById(R.id.imgItem3);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/items/" + ItemList.getItem(d.getItem_2()) + "_lg.png", imgItem3, options, animateFirstListener);

            imgItem4 = (ImageView) playerRow.findViewById(R.id.imgItem4);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/items/" + ItemList.getItem(d.getItem_3()) + "_lg.png", imgItem4, options, animateFirstListener);

            imgItem5 = (ImageView) playerRow.findViewById(R.id.imgItem5);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/items/" + ItemList.getItem(d.getItem_4()) + "_lg.png", imgItem5, options, animateFirstListener);

            imgItem6 = (ImageView) playerRow.findViewById(R.id.imgItem6);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/items/" + ItemList.getItem(d.getItem_5()) + "_lg.png", imgItem6, options, animateFirstListener);

            if (Integer.parseInt(d.getPlayer_slot()) < 5) {
                layPlayersRadiant.addView(playerRow);
            } else {
                layPlayersDire.addView(playerRow);
            }
        }

        //Picks & bans - only shown if match has picks/bans
        if(isRanked){
            LinearLayout layPicksBans = (LinearLayout) view.findViewById(R.id.layDetailPicksBans);
            layPicksBans.setVisibility(View.VISIBLE);
        }

        //Minimap info
        //tower and barrack status

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

}
