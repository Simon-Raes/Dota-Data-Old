package be.simonraes.dotadata.async;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.delegates.ASyncResponseStatsLoader;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;

import java.util.ArrayList;

/**
 * Created by Simon Raes on 27/03/2014.
 */
public class StatsMatchesLoader extends AsyncTask<String, Integer, ArrayList<DetailMatchLite>> {

    private Context context;
    private ASyncResponseStatsLoader delegate;


    public StatsMatchesLoader(ASyncResponseStatsLoader delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected ArrayList<DetailMatchLite> doInBackground(String... params) {

        MatchesDataSource mds = new MatchesDataSource(context, PreferenceManager.getDefaultSharedPreferences(context).getString("be.simonraes.dotadata.accountid", ""));
        ArrayList<DetailMatchLite> matches = new ArrayList<DetailMatchLite>();

        String gameModeID = params[0];
        String heroID = params[1];

        if (gameModeID == null || gameModeID == "") {
            gameModeID = "-1";
        }
        if (heroID == null || heroID == "") {
            heroID = "-1";
        }

        //neither selected, get statsrecords for all games
        if (Integer.parseInt(gameModeID) < 1 && Integer.parseInt(heroID) < 1) {
            matches = mds.getAllDetailMatchesLite();
        }
        //only gamemode selected
        else if (Integer.parseInt(gameModeID) > 0 && Integer.parseInt(heroID) < 1) {
            matches = mds.getAllStatRecordsForGameMode(gameModeID);
        }
        //only hero selected
        else if (Integer.parseInt(gameModeID) < 1 && Integer.parseInt(heroID) > 0) {
            matches = mds.getAllStatRecordsForHero(heroID);
        }
        //both selected
        else {
            matches = mds.getAllStatRecordsForHeroAndGameMode(heroID, gameModeID);
        }

        System.out.println("async found x matches " + matches.size());

        return matches;
    }

    @Override
    protected void onPostExecute(ArrayList<DetailMatchLite> detailMatches) {
        super.onPostExecute(detailMatches);


        delegate.processFinish(detailMatches);
    }
}
