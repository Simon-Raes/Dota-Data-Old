package be.simonraes.dotadata.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.database.MatchesExtrasDataSource;
import be.simonraes.dotadata.delegates.ASyncResponsePlayerSummary;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.detailmatch.DetailPlayer;
import be.simonraes.dotadata.detailmatch.PicksBans;
import be.simonraes.dotadata.dialog.PlayerDetailsDialog;
import be.simonraes.dotadata.holograph.Line;
import be.simonraes.dotadata.holograph.LineGraph;
import be.simonraes.dotadata.holograph.LinePoint;
import be.simonraes.dotadata.parser.PlayerSummaryParser;
import be.simonraes.dotadata.playersummary.PlayerSummaryContainer;
import be.simonraes.dotadata.statistics.TeamExperienceGraphLoader;
import be.simonraes.dotadata.statistics.TeamExperienceStats;
import be.simonraes.dotadata.util.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Simon on 30/01/14.
 * Sets layout to show details of a match
 */
public class MatchDetailFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener, View.OnClickListener, ASyncResponsePlayerSummary, TeamExperienceGraphLoader.ASyncResponseTeamExperience {

    private LayoutInflater inflater;
    private View view;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener;

    private ArrayList<TextView> playerNames;
    private ArrayList<PlayerSummaryParser> parsers;
    private ArrayList<ImageView> playerAvatars;

    private DetailMatch match;
    private DetailPlayer activePlayer;

    private ImageButton btnDeleteNote;

    private FrameLayout layDetailsMinimap;

    private LineGraph lineGraphExperienceTeams;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("starting oncreateview");
        view = inflater.inflate(R.layout.matchdetails_layout, container, false);
        this.inflater = inflater;
        match = (DetailMatch) getArguments().getParcelable("be.simonraes.dotadata.detailmatch");

        setHasOptionsMenu(true);
        getActivity().setTitle("Match Details");

        //disable drawer icon (needed for reorientation)
        if (getActivity() instanceof DrawerController) {
            ((DrawerController) getActivity()).getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
            //update the actionbar to show the up carat
            if (getActivity() != null && getActivity().getActionBar() != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        playerNames = new ArrayList<TextView>();
        parsers = new ArrayList<PlayerSummaryParser>();
        playerAvatars = new ArrayList<ImageView>();

        //todo: maybe split some of these off to fragments and/or background loading
        setHeader();
        setNote();
        scrollScoreboard();
        setScoreboard();
        setPicksBans();
        setGraph();

        //add listener to retrieve height and width of minimap layout, will call onGlobalLayout()
        layDetailsMinimap = (FrameLayout) view.findViewById(R.id.layDetailsMinimap);
        if (layDetailsMinimap != null && layDetailsMinimap.getViewTreeObserver() != null) {
            layDetailsMinimap.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

        return view;
    }

    /*Sets the textviews containing match info.*/
    private void setHeader() {
        TextView txtMatchID = (TextView) view.findViewById(R.id.txtDetailMatchID);
        txtMatchID.setText("ID " + match.getMatch_id());

        TextView txtGameMode = (TextView) view.findViewById(R.id.txtDetailGameMode);
        txtGameMode.setText(GameModes.getGameMode(match.getGame_mode()));
        if (match.getLobby_type().equals("7")) {
            txtGameMode.setText(GameModes.getGameMode(match.getGame_mode()) + " (Ranked)");
        }

        TextView txtDuration = (TextView) view.findViewById(R.id.txtDetailDuration);
        txtDuration.setText(Conversions.secondsToTime(match.getDuration()));

        TextView txtDate = (TextView) view.findViewById(R.id.txtDetailDate);
        txtDate.setText(Conversions.millisToDate(match.getStart_time()));

        ImageView imgBackground = (ImageView) view.findViewById(R.id.imgDetailBackground);
        LinearLayout layHeader = (LinearLayout) view.findViewById(R.id.layDetailHeader);
        TextView txtWinner = (TextView) view.findViewById(R.id.txtDetailWinner);
        if (match.getRadiant_win()) {
            //todo: reduce filesize of backgrounds
            imgBackground.setImageResource(R.drawable.bg_radiant);
            layHeader.setGravity(Gravity.END);
            txtWinner.setText("Radiant Victory");
        } else {
            imgBackground.setImageResource(R.drawable.bg_dire);
            layHeader.setGravity(Gravity.START);
            txtWinner.setText("Dire Victory");
        }
    }

    /*Shows note if there is one.*/
    private void setNote() {
        if (match.getExtras().getNote() != null && !match.getExtras().getNote().equals("") && !match.getExtras().getNote().equals("null")) {
            RelativeLayout layNote = (RelativeLayout) view.findViewById(R.id.layDetailNote);
            layNote.setVisibility(View.VISIBLE);
            TextView txtNote = (TextView) view.findViewById(R.id.txtDetailNote);
            txtNote.setText(match.getExtras().getNote());
            btnDeleteNote = (ImageButton) view.findViewById(R.id.btnDetailDeleteNote);
            btnDeleteNote.setOnClickListener(this);
        }
    }

    /*Sets the info of all players (items, stats,...).*/
    private void setScoreboard() {
        //Players info
        LinearLayout layPlayersRadiant = (LinearLayout) view.findViewById(R.id.layDetailRadiantPlayers);
        LinearLayout layPlayersDire = (LinearLayout) view.findViewById(R.id.layDetailDirePlayers);

        imageLoader = ImageLoader.getInstance();
        animateFirstListener = new AnimateFirstDisplayListenerToo();
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.hero_sb_loading)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        int numRadiantPlayers = 0;
        int numDirePlayers = 0;

        View playerRow;
        for (int i = 0; i < match.getPlayers().size(); i++) {
            DetailPlayer player = match.getPlayers().get(i);
            playerRow = inflater.inflate(R.layout.matchdetails_player_row, null);

            View divider = inflater.inflate(R.layout.divider, null);

            //give user's row a special background color
            if (player.getAccount_id() != null) {
                if (player.getAccount_id().equals(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("be.simonraes.dotadata.accountid", ""))) {
                    if (playerRow != null) {
                        playerRow.setBackgroundColor(getResources().getColor(R.color.LighterSteelBlue));
                    }
                }
            }

            //hero level
            TextView txtPlayerLevel = (TextView) playerRow.findViewById(R.id.txtDetailPlayerLevel);
            txtPlayerLevel.setText(player.getLevel());

            //player name
            TextView txtPlayerName = (TextView) playerRow.findViewById(R.id.txtDetailPlayerName);
            if (txtPlayerName != null) {
                if (player.getLeaver_status().equals("2")) {
                    txtPlayerName.setTextColor(getActivity().getResources().getColor(R.color.Crimson));
                }
                txtPlayerName.setText(player.getAccount_id());
                playerNames.add(txtPlayerName);

                //start parser to get player's name
                if (InternetCheck.isOnline(getActivity())) {
                    //no need to parse for the anonymous account
                    if (player.getAccount_id().equals("4294967295")) {
                        txtPlayerName.setText("Anonymous");
                    } else {
                        PlayerSummaryParser parser = new PlayerSummaryParser(this);
                        parser.execute(player.getAccount_id());
                        parsers.add(parser);
                    }
                }
            }

            //player avatar
            ImageView imgPlayerAvatar = (ImageView) playerRow.findViewById(R.id.imgDetailPlayer);
            if (imgPlayerAvatar != null) {
                imgPlayerAvatar.setTag(player.getAccount_id());
                playerAvatars.add(imgPlayerAvatar);
            }

            TextView txtPlayerKDA = (TextView) playerRow.findViewById(R.id.txtDetailKDA);
            txtPlayerKDA.setText(player.getKills() + "/" + player.getDeaths() + "/" + player.getAssists());

            TextView txtPlayerLHDenies = (TextView) playerRow.findViewById(R.id.txtDetailLHDenies);
            txtPlayerLHDenies.setText(player.getLast_hits() + "/" + player.getDenies());

            TextView txtPlayerGPMXPM = (TextView) playerRow.findViewById(R.id.txtDetailGPMXPM);
            txtPlayerGPMXPM.setText(player.getGold_per_min() + "/" + player.getXp_per_min());

            options = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true)
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .showImageOnLoading(R.drawable.hero_sb_loading)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();

            ImageView imgHero = (ImageView) playerRow.findViewById(R.id.imgDetailHero);
            imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(player.getHero_id()) + "_sb.png", imgHero, options, animateFirstListener);
            options = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true)
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .showImageOnLoading(R.drawable.item_lg_unknown)
                    .showImageOnFail(R.drawable.item_lg_unknown)
                    .build();


            ImageView imgItem = (ImageView) playerRow.findViewById(R.id.imgItem1);
            setItemImage(imgItem, (player.getItem_0()));

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem2);
            setItemImage(imgItem, (player.getItem_1()));

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem3);
            setItemImage(imgItem, (player.getItem_2()));

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem4);
            setItemImage(imgItem, (player.getItem_3()));

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem5);
            setItemImage(imgItem, (player.getItem_4()));

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem6);
            setItemImage(imgItem, (player.getItem_5()));

            //Additional units (Lone Druid bear) items
            View bearRow = null;

            //Also checks if the hero isn't Meepo (bug in match data, Meepo shouldn't have additional units)
            if (player.getAdditional_units().size() > 0 && !player.getHero_id().equals("82")) {
                bearRow = inflater.inflate(R.layout.additional_units_items_row, null);
                if (bearRow != null) {
                    imgItem = (ImageView) bearRow.findViewById(R.id.imgItem1);
                    setItemImage(imgItem, player.getAdditional_units().get(0).getItem_0());

                    imgItem = (ImageView) bearRow.findViewById(R.id.imgItem2);
                    setItemImage(imgItem, player.getAdditional_units().get(0).getItem_1());

                    imgItem = (ImageView) bearRow.findViewById(R.id.imgItem3);
                    setItemImage(imgItem, player.getAdditional_units().get(0).getItem_2());

                    imgItem = (ImageView) bearRow.findViewById(R.id.imgItem4);
                    setItemImage(imgItem, player.getAdditional_units().get(0).getItem_3());

                    imgItem = (ImageView) bearRow.findViewById(R.id.imgItem5);
                    setItemImage(imgItem, player.getAdditional_units().get(0).getItem_4());

                    imgItem = (ImageView) bearRow.findViewById(R.id.imgItem6);
                    setItemImage(imgItem, player.getAdditional_units().get(0).getItem_5());
                }
            }

            playerRow.setTag(i);
            playerRow.setOnClickListener(this);

            //add player row to correct team
            if (Integer.parseInt(player.getPlayer_slot()) < 5) {
                layPlayersRadiant.addView(playerRow);
                if (bearRow != null) {
                    layPlayersRadiant.addView(bearRow);
                }
                layPlayersRadiant.addView(divider);
                numRadiantPlayers++;
            } else {
                layPlayersDire.addView(playerRow);
                if (bearRow != null) {
                    layPlayersDire.addView(bearRow);
                }
                layPlayersDire.addView(divider);
                numDirePlayers++;
            }
        }

        //only show team card if it contains players - this is useful for special events/gamemodes that only use one team
        if (numRadiantPlayers == 0) {
            layPlayersRadiant.setVisibility(View.GONE);
        }

        if (numDirePlayers == 0) {
            layPlayersDire.setVisibility(View.GONE);
        }
    }

    /*Scrolls the scoreboard to the right if the player was on the Dire team.*/
    private void scrollScoreboard() {
        //Check if the active user participated in the match
        activePlayer = null;
        for (DetailPlayer p : match.getPlayers()) {
            if (p.getAccount_id().equals(AppPreferences.getAccountID(getActivity()))) {
                activePlayer = p;
            }
        }
        //Make the scoreboard start in the correct scroll position
        //Default if player is Radiant, scrolled to right if player is Dire.
        if (activePlayer != null) {
            final HorizontalScrollView scrollView = (HorizontalScrollView) view.findViewById(R.id.svMatchDetailsPlayers);
            if (!MatchUtils.isRadiant(activePlayer))
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(4000, 0);
                    }
                });
        }
    }

    /*Builds picks/bans view if required.*/
    private void setPicksBans() {
        boolean hasPicksBans = false;

        if (match != null && match.getPicks_bans().size() > 0) {
            hasPicksBans = true;
        }

        //Picks & bans - only shown if match has picks/bans
        if (hasPicksBans) {
            LinearLayout layPicksBans = (LinearLayout) view.findViewById(R.id.layDetailPicksBans);
            layPicksBans.setVisibility(View.VISIBLE);

            for (PicksBans pb : match.getPicks_bans()) {

                options = new DisplayImageOptions.Builder()
                        .resetViewBeforeLoading(true)
                        .cacheInMemory(true)
                        .showImageOnLoading(R.drawable.hero_sb_loading)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .build();

                LinearLayout layPicksBansEntry = (LinearLayout) inflater.inflate(R.layout.pickban_entry, null);
                ImageView imgPBHero = (ImageView) layPicksBansEntry.findViewById(R.id.imgPickBanRight);
                TextView txtPBTop = (TextView) layPicksBansEntry.findViewById(R.id.txtPickBanTop);
                TextView txtPBBottom = (TextView) layPicksBansEntry.findViewById(R.id.txtPickBanBottom);

                if (pb.getTeam().equals("0")) {
                    if (pb.isIs_pick()) {
                        txtPBTop.setText("Pick");
                        txtPBTop.setTextColor(getActivity().getResources().getColor(R.color.RadiantGreen));
                    } else {
                        txtPBTop.setText("Ban");
                        txtPBTop.setTextColor(getActivity().getResources().getColor(R.color.DireOrange));
                    }

                } else {
                    if (pb.isIs_pick()) {
                        txtPBBottom.setText("Pick");
                        txtPBBottom.setTextColor(getActivity().getResources().getColor(R.color.RadiantGreen));

                    } else {
                        txtPBBottom.setText("Ban");
                        txtPBBottom.setTextColor(getActivity().getResources().getColor(R.color.DireOrange));
                    }
                }
                imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(pb.getHero_id()) + "_sb.png", imgPBHero, options, animateFirstListener);
                layPicksBans.addView(layPicksBansEntry);
            }
        }
    }

    /*Builds the experience graphs.*/
    private void setGraph() {
        System.out.println("starting get graph");
//        TeamExperienceGraphLoader loader = new TeamExperienceGraphLoader(getActivity(), this);
//        loader.execute(match.getMatch_id());

        //experience graph
        TeamExperienceStats teamExpStats = MatchUtils.getExperienceTeamGraphData(match.getPlayers());
        if (teamExpStats != null && teamExpStats.getExpRadiant() != null && teamExpStats.getExpRadiant().size() > 0 && teamExpStats.getExpDire() != null && teamExpStats.getExpDire().size() > 0) {


            lineGraphExperienceTeams = (LineGraph) view.findViewById(R.id.lineGraphExperienceTeam);
            //lineGraphExperienceTeams.setOnPointClickedListener(this);
            lineGraphExperienceTeams.setUsingDips(true);

            Line lineRadiant = new Line();
            lineRadiant.setColor(getResources().getColor(R.color.RadiantGreen));
            lineRadiant.setShowingPoints(false);

            //ArrayList<LinePoint> radiantPoints = new ArrayList<LinePoint>();
            //add 1 startpoint so the graph starts at X 0
            LinePoint p;
            p = new LinePoint();
            p.setX(0);
            p.setY(0);
            lineRadiant.addPoint(p);
            for (Map.Entry<Integer, Integer> entry : teamExpStats.getExpRadiant().entrySet()) {
//                LinePoint p;
                p = new LinePoint();
                p.setX(entry.getKey());
                p.setY(entry.getValue());
                lineRadiant.addPoint(p);
                //radiantPoints.add(p);
            }

            Line lineDire = new Line();
            lineDire.setColor(getResources().getColor(R.color.DireOrange));
            lineDire.setShowingPoints(false);
            //ArrayList<LinePoint> direPoints = new ArrayList<LinePoint>();
            //add 1 startpoint so the graph starts at X 0
//            LinePoint p;
            p = new LinePoint();
            p.setX(0);
            p.setY(0);
            lineDire.addPoint(p);
            for (Map.Entry<Integer, Integer> entry : teamExpStats.getExpDire().entrySet()) {
//                LinePoint p;
                p = new LinePoint();
                p.setX(entry.getKey());
                p.setY(entry.getValue());
                lineDire.addPoint(p);
                //direPoints.add(p);
            }

            //todo: fix nullpointer error for matches without experience values

            lineGraphExperienceTeams.addLine(lineRadiant);
            lineGraphExperienceTeams.addLine(lineDire);

            TextView txtExpTop = (TextView) view.findViewById(R.id.txtDetailExperiencetxtDetailExperienceTextTop);
            TextView txtExpMiddle = (TextView) view.findViewById(R.id.txtDetailExperiencetxtDetailExperienceTextMiddle);

            int rangeY;

            if (teamExpStats.getExpRadiant().lastEntry().getKey() > teamExpStats.getExpDire().lastEntry().getKey()) {
                rangeY = teamExpStats.getExpRadiant().lastEntry().getValue();
                lineGraphExperienceTeams.setLineToFill(0);
            } else {
                rangeY = teamExpStats.getExpDire().lastEntry().getValue();
                lineGraphExperienceTeams.setLineToFill(1);
            }

            txtExpTop.setText(Integer.toString(rangeY));
            txtExpMiddle.setText(Integer.toString(rangeY / 2));

            lineGraphExperienceTeams.setRangeY(0, rangeY);
            System.out.println("graph done");
        }
    }

    @Override
    public void processComplete(TeamExperienceStats graphStats) {

    }

    private void setItemImage(ImageView imgItem, String item_id) {
        if (imgItem != null) {
            imgItem.setContentDescription(ItemList.getItem(item_id));
            if (item_id.equals("0")) {
                imgItem.setImageResource(R.drawable.emptyitembg_lg);
            } else {
                imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/items/" + ItemList.getItem(item_id) + "_lg.png", imgItem, options, animateFirstListener);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //cancel any active asynctask
        //this way other asynctasks in the app don't have to wait until these finish (or time out)
        if (parsers != null && parsers.size() > 0) {
            for (PlayerSummaryParser parser : parsers) {
                parser.cancel(true);
            }

        }
    }

    /*Adds towers and barracks to minimap.*/
    @Override
    public void onGlobalLayout() {

        //remove listener so this method only gets called once
        //different versions depending on android version (before or after API 16)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            if (layDetailsMinimap.getViewTreeObserver() != null) {
                //noinspection deprecation
                layDetailsMinimap.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        } else {
            if (layDetailsMinimap.getViewTreeObserver() != null) {
                layDetailsMinimap.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }

        //get minimap size
        int x = layDetailsMinimap.getWidth();
        int y = layDetailsMinimap.getHeight();

        //add towers to minimap
        //todo: barracks (and fix this awful code)


        TowerStatus twrRadiant = Conversions.towerStatusFromString(match.getTower_status_radiant());
        TowerStatus twrDire = Conversions.towerStatusFromString(match.getTower_status_dire());

        if (twrRadiant.isTopT1()) {
            View towerRadiantTopT1 = inflater.inflate(R.layout.minimap_tower_radiant, null);

            if (towerRadiantTopT1 != null) {
                towerRadiantTopT1.setPadding((int) Math.round(x * 0.11), (int) Math.round(y * 0.38), 0, 0);
                layDetailsMinimap.addView(towerRadiantTopT1);
            }
        }

        if (twrRadiant.isTopT2()) {
            View towerRadiantTopT2 = inflater.inflate(R.layout.minimap_tower_radiant, null);

            if (towerRadiantTopT2 != null) {
                towerRadiantTopT2.setPadding((int) Math.round(x * 0.11), (int) Math.round(y * 0.55), 0, 0);
                layDetailsMinimap.addView(towerRadiantTopT2);
            }
        }

        if (twrRadiant.isTopT3()) {
            View towerRadiantTopT3 = inflater.inflate(R.layout.minimap_tower_radiant, null);

            if (towerRadiantTopT3 != null) {
                towerRadiantTopT3.setPadding((int) Math.round(x * 0.075), (int) Math.round(y * 0.7), 0, 0);
                layDetailsMinimap.addView(towerRadiantTopT3);
            }
        }

        if (twrRadiant.isMidT1()) {
            View towerRadiantMidT1 = inflater.inflate(R.layout.minimap_tower_radiant, null);

            if (towerRadiantMidT1 != null) {
                towerRadiantMidT1.setPadding((int) Math.round(x * 0.40), (int) Math.round(y * 0.58), 0, 0);
                layDetailsMinimap.addView(towerRadiantMidT1);
            }
        }

        if (twrRadiant.isMidT2()) {
            View towerRadiantMidT2 = inflater.inflate(R.layout.minimap_tower_radiant, null);

            if (towerRadiantMidT2 != null) {
                towerRadiantMidT2.setPadding((int) Math.round(x * 0.28), (int) Math.round(y * 0.66), 0, 0);
                layDetailsMinimap.addView(towerRadiantMidT2);
            }
        }

        if (twrRadiant.isMidT3()) {
            View towerRadiantMidT3 = inflater.inflate(R.layout.minimap_tower_radiant, null);

            if (towerRadiantMidT3 != null) {
                towerRadiantMidT3.setPadding((int) Math.round(x * 0.20), (int) Math.round(y * 0.75), 0, 0);
                layDetailsMinimap.addView(towerRadiantMidT3);
            }
        }

        if (twrRadiant.isBotT1()) {
            View towerRadiantBotT1 = inflater.inflate(R.layout.minimap_tower_radiant, null);

            if (towerRadiantBotT1 != null) {
                towerRadiantBotT1.setPadding((int) Math.round(x * 0.80), (int) Math.round(y * 0.87), 0, 0);
                layDetailsMinimap.addView(towerRadiantBotT1);
            }

        }
        if (twrRadiant.isBotT2()) {
            View towerRadiantBotT2 = inflater.inflate(R.layout.minimap_tower_radiant, null);

            if (towerRadiantBotT2 != null) {
                towerRadiantBotT2.setPadding((int) Math.round(x * 0.47), (int) Math.round(y * 0.88), 0, 0);
                layDetailsMinimap.addView(towerRadiantBotT2);
            }

        }
        if (twrRadiant.isBotT3()) {
            View towerRadiantBotT3 = inflater.inflate(R.layout.minimap_tower_radiant, null);

            if (towerRadiantBotT3 != null) {
                towerRadiantBotT3.setPadding((int) Math.round(x * 0.25), (int) Math.round(y * 0.88), 0, 0);
                layDetailsMinimap.addView(towerRadiantBotT3);
            }
        }
        if (twrRadiant.isTopT4()) {
            View towerRadiantTopT4 = inflater.inflate(R.layout.minimap_tower_radiant, null);

            if (towerRadiantTopT4 != null) {
                towerRadiantTopT4.setPadding((int) Math.round(x * 0.13), (int) Math.round(y * 0.80), 0, 0);
                layDetailsMinimap.addView(towerRadiantTopT4);
            }
        }
        if (twrRadiant.isBotT4()) {
            View towerRadiantBotT4 = inflater.inflate(R.layout.minimap_tower_radiant, null);

            if (towerRadiantBotT4 != null) {
                towerRadiantBotT4.setPadding((int) Math.round(x * 0.15), (int) Math.round(y * 0.82), 0, 0);
                layDetailsMinimap.addView(towerRadiantBotT4);
            }
        }


        if (twrDire.isTopT1()) {
            View towerDireTopT1 = inflater.inflate(R.layout.minimap_tower_dire, null);

            if (towerDireTopT1 != null) {
                towerDireTopT1.setPadding((int) Math.round(x * 0.20), (int) Math.round(y * 0.12), 0, 0);
                layDetailsMinimap.addView(towerDireTopT1);
            }
        }
        if (twrDire.isTopT2()) {
            View towerDireTopT2 = inflater.inflate(R.layout.minimap_tower_dire, null);

            if (towerDireTopT2 != null) {
                towerDireTopT2.setPadding((int) Math.round(x * 0.48), (int) Math.round(y * 0.12), 0, 0);
                layDetailsMinimap.addView(towerDireTopT2);
            }
        }
        if (twrDire.isTopT3()) {
            View towerDireTopT3 = inflater.inflate(R.layout.minimap_tower_dire, null);

            if (towerDireTopT3 != null) {
                towerDireTopT3.setPadding((int) Math.round(x * 0.70), (int) Math.round(y * 0.13), 0, 0);
                layDetailsMinimap.addView(towerDireTopT3);
            }
        }

        if (twrDire.isMidT1()) {
            View towerDireMidT1 = inflater.inflate(R.layout.minimap_tower_dire, null);

            if (towerDireMidT1 != null) {
                towerDireMidT1.setPadding((int) Math.round(x * 0.54), (int) Math.round(y * 0.48), 0, 0);
                layDetailsMinimap.addView(towerDireMidT1);
            }
        }
        if (twrDire.isMidT2()) {
            View towerDireMidT2 = inflater.inflate(R.layout.minimap_tower_dire, null);

            if (towerDireMidT2 != null) {
                towerDireMidT2.setPadding((int) Math.round(x * 0.63), (int) Math.round(y * 0.35), 0, 0);
                layDetailsMinimap.addView(towerDireMidT2);
            }
        }
        if (twrDire.isMidT3()) {
            View towerDireMidT3 = inflater.inflate(R.layout.minimap_tower_dire, null);

            if (towerDireMidT3 != null) {
                towerDireMidT3.setPadding((int) Math.round(x * 0.74), (int) Math.round(y * 0.26), 0, 0);
                layDetailsMinimap.addView(towerDireMidT3);
            }
        }

        if (twrDire.isBotT1()) {
            View towerDireBotT1 = inflater.inflate(R.layout.minimap_tower_dire, null);

            if (towerDireBotT1 != null) {
                towerDireBotT1.setPadding((int) Math.round(x * 0.85), (int) Math.round(y * 0.62), 0, 0);
                layDetailsMinimap.addView(towerDireBotT1);
            }
        }
        if (twrDire.isBotT2()) {
            View towerDireBotT2 = inflater.inflate(R.layout.minimap_tower_dire, null);

            if (towerDireBotT2 != null) {
                towerDireBotT2.setPadding((int) Math.round(x * 0.87), (int) Math.round(y * 0.49), 0, 0);
                layDetailsMinimap.addView(towerDireBotT2);
            }
        }
        if (twrDire.isBotT3()) {
            View towerDireBotT3 = inflater.inflate(R.layout.minimap_tower_dire, null);

            if (towerDireBotT3 != null) {
                towerDireBotT3.setPadding((int) Math.round(x * 0.87), (int) Math.round(y * 0.31), 0, 0);
                layDetailsMinimap.addView(towerDireBotT3);
            }
        }
        if (twrDire.isTopT4()) {
            View towerDireTopT4 = inflater.inflate(R.layout.minimap_tower_dire, null);

            if (towerDireTopT4 != null) {
                towerDireTopT4.setPadding((int) Math.round(x * 0.80), (int) Math.round(y * 0.18), 0, 0);
                layDetailsMinimap.addView(towerDireTopT4);
            }
        }
        if (twrDire.isBotT4()) {
            View towerDireBotT4 = inflater.inflate(R.layout.minimap_tower_dire, null);

            if (towerDireBotT4 != null) {
                towerDireBotT4.setPadding((int) Math.round(x * 0.82), (int) Math.round(y * 0.20), 0, 0);
                layDetailsMinimap.addView(towerDireBotT4);
            }
        }
    }

    /*Sets action bar buttons.*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_menu, menu);

        MenuItem btnRefresh = menu.findItem(R.id.btnRefresh);
        if (btnRefresh != null) {
            btnRefresh.setVisible(false);
        }

        MenuItem btnFavourite = menu.findItem(R.id.btnFavourite);
        if (btnFavourite != null) {
            if (match.getExtras() != null) {
                if (match.getExtras().isFavourite()) {
                    btnFavourite.setIcon(R.drawable.ic_action_important_color);
                } else {
                    btnFavourite.setIcon(R.drawable.ic_action_not_important);
                }
            }
        }
    }

    /*Handles action bar buttons.*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                return true;
            case R.id.btnNote:
                noteDialog();
                return true;
            case R.id.btnFavourite:
                match.getExtras().setFavourite(!match.getExtras().isFavourite());
                match.getExtras().setMatch_id(match.getMatch_id());

                MatchesExtrasDataSource meds = new MatchesExtrasDataSource(getActivity());
                meds.updateMatchesExtras(match.getExtras());

                if (match.getExtras().isFavourite()) {
                    Toast.makeText(getActivity(), "Added match to favourites", Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.ic_action_important_color);
                } else {
                    Toast.makeText(getActivity(), "Removed match from favourites", Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.ic_action_not_important);
                }
                return true;
//            case R.id.btnShare:
//                setShareIntentImage();
//
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //received names of players
    @Override
    public void processFinish(PlayerSummaryContainer result) {
        if (result != null) {
            if (result.getPlayers() != null) {
                if (result.getPlayers().getPlayers().size() < 1) {
                    for (TextView textView : playerNames) {
                        if (textView.getText() != null) {
                            if (textView.getText().equals("4294967295")) {
                                textView.setText("Anonymous");
                            }
                        }
                    }
                } else {
                    for (TextView textView : playerNames) {
                        if (textView.getText() != null) {
                            if (textView.getText().equals(Conversions.community64IDToDota64ID(result.getPlayers().getPlayers().get(0).getSteamid()))) {
                                textView.setText(result.getPlayers().getPlayers().get(0).getPersonaname());
                            }
                        }
                    }
                    for (ImageView imgView : playerAvatars) {
                        if (imgView.getTag() != null) {
                            if (imgView.getTag().equals(Conversions.community64IDToDota64ID(result.getPlayers().getPlayers().get(0).getSteamid()))) {
                                imageLoader.displayImage(result.getPlayers().getPlayers().get(0).getAvatar(), imgView, options, animateFirstListener);
                            }
                        }
                    }
                }
            }
        } else {
            //this means the connection timed out, do nothing
        }

    }

    private void noteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add note");

        // Set up the input
        final EditText input = new EditText(getActivity());
        input.setLines(6);
        input.setMinLines(3);
        if (match.getExtras().getNote() != null && !match.getExtras().getNote().equals("") && !match.getExtras().getNote().equals("null")) {
            input.setText(match.getExtras().getNote());
        } else {
            input.setText("");
        }


        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveNote(input.getText().toString());
                //hide keyboard
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void saveNote(String text) {

        match.getExtras().setNote(text);
        match.getExtras().setMatch_id((match.getMatch_id()));

        MatchesExtrasDataSource meds = new MatchesExtrasDataSource(getActivity());
        meds.updateMatchesExtras(match.getExtras());
        if (text.equals("")) {
            Toast.makeText(getActivity(), "Note removed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Note saved", Toast.LENGTH_SHORT).show();
        }

        //replace the fragment to make the note appear
        //todo: fix bug where back button no longer works after adding a note and then rotating the device

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new MatchDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("be.simonraes.dotadata.detailmatch", match);
        fragment.setArguments(bundle);
        transaction.remove(this);
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();

    }

    /*Takes screenshot of the important views so scoreboard can be shared - WIP*/
    private void setShareIntentImage() {
        //match info

        View viewInfo = view.findViewById(R.id.layDetailHeader);
        Bitmap headerBitmap = Bitmap.createBitmap(viewInfo.getWidth(), viewInfo.getHeight(), Bitmap.Config.ARGB_8888);
        //Construct a canvas with the specified bitmap to draw into.
        Canvas viewCanvas = new Canvas(headerBitmap);
        viewInfo.draw(viewCanvas);
        //image now stored in headerBitmap

        //players

        View viewPlayers = view.findViewById(R.id.svMatchDetailsPlayers);
        HorizontalScrollView scrollViewPlayers = (HorizontalScrollView) view.findViewById(R.id.svMatchDetailsPlayers);

        viewPlayers.setDrawingCacheEnabled(true);
        viewPlayers.buildDrawingCache(true);

        Bitmap playersBitmap = Bitmap.createBitmap(scrollViewPlayers.getChildAt(0).getWidth(), scrollViewPlayers.getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);
        //add bitmap to canvas
        Canvas c = new Canvas(playersBitmap);
        //set canvas background color
        c.drawColor(getResources().getColor(R.color.Gainsboro));
        //set view size
        viewPlayers.layout(0, 0, viewPlayers.getLayoutParams().width, viewPlayers.getLayoutParams().height);
        //draw view on canvas
        viewPlayers.draw(c);

        viewPlayers.setDrawingCacheEnabled(false);
        viewPlayers.buildDrawingCache(false);
        //image now stored in playersBitmap


        //minimap
//        View viewMap = view.findViewById(R.id.layDetailMapCard);
//        Bitmap mapBitmap = Bitmap.createBitmap(viewMap.getWidth(), viewMap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas mapCanvas = new Canvas(mapBitmap);
//        viewMap.draw(mapCanvas);
//        //image stored in mapBitmap


        //total

        //start with header as widest view
        int widestWidth = viewInfo.getWidth();
        Bitmap resultBitmap = Bitmap.createBitmap(viewInfo.getWidth(), viewInfo.getHeight() + scrollViewPlayers.getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);

//        Bitmap resultBitmap = Bitmap.createBitmap(viewInfo.getWidth(), viewInfo.getHeight() + scrollViewPlayers.getChildAt(0).getHeight() + viewMap.getHeight(), Bitmap.Config.ARGB_8888);

        //check if other views are wider
        if (scrollViewPlayers.getChildAt(0).getWidth() > widestWidth) {
            widestWidth = scrollViewPlayers.getChildAt(0).getWidth();
            resultBitmap = Bitmap.createBitmap(scrollViewPlayers.getChildAt(0).getWidth(), viewInfo.getHeight() + scrollViewPlayers.getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);

//            resultBitmap = Bitmap.createBitmap(scrollViewPlayers.getChildAt(0).getWidth(), viewInfo.getHeight() + scrollViewPlayers.getChildAt(0).getHeight() + viewMap.getHeight(), Bitmap.Config.ARGB_8888);
        }
//        if (viewMap.getWidth() > widestWidth) {
//            widestWidth = viewMap.getWidth();
//            resultBitmap = Bitmap.createBitmap(viewMap.getWidth(), viewInfo.getHeight() + scrollViewPlayers.getChildAt(0).getHeight() + viewMap.getHeight(), Bitmap.Config.ARGB_8888);
//        }

        Canvas resultCanvas = new Canvas(resultBitmap);
        resultCanvas.drawColor(getResources().getColor(R.color.Gainsboro));
        Paint paint = new Paint();

        resultCanvas.drawBitmap(headerBitmap, (widestWidth - viewInfo.getWidth()) / 2, 0, paint);
        resultCanvas.drawBitmap(playersBitmap, (widestWidth - scrollViewPlayers.getChildAt(0).getWidth()) / 2, headerBitmap.getHeight(), paint);
//        resultCanvas.drawBitmap(mapBitmap, (widestWidth - viewMap.getWidth()) / 2, headerBitmap.getHeight() + scrollViewPlayers.getChildAt(0).getHeight(), paint);

        //Save bitmap
//        String path = Environment.getExternalStorageDirectory().toString();
//        File mFolder = new File(path + "/dotes");
//        if (!mFolder.exists()) {
//            mFolder.mkdir();
//        }
//
//        String fileName = "scoreboard.jpg";
//        File myPath = new File(mFolder.getAbsolutePath(), fileName);
//        System.out.println(myPath);
//        FileOutputStream fos;
//
//        try {
//            fos = new FileOutputStream(myPath);
//            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), resultBitmap, "Scoreboard", "result of match xxxxxx");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        //Bitmap screenShot = resultBitmap;
        Bitmap icon = resultBitmap;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "dotadata" + File.separator + "DD_Scoreboard.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "dotadata" + File.separator + "DD_Scoreboard.jpg"));
        startActivity(Intent.createChooser(share, "Share Image"));


//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("image/*");
//        Uri uri = Uri.fromFile(myPath);
//        intent.putExtra(Intent.EXTRA_STREAM, uri.toString());
//        mShareActionProvider.setShareIntent(intent);

    }


    @Override
    public void onClick(View v) {
        boolean deletedNote = false;
        switch (v.getId()) {
            case R.id.btnDetailDeleteNote:
                deletedNote = true;
                saveNote("");
                break;
            default:
                break;
        }

        //todo: needs a better check to see if the button is one of the 10 player buttons
        if (!deletedNote) {
            int clickedPlayer = Integer.parseInt(v.getTag().toString());

            DialogFragment detailsDialog = PlayerDetailsDialog.newInstance(match.getPlayers().get(clickedPlayer), match.getDuration());
            detailsDialog.show(getActivity().getSupportFragmentManager(), "d");
        }


    }


}
