package be.simonraes.dotadata.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by Simon on 5/02/14.
 */
public class LiveLeagueGameFragment extends Fragment implements ASyncResponseLeagueListing {

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener;

    private LinearLayout layPlayersRadiant, layPlayersDire, laySpectators;

    private TextView txtTeams, txtLeague, txtSpectatorCount;
    private ImageView imgRadiantLogo, imgDireLogo;

    private TextView txtPlayerName;
    private ImageView imgHero;

    private String leagueID = "";
    private String leagueName = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.liveleaguegame_layout, container, false);


        getActivity().getActionBar().setTitle("Live league game");

        LiveLeagueMatch match = (LiveLeagueMatch) getArguments().getSerializable("liveLeagueMatch");

        txtTeams = (TextView) view.findViewById(R.id.txtLiveLeagueTeams);
        txtTeams.setText(Html.fromHtml("<b>" + match.getRadiantTeam().getTeam_name() + "</b>" + " vs " + "<b>" + match.getDireTeam().getTeam_name() + "</b>"));

        txtLeague = (TextView) view.findViewById(R.id.txtLiveLeagueLeague);
        if (!leagueName.equals("")) {
            txtLeague.setText(Conversions.leagueTitleToString(leagueName));

            SteamRemoteStorageParser logoParser;

            if (!match.getRadiantTeam().getTeam_logo().equals("0")) {
                imgRadiantLogo = (ImageView) view.findViewById(R.id.imgLiveLeagueDetailsRadiantLogo);
                logoParser = new SteamRemoteStorageParser(imgRadiantLogo);
                logoParser.execute(match.getRadiantTeam().getTeam_logo());
            }
            if (!match.getDireTeam().getTeam_logo().equals("0")) {
                imgDireLogo = (ImageView) view.findViewById(R.id.imgLiveLeagueDetailsDireLogo);
                logoParser = new SteamRemoteStorageParser(imgDireLogo);
                logoParser.execute(match.getDireTeam().getTeam_logo());
            }


        } else {
            //get league name
            //todo: store leagues in DB, only parse if leagueID is unknown
            LeagueListingParser parser = new LeagueListingParser(this);
            parser.execute();

            leagueID = match.getLeague_id();
            txtLeague.setText("League: " + leagueID);
        }

        txtSpectatorCount = (TextView) view.findViewById(R.id.txtLiveLeagueSpectatorCount);
        txtSpectatorCount.setText("DotaTV spectators: " + match.getSpectators());

        layPlayersRadiant = (LinearLayout) view.findViewById(R.id.layLiveLeagueRadiantPlayers);
        layPlayersDire = (LinearLayout) view.findViewById(R.id.layLiveLeagueDirePlayers);
        laySpectators = (LinearLayout) view.findViewById(R.id.layLiveLeagueSpectators);

        for (LiveLeaguePlayer d : match.getLiveLeaguePlayers()) {
            View playerRow = inflater.inflate(R.layout.liveleaguegame_player_row, null);

            txtPlayerName = (TextView) playerRow.findViewById(R.id.txtLiveLeaguePlayerName);
            txtPlayerName.setText(d.getName());

            //no hero image for spectators
            //todo: only show casters (team: 4) names, only show title when there is at lest 1
            if (!d.getHero_id().equals("0")) {

                imageLoader = ImageLoader.getInstance();

                //heroloading options
                options = new DisplayImageOptions.Builder()
                        .resetViewBeforeLoading(true)
                        .cacheInMemory(true)
                        .showImageOnLoading(R.drawable.hero_sb_loading)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .build();
                animateFirstListener = new AnimateFirstDisplayListenerToo();

                imgHero = (ImageView) playerRow.findViewById(R.id.imgLiveLeagueDetailHero);
                imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(d.getHero_id()) + "_sb.png", imgHero, options, animateFirstListener);
            }

            if (Integer.parseInt(d.getTeam()) == 0) {
                layPlayersRadiant.addView(playerRow);
            } else if (Integer.parseInt(d.getTeam()) == 1) {
                layPlayersDire.addView(playerRow);
            } else {
                laySpectators.addView(playerRow);
            }
        }

        return view;
    }

    //reload View when leagueName is found
    @Override
    public void processFinish(LeagueListingContainer result) {
        for (League l : result.getLeagues().getLeagues()) {
            if (l.getLeagueid().equals(leagueID)) {
                leagueName = l.getName();

                //reload current fragment to display league name
                Fragment currentFragment = this;
                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();
            }
        }

    }
}
