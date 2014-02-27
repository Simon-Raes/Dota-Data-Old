package be.simonraes.dotadata.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.provider.MediaStore;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.delegates.ASyncResponsePlayerSummary;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.detailmatch.DetailPlayer;
import be.simonraes.dotadata.detailmatch.PicksBans;
import be.simonraes.dotadata.parser.PlayerSummaryParser;
import be.simonraes.dotadata.playersummary.PlayerSummaryContainer;
import be.simonraes.dotadata.util.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Simon on 30/01/14.
 * Sets layout to show details of a match
 */
public class MatchDetailFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener, View.OnClickListener, ASyncResponsePlayerSummary {

    private LayoutInflater inflaterB;
    private View view;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener;

    private ArrayList<TextView> playerNames;

    private DetailMatch match;

    private FrameLayout layDetailsMinimap;
    private ImageButton btnDetailDeleteNote;

    private Bitmap screenShot;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        view = inflater.inflate(R.layout.matchdetails_layout, container, false);
        inflaterB = inflater;

        getActivity().setTitle("Match Details");


        setHasOptionsMenu(true);

        //disable drawer icon (needed for reorientation)
        ((DrawerController) getActivity()).getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        //update the actionbar to show the up carat/affordance
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);


        match = (DetailMatch) getArguments().getParcelable("be.simonraes.dotadata.detailmatch");
        playerNames = new ArrayList<TextView>();

        boolean hasPicksBans = false;

        if (match != null) {
            if (match.getPicks_bans().size() > 0) {
                hasPicksBans = true;
            }
        }

        //holder?

        //Match info
        TextView txtMatchID = (TextView) view.findViewById(R.id.txtDetailMatchID);
        txtMatchID.setText("Match ID: " + match.getMatch_id());

        TextView txtGameMode = (TextView) view.findViewById(R.id.txtDetailGameMode);
        txtGameMode.setText(GameModes.getGameMode(match.getGame_mode()));
        if (match.getLobby_type().equals("7")) {
            txtGameMode.setText(GameModes.getGameMode(match.getGame_mode()) + " (Ranked)");
        }

        TextView txtDuration = (TextView) view.findViewById(R.id.txtDetailDuration);
        txtDuration.setText("Duration: " + Conversions.secondsToTime(match.getDuration()));

        TextView txtDate = (TextView) view.findViewById(R.id.txtDetailDate);
        txtDate.setText(Conversions.millisToDate(match.getStart_time()));

        TextView txtWinner = (TextView) view.findViewById(R.id.txtDetailWinner);
        if (match.getRadiant_win()) {
            txtWinner.setText("Radiant Victory");
        } else {
            txtWinner.setText("Dire Victory");
        }

        TextView txtVictoryDefeat = (TextView) view.findViewById(R.id.txtDetailVictoryDefeat);
        if (match.isUser_win()) {
            txtVictoryDefeat.setText("Victory");
            txtVictoryDefeat.setTextColor(getActivity().getResources().getColor(R.color.ForestGreen));
        } else {
            txtVictoryDefeat.setText("Defeat");
            txtVictoryDefeat.setTextColor(getActivity().getResources().getColor(R.color.Crimson));
        }


        //note layout
        if (match.getNote() != null && !match.getNote().equals("") && !match.getNote().equals("null")) {
            RelativeLayout layNote = (RelativeLayout) view.findViewById(R.id.layDetailNote);
            layNote.setVisibility(View.VISIBLE);
            TextView txtNote = (TextView) view.findViewById(R.id.txtDetailNote);
            txtNote.setText(match.getNote());
            ImageButton btnDeleteNote = (ImageButton) view.findViewById(R.id.btnDetailDeleteNote);
            btnDeleteNote.setOnClickListener(this);
        }


        //Players info
        LinearLayout layPlayersRadiant = (LinearLayout) view.findViewById(R.id.layDetailRadiantPlayers);
        LinearLayout layPlayersDire = (LinearLayout) view.findViewById(R.id.layDetailDirePlayers);


        imageLoader = ImageLoader.getInstance();

        View playerRow;
        animateFirstListener = new AnimateFirstDisplayListenerToo();

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.hero_sb_loading)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        int numRadiantPlayers = 0;
        int numDirePlayers = 0;


        for (DetailPlayer player : match.getPlayers()) {
            playerRow = inflater.inflate(R.layout.matchdetails_player_row, null);

            View divider = inflater.inflate(R.layout.divider, null);

            //give user's row a special background color
            if (player.getAccount_id().equals(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("be.simonraes.dotadata.accountid", ""))) {
                playerRow.setBackgroundColor(getResources().getColor(R.color.AntiqueWhite));
            }

            TextView txtPlayerLevel = (TextView) playerRow.findViewById(R.id.txtDetailPlayerLevel);
            txtPlayerLevel.setText(player.getLevel());

            TextView txtPlayerName = (TextView) playerRow.findViewById(R.id.txtDetailPlayerName);
            txtPlayerName.setText(player.getAccount_id());
            playerNames.add(txtPlayerName);

            //start parser to get player's name
            if (InternetCheck.isOnline(getActivity())) {
                PlayerSummaryParser parser = new PlayerSummaryParser(this);
                parser.execute(player.getAccount_id());
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
            //imgHero.setImageResource(getActivity().getResources().getIdentifier(HeroList.getHeroImageName(player.getHero_id()), "drawable", getActivity().getPackageName()));            //item image loading options
            options = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true)
                    .cacheInMemory(true)
                    .showImageOnLoading(R.drawable.item_lg_unknown)
                    .build();


            ImageView imgItem = (ImageView) playerRow.findViewById(R.id.imgItem1);
            imgItem.setImageResource(getActivity().getResources().getIdentifier(ItemList.getItem(player.getItem_0()) + "_lg", "drawable", getActivity().getPackageName()));

            //setItemImage(imgItem, player.getItem_0());

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem2);
            imgItem.setImageResource(getActivity().getResources().getIdentifier(ItemList.getItem(player.getItem_1()) + "_lg", "drawable", getActivity().getPackageName()));

            //setItemImage(imgItem, player.getItem_1());

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem3);
            imgItem.setImageResource(getActivity().getResources().getIdentifier(ItemList.getItem(player.getItem_2()) + "_lg", "drawable", getActivity().getPackageName()));

            //setItemImage(imgItem, player.getItem_2());

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem4);
            imgItem.setImageResource(getActivity().getResources().getIdentifier(ItemList.getItem(player.getItem_3()) + "_lg", "drawable", getActivity().getPackageName()));

            //setItemImage(imgItem, player.getItem_3());

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem5);
            imgItem.setImageResource(getActivity().getResources().getIdentifier(ItemList.getItem(player.getItem_4()) + "_lg", "drawable", getActivity().getPackageName()));

            //setItemImage(imgItem, player.getItem_4());

            imgItem = (ImageView) playerRow.findViewById(R.id.imgItem6);
            imgItem.setImageResource(getActivity().getResources().getIdentifier(ItemList.getItem(player.getItem_5()) + "_lg", "drawable", getActivity().getPackageName()));

            //setItemImage(imgItem, player.getItem_5());

            if (Integer.parseInt(player.getPlayer_slot()) < 5) {
                layPlayersRadiant.addView(playerRow);
                layPlayersRadiant.addView(divider);
                numRadiantPlayers++;
            } else {
                layPlayersDire.addView(playerRow);
                layPlayersDire.addView(divider);
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

                RelativeLayout layPBLeft = (RelativeLayout) inflaterB.inflate(R.layout.pickban_left, null);
                RelativeLayout layPBRight = (RelativeLayout) inflaterB.inflate(R.layout.pickban_right, null);
                ImageView imgPBHeroLeft = (ImageView) layPBLeft.findViewById(R.id.imgPickBanLeft);
                TextView txtPBLeft = (TextView) layPBLeft.findViewById(R.id.txtPickBanLeft);
                ImageView imgPBHeroRight = (ImageView) layPBRight.findViewById(R.id.imgPickBanRight);
                TextView txtPBRight = (TextView) layPBRight.findViewById(R.id.txtPickBanRight);

                if (pb.getTeam().equals("0")) {
                    if (pb.isIs_pick()) {
                        txtPBLeft.setText("Pick");
                    } else {
                        txtPBLeft.setText("Ban");
                    }
                    imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(pb.getHero_id()) + "_sb.png", imgPBHeroLeft, options, animateFirstListener);
                    layPicksBans.addView(layPBLeft);
                } else {
                    if (pb.isIs_pick()) {
                        txtPBRight.setText("Pick");
                    } else {
                        txtPBRight.setText("Ban");
                    }
                    imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(pb.getHero_id()) + "_sb.png", imgPBHeroRight, options, animateFirstListener);
                    layPicksBans.addView(layPBRight);
                }
            }

        }

        //add listener to retrieve height and width of minimap layout, will call onGlobalLayout()
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
        //todo: barracks (and fix this awful code)


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

            towerRadiantTopT4.setPadding((int) Math.round(x * 0.13), (int) Math.round(y * 0.80), 0, 0);
            layDetailsMinimap.addView(towerRadiantTopT4);
        }
        if (twrRadiant.isBotT4()) {
            View towerRadiantBotT4 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantBotT4.setPadding((int) Math.round(x * 0.15), (int) Math.round(y * 0.82), 0, 0);
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

    //set action bar buttons
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
            if (match.isFavourite()) {
                btnFavourite.setIcon(R.drawable.ic_action_important_color);
            } else {
                btnFavourite.setIcon(R.drawable.ic_action_not_important);
            }
        }

    }


    //click action bar buttson
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.btnNote:
                noteDialog();
                return true;
            case R.id.btnFavourite:
                MatchesDataSource mds = new MatchesDataSource(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("be.simonraes.dotadata.accountid", ""));
                match.setFavourite(!match.isFavourite());
                //todo: fix database so 1 match can be added (single add method doesn't have database open(),close())
                mds.saveDetailMatches(new ArrayList<DetailMatch>(Arrays.asList(match)));
                if (match.isFavourite()) {
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

        if (result.getPlayers().getPlayers().size() < 1) {
            for (TextView textView : playerNames) {
                if (textView.getText().equals("4294967295")) {
                    textView.setText("Anonymous");
                }
            }
        } else {
            for (TextView textView : playerNames) {
                if (textView.getText().equals(Conversions.community64IDToDota64ID(result.getPlayers().getPlayers().get(0).getSteamid()))) {
                    textView.setText(result.getPlayers().getPlayers().get(0).getPersonaname());
                }
            }
        }
    }

    private void noteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add note");
        String m_Text = "";

        // Set up the input
        final EditText input = new EditText(getActivity());
        input.setLines(6);
        input.setMinLines(3);
        if (match.getNote() != null && !match.getNote().equals("") && !match.getNote().equals("null")) {
            input.setText(match.getNote());
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
        MatchesDataSource mds = new MatchesDataSource(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("be.simonraes.dotadata.accountid", ""));
        match.setNote(text);
        mds.open();
        mds.saveDetailMatch(match);
        mds.close();
        if (text.equals("")) {
            Toast.makeText(getActivity(), "Note removed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Note saved", Toast.LENGTH_SHORT).show();
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.remove(this);
        transaction.replace(R.id.content_frame, this).commit();
    }

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
        View viewMap = view.findViewById(R.id.layDetailMapCard);
        Bitmap mapBitmap = Bitmap.createBitmap(viewMap.getWidth(), viewMap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mapCanvas = new Canvas(mapBitmap);
        viewMap.draw(mapCanvas);
        //image stored in mapBitmap


        //total

        //start with header as widest view
        int widestWidth = viewInfo.getWidth();
        Bitmap resultBitmap = Bitmap.createBitmap(viewInfo.getWidth(), viewInfo.getHeight() + scrollViewPlayers.getChildAt(0).getHeight() + viewMap.getHeight(), Bitmap.Config.ARGB_8888);

        //check if other views are wider
        if (scrollViewPlayers.getChildAt(0).getWidth() > widestWidth) {
            widestWidth = scrollViewPlayers.getChildAt(0).getWidth();
            resultBitmap = Bitmap.createBitmap(scrollViewPlayers.getChildAt(0).getWidth(), viewInfo.getHeight() + scrollViewPlayers.getChildAt(0).getHeight() + viewMap.getHeight(), Bitmap.Config.ARGB_8888);
        }
        if (viewMap.getWidth() > widestWidth) {
            widestWidth = viewMap.getWidth();
            resultBitmap = Bitmap.createBitmap(viewMap.getWidth(), viewInfo.getHeight() + scrollViewPlayers.getChildAt(0).getHeight() + viewMap.getHeight(), Bitmap.Config.ARGB_8888);

        }

        Canvas resultCanvas = new Canvas(resultBitmap);
        resultCanvas.drawColor(getResources().getColor(R.color.Gainsboro));
        Paint paint = new Paint();

        resultCanvas.drawBitmap(headerBitmap, (widestWidth - viewInfo.getWidth()) / 2, 0, paint);
        resultCanvas.drawBitmap(playersBitmap, (widestWidth - scrollViewPlayers.getChildAt(0).getWidth()) / 2, headerBitmap.getHeight(), paint);
        resultCanvas.drawBitmap(mapBitmap, (widestWidth - viewMap.getWidth()) / 2, headerBitmap.getHeight() + scrollViewPlayers.getChildAt(0).getHeight(), paint);

        //Save bitmap
        String path = Environment.getExternalStorageDirectory().toString();
        File mFolder = new File(path + "/dotes");
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }

        String fileName = "scoreboard.jpg";
        File myPath = new File(mFolder.getAbsolutePath(), fileName);
        System.out.println(myPath);
        FileOutputStream fos;

        try {
            fos = new FileOutputStream(myPath);
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), resultBitmap, "Scoreboard", "result of match xxxxxx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        screenShot = resultBitmap;
        Bitmap icon = resultBitmap;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
        startActivity(Intent.createChooser(share, "Share Image"));


//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("image/*");
//        Uri uri = Uri.fromFile(myPath);
//        intent.putExtra(Intent.EXTRA_STREAM, uri.toString());
//        mShareActionProvider.setShareIntent(intent);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDetailDeleteNote:
                saveNote("");
//                //hide keyboard
                //((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(btnDetailDeleteNote.getWindowToken(), 0);
                break;
            default:
                break;
        }
    }
}
