package be.simonraes.dotadata.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.delegates.ASyncResponsePlayerSummary;
import be.simonraes.dotadata.detailmatch.DetailPlayer;
import be.simonraes.dotadata.parser.PlayerSummaryParser;
import be.simonraes.dotadata.playersummary.PlayerSummaryContainer;
import be.simonraes.dotadata.util.AnimateFirstDisplayListenerToo;
import be.simonraes.dotadata.util.HeroList;
import be.simonraes.dotadata.util.MatchUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

/**
 * Created by Simon Raes on 9/07/2014.
 */
public class PlayerDetailsDialog extends DialogFragment implements ASyncResponsePlayerSummary {

    private DetailPlayer player;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener;

    private View view;
    private TextView txtPlayerName;
    private ImageView imgAvatar;

    private ArrayList<PlayerSummaryParser> parsers;

    public static PlayerDetailsDialog newInstance(DetailPlayer selectedPlayer) {
        PlayerDetailsDialog f = new PlayerDetailsDialog();

        Bundle args = new Bundle();
        args.putParcelable("player", selectedPlayer);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        player = getArguments().getParcelable("player");

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.player_details_layout, container, false);

        parsers = new ArrayList<PlayerSummaryParser>();

        setupHeader();
        setupStats();

        return view;
    }

    private void setupHeader() {

        txtPlayerName = (TextView) view.findViewById(R.id.txtPlayerDetailName);
        ImageView imgHero = (ImageView) view.findViewById(R.id.imgPlayerDetailHero);
        imgAvatar = (ImageView) view.findViewById(R.id.imgPlayerDetailAvatar);

        LinearLayout layPlayerDetailsTeamBar = (LinearLayout) view.findViewById(R.id.layPlayerDetailsTeamBar);

        if (MatchUtils.isRadiant(player)) {
            layPlayerDetailsTeamBar.setBackgroundColor(getResources().getColor(R.color.RadiantGreenTransparent));
        } else {
            layPlayerDetailsTeamBar.setBackgroundColor(getResources().getColor(R.color.DireOrangeTransparent));
        }

        if (player.getAccount_id().equals("4294967295")) {
            txtPlayerName.setText("Anonymous");
        } else {
            PlayerSummaryParser parser = new PlayerSummaryParser(this);
            parser.execute(player.getAccount_id());
            parsers.add(parser);
        }

        imageLoader = ImageLoader.getInstance();
        animateFirstListener = new AnimateFirstDisplayListenerToo();
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(player.getHero_id()) + "_full.png", imgHero, options, animateFirstListener);

    }

    private void setupStats() {
        TextView txtTowerDamage = (TextView) view.findViewById(R.id.txtPlayerDetailTowerDamage);
        txtTowerDamage.setText(player.getTower_damage().toString());
        TextView txtHeroDamage = (TextView) view.findViewById(R.id.txtPlayerDetailHeroDamage);
        txtHeroDamage.setText(player.getHero_damage().toString());
        TextView txtHeroHealing = (TextView) view.findViewById(R.id.txtPlayerDetailHeroHealing);
        txtHeroHealing.setText(player.getHero_healing());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (PlayerSummaryParser parser : parsers) {
            parser.cancel(true);
        }
    }

    @Override
    public void processFinish(PlayerSummaryContainer result) {
        //received player data
        txtPlayerName.setText(result.getPlayers().getPlayers().get(0).getPersonaname());
        imageLoader.displayImage(result.getPlayers().getPlayers().get(0).getAvatarfull(), imgAvatar, options, animateFirstListener);

    }
}
