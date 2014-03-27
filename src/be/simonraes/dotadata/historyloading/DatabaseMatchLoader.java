package be.simonraes.dotadata.historyloading;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.delegates.ASyncResponseDatabase;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.statistics.DetailMatchLite;
import be.simonraes.dotadata.util.HeroList;

import java.util.ArrayList;

/**
 * Created by Simon on 19/02/14.
 * AsyncTask class that gets matches (in blocks of 50) from the database and returns them to the listview fragment.
 */
public class DatabaseMatchLoader extends AsyncTask<String, Integer, ArrayList<DetailMatchLite>> {

    private Context context;
    private ASyncResponseDatabase delegate;

    public DatabaseMatchLoader(ASyncResponseDatabase delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected ArrayList<DetailMatchLite> doInBackground(String... params) {
        MatchesDataSource mds = new MatchesDataSource(context, PreferenceManager.getDefaultSharedPreferences(context).getString("be.simonraes.dotadata.accountid", ""));
        ArrayList<DetailMatchLite> matchesLite = new ArrayList<DetailMatchLite>();

        if (params.length < 1) {
            matchesLite = mds.get50LiteMatchesStartingFromID(null);
        } else {
            matchesLite = mds.get50LiteMatchesStartingFromID(params[0]);
        }

        System.out.println("got lite matches ");
        for (DetailMatchLite match : matchesLite) {
            System.out.println(HeroList.getHeroName(match.getHero_id()));
        }
        return matchesLite;
    }

    @Override
    protected void onPostExecute(ArrayList<DetailMatchLite> detailMatches) {
        super.onPostExecute(detailMatches);
        delegate.processFinish(detailMatches);
    }
}
