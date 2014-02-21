package be.simonraes.dotadata.historyloading;

import android.content.Context;
import android.os.AsyncTask;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.delegates.ASyncResponseDatabase;
import be.simonraes.dotadata.detailmatch.DetailMatch;

import java.util.ArrayList;

/**
 * Created by Simon on 19/02/14.
 * AsyncTask class that gets matches (in blocks of 50) from the database and returns them to the listview fragment.
 */
public class DatabaseMatchLoader extends AsyncTask<String, Integer, ArrayList<DetailMatch>> {

    private Context context;
    private ASyncResponseDatabase delegate;

    public DatabaseMatchLoader(ASyncResponseDatabase delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected ArrayList<DetailMatch> doInBackground(String... params) {
        MatchesDataSource mds = new MatchesDataSource(context);
        ArrayList<DetailMatch> matches = new ArrayList<DetailMatch>();

        if (params.length < 1) {
            matches = mds.get50MatchesStartingAtMatchID(null);
        } else {
            matches = mds.get50MatchesStartingAtMatchID(params[0]);
        }

        return matches;
    }

    @Override
    protected void onPostExecute(ArrayList<DetailMatch> detailMatches) {
        super.onPostExecute(detailMatches);
        delegate.processFinish(detailMatches);
    }
}
