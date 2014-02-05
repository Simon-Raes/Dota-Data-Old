package be.simonraes.dotadata.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.liveleaguegame.LiveLeagueMatch;

/**
 * Created by Simon on 5/02/14.
 */
public class LiveLeagueGameFragment extends Fragment {

    private TextView txtTeams, txtLeague;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.liveleaguegame_layout, container, false);

        LiveLeagueMatch match = (LiveLeagueMatch) getArguments().getSerializable("liveLeagueMatch");

        txtTeams = (TextView) view.findViewById(R.id.txtLiveLeagueTeams);
        txtTeams.setText(match.getRadiantTeam().getTeam_name()+" vs "+match.getDireTeam().getTeam_name());

        //todo: get league name from https://api.steampowered.com/IDOTA2Match_570/GetLeagueListing/v0001/?key=EB5773FAAF039592D9383FA104EEA55D
        txtLeague = (TextView) view.findViewById(R.id.txtLiveLeagueLeague);
        txtLeague.setText(match.getLeague_id());

        return view;
    }
}
