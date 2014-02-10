package be.simonraes.dotadata.fragment;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.interfaces.ASyncResponseLeagueListing;
import be.simonraes.dotadata.leaguelisting.League;
import be.simonraes.dotadata.leaguelisting.LeagueListingContainer;
import be.simonraes.dotadata.liveleaguegame.LiveLeagueMatch;
import be.simonraes.dotadata.liveleaguegame.LiveLeaguePlayer;
import be.simonraes.dotadata.parser.LeagueListingParser;
import be.simonraes.dotadata.parser.SteamRemoteStorageParser;
import be.simonraes.dotadata.util.AnimateFirstDisplayListenerToo;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.HeroList;
import be.simonraes.dotadata.util.TowerStatus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * Created by Simon on 5/02/14.
 */
public class LiveLeagueGameFragment extends Fragment implements ASyncResponseLeagueListing, ViewTreeObserver.OnGlobalLayoutListener {

    private String leagueID = "";

    private TextView txtLeague;

    private FrameLayout layLiveLeagueMinimap;
    private LiveLeagueMatch match;
    private LayoutInflater inflaterB;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.liveleaguegame_layout, container, false);
        inflaterB = inflater;

        getActivity().getActionBar().setTitle("Live league game");

        match = (LiveLeagueMatch) getArguments().getSerializable("liveLeagueMatch");

        TextView txtTeams = (TextView) view.findViewById(R.id.txtLiveLeagueTeams);
        txtTeams.setText(Html.fromHtml("<b>" + match.getRadiantTeam().getTeam_name() + "</b>" + " vs " + "<b>" + match.getDireTeam().getTeam_name() + "</b>"));

        //todo: store leagues in DB, only parse if leagueID is unknown
        LeagueListingParser parser = new LeagueListingParser(this);
        parser.execute();

        txtLeague = (TextView) view.findViewById(R.id.txtLiveLeagueLeague);
        //set leagueID for use in processFinish()
        leagueID = match.getLeague_id();
        txtLeague.setText("League: " + leagueID);
        SteamRemoteStorageParser logoParser;

        //set team logos
        if (!match.getRadiantTeam().getTeam_logo().equals("0")) {
            ImageView imgRadiantLogo = (ImageView) view.findViewById(R.id.imgLiveLeagueDetailsRadiantLogo);
            logoParser = new SteamRemoteStorageParser(imgRadiantLogo);
            logoParser.execute(match.getRadiantTeam().getTeam_logo());
        }
        if (!match.getDireTeam().getTeam_logo().equals("0")) {
            ImageView imgDireLogo = (ImageView) view.findViewById(R.id.imgLiveLeagueDetailsDireLogo);
            logoParser = new SteamRemoteStorageParser(imgDireLogo);
            logoParser.execute(match.getDireTeam().getTeam_logo());
        }

        TextView txtSpectatorCount = (TextView) view.findViewById(R.id.txtLiveLeagueSpectatorCount);
        txtSpectatorCount.setText("DotaTV spectators: " + match.getSpectators());

        LinearLayout layPlayersRadiant = (LinearLayout) view.findViewById(R.id.layLiveLeagueRadiantPlayers);
        LinearLayout layPlayersDire = (LinearLayout) view.findViewById(R.id.layLiveLeagueDirePlayers);
        LinearLayout laySpectators = (LinearLayout) view.findViewById(R.id.layLiveLeagueSpectators);

        int numberOfCasters = 0;
        View playerRow;
        TextView txtPlayerName;
        ImageView imgHero;
        ImageLoader imageLoader;
        DisplayImageOptions options;
        ImageLoadingListener animateFirstListener;

        for (LiveLeaguePlayer d : match.getLiveLeaguePlayers()) {
            playerRow = inflater.inflate(R.layout.liveleaguegame_player_row, null);

            txtPlayerName = (TextView) playerRow.findViewById(R.id.txtLiveLeaguePlayerName);
            txtPlayerName.setText(d.getName());

            //no hero image for spectators
            if (!d.getHero_id().equals("0")) {

                imageLoader = ImageLoader.getInstance();

                //heroloading options
                options = new DisplayImageOptions.Builder()
                        .resetViewBeforeLoading(true)
                        .cacheInMemory(true)
                        .cacheOnDisc(true)
                        .showImageOnLoading(R.drawable.hero_sb_loading)
                        .build();
                animateFirstListener = new AnimateFirstDisplayListenerToo();

                imgHero = (ImageView) playerRow.findViewById(R.id.imgLiveLeagueDetailHero);
                imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(d.getHero_id()) + "_sb.png", imgHero, options, animateFirstListener);
            }

            if (Integer.parseInt(d.getTeam()) == 0) {
                layPlayersRadiant.addView(playerRow);
            } else if (Integer.parseInt(d.getTeam()) == 1) {
                layPlayersDire.addView(playerRow);
            } else if (Integer.parseInt(d.getTeam()) == 2) {
                numberOfCasters++;
                laySpectators.addView(playerRow);
            }
            if (numberOfCasters <= 0) {
                TextView txtCasters = (TextView) view.findViewById(R.id.txtLiveLeagueCasters);
                txtCasters.setText("No casters");
            }
        }

        layLiveLeagueMinimap = (FrameLayout) view.findViewById(R.id.layLiveLeagueMinimap);
        layLiveLeagueMinimap.getViewTreeObserver().addOnGlobalLayoutListener(this);


        return view;
    }

    @Override
    public void processFinish(LeagueListingContainer result) {
        //only reload view if it is still on screen
        if (view.isShown()) {
            for (League l : result.getLeagues().getLeagues()) {
                if (l.getLeagueid().equals(leagueID)) {
                    txtLeague.setText(Conversions.leagueTitleToString(l.getName()));
                }
            }
        }
    }


    //todo: check if duplicate code can be removed (this and matchdetailfragment)
    //todo: check if views can be added without a new inflate for each one
    @Override
    public void onGlobalLayout() {

        //remove listener so this method only gets called once
        //different versions depending on android version (before or after API 16)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            layLiveLeagueMinimap.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        } else {
            layLiveLeagueMinimap.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }

        //get minimap size
        int x = layLiveLeagueMinimap.getWidth();
        int y = layLiveLeagueMinimap.getHeight();

        //add towers to minimap
        TowerStatus twrRadiant = Conversions.radiantTowerStatusFromTeamString(match.getTower_state());
        TowerStatus twrDire = Conversions.direTowerStatusFromTeamString(match.getTower_state());

        if (twrRadiant.isTopT1()) {
            View towerRadiantTopT1 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantTopT1.setPadding((int) Math.round(x * 0.11), (int) Math.round(y * 0.38), 0, 0);
            layLiveLeagueMinimap.addView(towerRadiantTopT1);
        }

        if (twrRadiant.isTopT2()) {
            View towerRadiantTopT2 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantTopT2.setPadding((int) Math.round(x * 0.11), (int) Math.round(y * 0.55), 0, 0);
            layLiveLeagueMinimap.addView(towerRadiantTopT2);
        }

        if (twrRadiant.isTopT3()) {
            View towerRadiantTopT3 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantTopT3.setPadding((int) Math.round(x * 0.075), (int) Math.round(y * 0.7), 0, 0);
            layLiveLeagueMinimap.addView(towerRadiantTopT3);
        }

        if (twrRadiant.isMidT1()) {
            View towerRadiantMidT1 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantMidT1.setPadding((int) Math.round(x * 0.40), (int) Math.round(y * 0.58), 0, 0);
            layLiveLeagueMinimap.addView(towerRadiantMidT1);
        }

        if (twrRadiant.isMidT2()) {
            View towerRadiantMidT2 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantMidT2.setPadding((int) Math.round(x * 0.28), (int) Math.round(y * 0.66), 0, 0);
            layLiveLeagueMinimap.addView(towerRadiantMidT2);
        }

        if (twrRadiant.isMidT3()) {
            View towerRadiantMidT3 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantMidT3.setPadding((int) Math.round(x * 0.20), (int) Math.round(y * 0.75), 0, 0);
            layLiveLeagueMinimap.addView(towerRadiantMidT3);
        }

        if (twrRadiant.isBotT1()) {
            View towerRadiantBotT1 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantBotT1.setPadding((int) Math.round(x * 0.80), (int) Math.round(y * 0.87), 0, 0);
            layLiveLeagueMinimap.addView(towerRadiantBotT1);
        }
        if (twrRadiant.isBotT2()) {
            View towerRadiantBotT2 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantBotT2.setPadding((int) Math.round(x * 0.47), (int) Math.round(y * 0.88), 0, 0);
            layLiveLeagueMinimap.addView(towerRadiantBotT2);
        }
        if (twrRadiant.isBotT3()) {
            View towerRadiantBotT3 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantBotT3.setPadding((int) Math.round(x * 0.25), (int) Math.round(y * 0.88), 0, 0);
            layLiveLeagueMinimap.addView(towerRadiantBotT3);
        }
        if (twrRadiant.isTopT4()) {
            View towerRadiantTopT4 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantTopT4.setPadding((int) Math.round(x * 0.15), (int) Math.round(y * 0.82), 0, 0);
            layLiveLeagueMinimap.addView(towerRadiantTopT4);
        }
        if (twrRadiant.isBotT4()) {
            View towerRadiantBotT4 = inflaterB.inflate(R.layout.minimap_tower_radiant, null);

            towerRadiantBotT4.setPadding((int) Math.round(x * 0.13), (int) Math.round(y * 0.80), 0, 0);
            layLiveLeagueMinimap.addView(towerRadiantBotT4);
        }


        if (twrDire.isTopT1()) {
            View towerDireTopT1 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireTopT1.setPadding((int) Math.round(x * 0.20), (int) Math.round(y * 0.12), 0, 0);
            layLiveLeagueMinimap.addView(towerDireTopT1);
        }
        if (twrDire.isTopT2()) {
            View towerDireTopT2 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireTopT2.setPadding((int) Math.round(x * 0.48), (int) Math.round(y * 0.12), 0, 0);
            layLiveLeagueMinimap.addView(towerDireTopT2);
        }
        if (twrDire.isTopT3()) {
            View towerDireTopT3 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireTopT3.setPadding((int) Math.round(x * 0.70), (int) Math.round(y * 0.13), 0, 0);
            layLiveLeagueMinimap.addView(towerDireTopT3);
        }

        if (twrDire.isMidT1()) {
            View towerDireMidT1 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireMidT1.setPadding((int) Math.round(x * 0.54), (int) Math.round(y * 0.48), 0, 0);
            layLiveLeagueMinimap.addView(towerDireMidT1);
        }
        if (twrDire.isMidT2()) {
            View towerDireMidT2 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireMidT2.setPadding((int) Math.round(x * 0.63), (int) Math.round(y * 0.35), 0, 0);
            layLiveLeagueMinimap.addView(towerDireMidT2);
        }
        if (twrDire.isMidT3()) {
            View towerDireMidT3 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireMidT3.setPadding((int) Math.round(x * 0.74), (int) Math.round(y * 0.26), 0, 0);
            layLiveLeagueMinimap.addView(towerDireMidT3);
        }

        if (twrDire.isBotT1()) {
            View towerDireBotT1 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireBotT1.setPadding((int) Math.round(x * 0.85), (int) Math.round(y * 0.62), 0, 0);
            layLiveLeagueMinimap.addView(towerDireBotT1);
        }
        if (twrDire.isBotT2()) {
            View towerDireBotT2 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireBotT2.setPadding((int) Math.round(x * 0.87), (int) Math.round(y * 0.49), 0, 0);
            layLiveLeagueMinimap.addView(towerDireBotT2);
        }
        if (twrDire.isBotT3()) {
            View towerDireBotT3 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireBotT3.setPadding((int) Math.round(x * 0.87), (int) Math.round(y * 0.31), 0, 0);
            layLiveLeagueMinimap.addView(towerDireBotT3);
        }
        if (twrDire.isTopT4()) {
            View towerDireTopT4 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireTopT4.setPadding((int) Math.round(x * 0.80), (int) Math.round(y * 0.18), 0, 0);
            layLiveLeagueMinimap.addView(towerDireTopT4);
        }
        if (twrDire.isBotT4()) {
            View towerDireBotT4 = inflaterB.inflate(R.layout.minimap_tower_dire, null);

            towerDireBotT4.setPadding((int) Math.round(x * 0.82), (int) Math.round(y * 0.20), 0, 0);
            layLiveLeagueMinimap.addView(towerDireBotT4);
        }
    }
}
