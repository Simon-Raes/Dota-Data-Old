package be.simonraes.dotadata.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * Created by Simon on 5/02/14.
 */
public class LiveLeagueGameFragment extends Fragment implements ASyncResponseLeagueListing {

    private String leagueID = "";

    private TextView txtLeague;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.liveleaguegame_layout, container, false);

        getActivity().getActionBar().setTitle("Live league game");

        LiveLeagueMatch match = (LiveLeagueMatch) getArguments().getSerializable("liveLeagueMatch");

        TextView txtTeams = (TextView) view.findViewById(R.id.txtLiveLeagueTeams);
        txtTeams.setText(Html.fromHtml("<b>" + match.getRadiantTeam().getTeam_name() + "</b>" + " vs " + "<b>" + match.getDireTeam().getTeam_name() + "</b>"));

        //todo: store leagues in DB, only parse if leagueID is unknown
        LeagueListingParser parser = new LeagueListingParser(this);
        parser.execute();

        txtLeague = (TextView) view.findViewById(R.id.txtLiveLeagueLeague);
        txtLeague.setText("League: " + match.getLeague_id());
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
            } else if (Integer.parseInt(d.getTeam()) == 4) {
                numberOfCasters++;
                laySpectators.addView(playerRow);
            }
            if (numberOfCasters == 0) {
                TextView txtCasters = (TextView) view.findViewById(R.id.txtLiveLeagueCasters);
                txtCasters.setText("No casters");
            }
        }
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
}
