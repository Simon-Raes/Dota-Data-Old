package be.simonraes.dotadata.async;

import android.content.Context;
import android.os.AsyncTask;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.util.AppPreferences;

import java.util.ArrayList;

/**
 * Loads all needed games from the database to calculate the statistics.
 * Created by Simon Raes on 27/03/2014.
 */
public class StatsMatchesLoader extends AsyncTask<String, Integer, ArrayList<DetailMatchLite>> {

    private Context context;
    private ASyncResponseStatsLoader delegate;
    public interface ASyncResponseStatsLoader {
        public void processFinish(ArrayList<DetailMatchLite> result);
    }

    public StatsMatchesLoader(ASyncResponseStatsLoader delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected ArrayList<DetailMatchLite> doInBackground(String... params) {

        MatchesDataSource mds = new MatchesDataSource(context, AppPreferences.getAccountID(context));
        ArrayList<DetailMatchLite> matches;

        String gameModeID = params[0];
        String heroID = params[1];

        if (gameModeID == null || gameModeID.equals("")) {
            gameModeID = "-1";
        }
        if (heroID == null || heroID.equals("")) {
            heroID = "-1";
        }

        //neither selected, get statsrecords for all games
        if (Integer.parseInt(gameModeID) < 1 && Integer.parseInt(heroID) < 1) {
            matches = mds.getAllRealDetailMatchesLite();
        }
        //only gamemode selected
        else if (Integer.parseInt(gameModeID) > 0 && Integer.parseInt(heroID) < 1) {
            matches = mds.getAllRealDetailMatchesLiteForGameMode(gameModeID);
        }
        //only hero selected
        else if (Integer.parseInt(gameModeID) < 1 && Integer.parseInt(heroID) > 0) {
            matches = mds.getAllRealDetailMatchesLiteForHero(heroID);
        }
        //both selected
        else {
            matches = mds.getAllRealDetailMatchesLiteForHeroAndGameMode(heroID, gameModeID);
        }

        return matches;
    }

    @Override
    protected void onPostExecute(ArrayList<DetailMatchLite> detailMatches) {
        super.onPostExecute(detailMatches);
        delegate.processFinish(detailMatches);
    }
}
