package be.simonraes.dotadata.fragment;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.detailmatch.DetailPlayer;
import be.simonraes.dotadata.detailmatch.PicksBans;
import be.simonraes.dotadata.util.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by Simon on 30/01/14.
 * Sets layout to show details of a match
 */
public class MatchDetailFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener {

    private View viewB;
    private LayoutInflater inflaterB;

    private DetailMatch match;

    private FrameLayout layDetailsMinimap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matchdetails_layout, container, false);
        inflaterB = inflater;
        viewB = view;

        getActivity().getActionBar().setTitle("Match Details");

        match = (DetailMatch) getArguments().getSerializable("be/simonraes/dotadata/detailmatch");

        boolean isRanked = false;
        if (match != null) {
            if (match.getLobby_type().equals("7")) {
                isRanked = true;
            }
        }

        //holder?

        //Match info
        TextView txtMatchID = (TextView) view.findViewById(R.id.txtDetailMatchID);
        txtMatchID.setText("Match ID: " + match.getMatch_id());

        TextView txtGameMode = (TextView) view.findViewById(R.id.txtDetailGameMode);
        txtGameMode.setText(GameModes.getGameMode(match.getGame_mode()));
        if (isRanked) {
            txtGameMode.setText(GameModes.getGameMode(match.getGame_mode()) + " (Ranked)");
        }

        TextView txtDuration = (TextView) view.findViewById(R.id.txtDetailDuration);
        txtDuration.setText("Duration: " + Conversions.secondsToTime(match.getDuration()));

        TextView txtWinner = (TextView) view.findViewById(R.id.txtDetailWinner);
        if (match.getRadiant_win()) {
            txtWinner.setText("Radiant Victory");
        } else {
            txtWinner.setText("Dire Victory");
        }

        //Players info
        LinearLayout layPlayersRadiant = (LinearLayout) view.findViewById(R.id.layDetailRadiantPlayers);
        LinearLayout layPlayersDire = (LinearLayout) view.findViewById(R.id.layDetailDirePlayers);


        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options;
        View playerRow;
        ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListenerToo();

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.hero_sb_loading)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        int numRadiantPlayers = 0;
        int numDirePlayers = 0;

        for (DetailPlayer d : match.getPlayers()) {
            playerRow = inflater.inflate(R.layout.matchdetails_player_row, null);

            TextView txtPlayerName = (TextView) playerRow.findViewById(R.id.txtDetailPlayerName);
            txtPlayerName.setText(d.getAccount_id());

            TextView txtPlayerKDA = (TextView) playerRow.findViewById(R.id.txtDetailKDA);
            txtPlayerKDA.setText(d.getKills() + "/" + d.getDeaths() + "/" + d.getAssists());

            TextView txtPlayerLHDenies = (TextView) playerRow.findViewById(R.id.txtDetailLHDenies);
            txtPlayerLHDenies.setText(d.getLast_hits() + "/" + d.getDenies());

            TextView txtPlayerGPMXPM = (TextView) playerRow.findViewById(R.id.txtDetailGPMXPM);
            txtPlayerGPMXPM.setText(d.getGold_per_min() + "/" + d.getXp_per_min());

            options = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true)
                    .cacheInMemory(true)
                    .showImageOnLoading(R.drawable.hero_sb_loading)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();

            ImageView imgHero = (ImageView) playerRow.findViewById(R.id.imgDetailHero);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(d.getHero_id()) + "_sb.png", imgHero, options, animateFirstListener);

            //item image loading options
            options = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true)
                    .cacheInMemory(true)
                    .showImageOnLoading(R.drawable.item_lg_loading)
                    .build();

            ImageView imgItem = (ImageView) playerRow.findViewById(R.id.imgItem1);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/items/" + ItemList.getItem(d.getItem_0()) + "_lg.png", imgItem, options, animateFirstListener);

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem2);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/items/" + ItemList.getItem(d.getItem_1()) + "_lg.png", imgItem, options, animateFirstListener);

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem3);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/items/" + ItemList.getItem(d.getItem_2()) + "_lg.png", imgItem, options, animateFirstListener);

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem4);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/items/" + ItemList.getItem(d.getItem_3()) + "_lg.png", imgItem, options, animateFirstListener);

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem5);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/items/" + ItemList.getItem(d.getItem_4()) + "_lg.png", imgItem, options, animateFirstListener);

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem6);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/items/" + ItemList.getItem(d.getItem_5()) + "_lg.png", imgItem, options, animateFirstListener);


            if (Integer.parseInt(d.getPlayer_slot()) < 5) {
                layPlayersRadiant.addView(playerRow);
                numRadiantPlayers++;
            } else {
                layPlayersDire.addView(playerRow);
                numDirePlayers++;
            }
        }
        if (numRadiantPlayers == 0) {
            layPlayersRadiant.setVisibility(View.GONE);
        }

        if (numDirePlayers == 0) {
            layPlayersDire.setVisibility(View.GONE);
        }

        //Picks & bans - only shown if match has picks/bans
        if (isRanked) {
            LinearLayout layPicksBans = (LinearLayout) view.findViewById(R.id.layDetailPicksBans);
            layPicksBans.setVisibility(View.VISIBLE);


            for (PicksBans pb : match.getPicks_bans()) {

                options = new DisplayImageOptions.Builder()
                        .resetViewBeforeLoading(true)
                        .cacheInMemory(true)
                        .showImageOnLoading(R.drawable.hero_sb_loading)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .build();

                RelativeLayout layPBLeft = (RelativeLayout) inflaterB.inflate(R.layout.pickban_left, null);
                RelativeLayout layPBRight = (RelativeLayout) inflaterB.inflate(R.layout.pickban_right, null);
                ImageView imgPBHeroLeft = (ImageView) layPBLeft.findViewById(R.id.imgPickBanLeft);
                TextView txtPBLeft = (TextView) layPBLeft.findViewById(R.id.txtPickBanLeft);
                ImageView imgPBHeroRight = (ImageView) layPBRight.findViewById(R.id.imgPickBanRight);
                TextView txtPBRight = (TextView) layPBRight.findViewById(R.id.txtPickBanRight);

                if (pb.getTeam().equals("0")) {
                    if (pb.isIs_pick()) {
                        txtPBLeft.setText("PICK");
                    } else {
                        txtPBLeft.setText("BAN");
                    }
                    imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(pb.getHero_id()) + "_sb.png", imgPBHeroLeft, options, animateFirstListener);
                    layPicksBans.addView(layPBLeft);
                } else {
                    if (pb.isIs_pick()) {
                        txtPBRight.setText("PICK");
                    } else {
                        txtPBRight.setText("BAN");
                    }
                    imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(pb.getHero_id()) + "_sb.png", imgPBHeroRight, options, animateFirstListener);
                    layPicksBans.addView(layPBRight);
                }
            }

        }

        //add listener to retrieve height and width of minimap layout
        layDetailsMinimap = (FrameLayout) view.findViewById(R.id.layDetailsMinimap);
        layDetailsMinimap.getViewTreeObserver().addOnGlobalLayoutListener(this);

        return view;
    }


    //Add towers and barracks to minimap

    @Override
    public void onGlobalLayout() {

        //remove listener so this method only gets called once
        //different versions depending on android version (before or after API 16)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            layDetailsMinimap.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        } else {
            layDetailsMinimap.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }

        //get minimap size
        int x = layDetailsMinimap.getWidth();
        int y = layDetailsMinimap.getHeight();

        //add towers to minimap
        //todo: T4 and barracks


        TowerStatus twrRadiant = Conversions.towerStatusFromString(match.getTower_status_radiant());
        TowerStatus twrDire = Conversions.towerStatusFromString(match.getTower_status_dire());

        if (twrRadiant.isTopT1()) {
            View towerRadiantTopT1 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantTopT1.setPadding((int) Math.round(x * 0.11), (int) Math.round(y * 0.38), 0, 0);
            layDetailsMinimap.addView(towerRadiantTopT1);
        }

        if (twrRadiant.isTopT2()) {
            View towerRadiantTopT2 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantTopT2.setPadding((int) Math.round(x * 0.11), (int) Math.round(y * 0.55), 0, 0);
            layDetailsMinimap.addView(towerRadiantTopT2);
        }

        if (twrRadiant.isTopT3()) {
            View towerRadiantTopT3 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantTopT3.setPadding((int) Math.round(x * 0.075), (int) Math.round(y * 0.7), 0, 0);
            layDetailsMinimap.addView(towerRadiantTopT3);
        }

        if (twrRadiant.isMidT1()) {
            View towerRadiantMidT1 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantMidT1.setPadding((int) Math.round(x * 0.40), (int) Math.round(y * 0.58), 0, 0);
            layDetailsMinimap.addView(towerRadiantMidT1);
        }

        if (twrRadiant.isMidT2()) {
            View towerRadiantMidT2 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantMidT2.setPadding((int) Math.round(x * 0.28), (int) Math.round(y * 0.66), 0, 0);
            layDetailsMinimap.addView(towerRadiantMidT2);
        }

        if (twrRadiant.isMidT3()) {
            View towerRadiantMidT3 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantMidT3.setPadding((int) Math.round(x * 0.20), (int) Math.round(y * 0.75), 0, 0);
            layDetailsMinimap.addView(towerRadiantMidT3);
        }

        if (twrRadiant.isBotT1()) {
            View towerRadiantBotT1 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantBotT1.setPadding((int) Math.round(x * 0.80), (int) Math.round(y * 0.87), 0, 0);
            layDetailsMinimap.addView(towerRadiantBotT1);
        }
        if (twrRadiant.isBotT2()) {
            View towerRadiantBotT2 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantBotT2.setPadding((int) Math.round(x * 0.47), (int) Math.round(y * 0.88), 0, 0);
            layDetailsMinimap.addView(towerRadiantBotT2);
        }
        if (twrRadiant.isBotT3()) {
            View towerRadiantBotT3 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantBotT3.setPadding((int) Math.round(x * 0.25), (int) Math.round(y * 0.88), 0, 0);
            layDetailsMinimap.addView(towerRadiantBotT3);
        }
        if (twrRadiant.isTopT4()) {
            View towerRadiantTopT4 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantTopT4.setPadding((int) Math.round(x * 0.15), (int) Math.round(y * 0.82), 0, 0);
            layDetailsMinimap.addView(towerRadiantTopT4);
        }
        if (twrRadiant.isBotT4()) {
            View towerRadiantBotT4 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantBotT4.setPadding((int) Math.round(x * 0.13), (int) Math.round(y * 0.80), 0, 0);
            layDetailsMinimap.addView(towerRadiantBotT4);
        }


        if (twrDire.isTopT1()) {
            View towerDireTopT1 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireTopT1.setPadding((int) Math.round(x * 0.20), (int) Math.round(y * 0.12), 0, 0);
            layDetailsMinimap.addView(towerDireTopT1);
        }
        if (twrDire.isTopT2()) {
            View towerDireTopT2 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireTopT2.setPadding((int) Math.round(x * 0.48), (int) Math.round(y * 0.12), 0, 0);
            layDetailsMinimap.addView(towerDireTopT2);
        }
        if (twrDire.isTopT3()) {
            View towerDireTopT3 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireTopT3.setPadding((int) Math.round(x * 0.70), (int) Math.round(y * 0.13), 0, 0);
            layDetailsMinimap.addView(towerDireTopT3);
        }

        if (twrDire.isMidT1()) {
            View towerDireMidT1 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireMidT1.setPadding((int) Math.round(x * 0.54), (int) Math.round(y * 0.48), 0, 0);
            layDetailsMinimap.addView(towerDireMidT1);
        }
        if (twrDire.isMidT2()) {
            View towerDireMidT2 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireMidT2.setPadding((int) Math.round(x * 0.63), (int) Math.round(y * 0.35), 0, 0);
            layDetailsMinimap.addView(towerDireMidT2);
        }
        if (twrDire.isMidT3()) {
            View towerDireMidT3 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireMidT3.setPadding((int) Math.round(x * 0.74), (int) Math.round(y * 0.26), 0, 0);
            layDetailsMinimap.addView(towerDireMidT3);
        }

        if (twrDire.isBotT1()) {
            View towerDireBotT1 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireBotT1.setPadding((int) Math.round(x * 0.85), (int) Math.round(y * 0.62), 0, 0);
            layDetailsMinimap.addView(towerDireBotT1);
        }
        if (twrDire.isBotT2()) {
            View towerDireBotT2 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireBotT2.setPadding((int) Math.round(x * 0.87), (int) Math.round(y * 0.49), 0, 0);
            layDetailsMinimap.addView(towerDireBotT2);
        }
        if (twrDire.isBotT3()) {
            View towerDireBotT3 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireBotT3.setPadding((int) Math.round(x * 0.87), (int) Math.round(y * 0.31), 0, 0);
            layDetailsMinimap.addView(towerDireBotT3);
        }
        if (twrDire.isTopT4()) {
            View towerDireTopT4 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireTopT4.setPadding((int) Math.round(x * 0.80), (int) Math.round(y * 0.18), 0, 0);
            layDetailsMinimap.addView(towerDireTopT4);
        }
        if (twrDire.isBotT4()) {
            View towerDireBotT4 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireBotT4.setPadding((int) Math.round(x * 0.82), (int) Math.round(y * 0.20), 0, 0);
            layDetailsMinimap.addView(towerDireBotT4);
        }
    }
}
