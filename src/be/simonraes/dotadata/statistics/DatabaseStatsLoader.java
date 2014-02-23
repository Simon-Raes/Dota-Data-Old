package be.simonraes.dotadata.statistics;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.delegates.ASyncResponseDatabase;
import be.simonraes.dotadata.detailmatch.DetailMatch;

import java.util.ArrayList;

/**
 * Created by Simon on 20/02/14.
 * ASyncTask that loads all matches from the database
 */
public class DatabaseStatsLoader extends AsyncTask<String, Integer, ArrayList<DetailMatch>> {

    private Context context;
    private ASyncResponseDatabase delegate;

    public DatabaseStatsLoader(ASyncResponseDatabase delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected ArrayList<DetailMatch> doInBackground(String... params) {

        MatchesDataSource mds = new MatchesDataSource(context, PreferenceManager.getDefaultSharedPreferences(context).getString("be.simonraes.dotadata.accountid", ""));
        ArrayList<DetailMatch> matches = mds.getAllMatches();

        return matches;
    }

    @Override
    protected void onPostExecute(ArrayList<DetailMatch> detailMatches) {
        super.onPostExecute(detailMatches);
        delegate.processFinish(detailMatches);
    }
}
