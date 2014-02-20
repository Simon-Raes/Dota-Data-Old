package be.simonraes.dotadata.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.delegates.ASyncResponseHistoryLoader;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.historyloading.HistoryLoader;
import be.simonraes.dotadata.statistics.GameModeStats;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.GameModes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Simon on 14/02/14.
 */
public class StatsFragment extends Fragment implements View.OnClickListener {

    private Button btnUpdateMatches, btnClearDatabase;
    private Button btnNumberOfRecords, btnDeleteLatestMatch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stats_layout, null);

        getActivity().getActionBar().setTitle("Statistics");

        btnUpdateMatches = (Button) view.findViewById(R.id.btnUpdateMatches);
        btnUpdateMatches.setOnClickListener(this);

        btnClearDatabase = (Button) view.findViewById(R.id.btnClearDatabase);
        btnClearDatabase.setOnClickListener(this);

        btnNumberOfRecords = (Button) view.findViewById(R.id.btnNumberOfRecords);
        btnNumberOfRecords.setOnClickListener(this);

        btnDeleteLatestMatch = (Button) view.findViewById(R.id.btnDeleteLatestMatch);
        btnDeleteLatestMatch.setOnClickListener(this);


        //todo: load stats in background thread


        MatchesDataSource mds = new MatchesDataSource(getActivity());
        ArrayList<DetailMatch> matches = mds.getAllMatches();
        HashMap<String, GameModeStats> gameModeStatsList = new HashMap<String, GameModeStats>();

        int totalWins = 0, totalLosses = 0;
        for (DetailMatch match : matches) {
            //winrate
            if (match.isUser_win()) {
                totalWins++;
            } else {
                totalLosses++;
            }

            //gamemode stats
            if (gameModeStatsList.containsKey(match.getGame_mode())) {
                GameModeStats gameMode = gameModeStatsList.get(match.getGame_mode());

                //number of games
                gameMode.setNumberOfGames(gameMode.getNumberOfGames() + 1);

                //wins-losses-winrate
                if (match.isUser_win()) {
                    gameMode.setWins(gameMode.getWins() + 1);
                } else {
                    gameMode.setLosses(gameMode.getLosses() + 1);
                }

                //longest game
                if (Integer.parseInt(match.getDuration()) > gameMode.getLongestMatch()) {
                    gameMode.setLongestMatch(Integer.parseInt(match.getDuration()));
                }
            } else {
                GameModeStats gameMode = new GameModeStats();
                gameMode.setNumberOfGames(1);
                if (match.isUser_win()) {
                    gameMode.setWins(1);
                } else {
                    gameMode.setLosses(1);
                }
                gameMode.setLongestMatch(Integer.parseInt(match.getDuration()));
                gameModeStatsList.put(match.getGame_mode(), gameMode);
            }


        }
        double winrate = (double) totalWins / ((double) totalWins + (double) totalLosses);
        DecimalFormat formatWinrate = new DecimalFormat("#.00");

        TextView txtWinRate = (TextView) view.findViewById(R.id.txtStatsWinrate);
        txtWinRate.setText("wins: " + totalWins + " losses: " + totalLosses + " winrate: " + formatWinrate.format(winrate * 100) + "%");

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, GameModeStats> entry : gameModeStatsList.entrySet()) {
            String key = entry.getKey();
            GameModeStats value = entry.getValue();
            builder.append(GameModes.getGameMode(key) + " - Wins: " + value.getWins() + " - Losses: " + value.getLosses() + " - Winrate: " + formatWinrate.format(value.getWinrate() * 100) + "%\n");
        }
        TextView txtGameModes = (TextView) view.findViewById(R.id.txtStatsGameModes);
        txtGameModes.setText(builder.toString());


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdateMatches:
                HistoryLoader loader = new HistoryLoader(getActivity());
                loader.updateHistory();
                break;
            case R.id.btnClearDatabase:
                getActivity().deleteDatabase("be.simonraes.dotadata.db");
                break;
            case R.id.btnNumberOfRecords:
                MatchesDataSource mds = new MatchesDataSource(getActivity());
                Toast.makeText(getActivity(), Integer.toString(mds.getNumberOfRecords()), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnDeleteLatestMatch:
                MatchesDataSource mds2 = new MatchesDataSource(getActivity());
                mds2.deleteLatestMatch();
                break;
            default:
                break;
        }
    }


}
