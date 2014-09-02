package be.simonraes.dotadata.historyloading;

import android.content.Context;
import android.os.AsyncTask;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.util.AppPreferences;

import java.util.ArrayList;

/**
 * Created by Simon on 19/02/14.
 * AsyncTask class that gets matches (in blocks of 50) from the database and returns them to the listview fragment.
 */
public class DatabaseMatchLoader extends AsyncTask<String, Integer, ArrayList<DetailMatchLite>> {

    private Context context;
    private ASyncResponseDatabase delegate;
    public interface ASyncResponseDatabase {
        public void processFinish(ArrayList<DetailMatchLite> detailMatches);
    }

    public DatabaseMatchLoader(ASyncResponseDatabase delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected ArrayList<DetailMatchLite> doInBackground(String... params) {
        MatchesDataSource mds = new MatchesDataSource(context, AppPreferences.getActiveAccountId(context));
        ArrayList<DetailMatchLite> matchesLite = new ArrayList<DetailMatchLite>();

        if (params.length < 1) {
            matchesLite = mds.get50LiteMatchesStartingFromID(null);
        } else {
            matchesLite = mds.get50LiteMatchesStartingFromID(params[0]);
        }
        return matchesLite;
    }

    @Override
    protected void onPostExecute(ArrayList<DetailMatchLite> detailMatches) {
        super.onPostExecute(detailMatches);
        delegate.processFinish(detailMatches);
    }
}
