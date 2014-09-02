package be.simonraes.dotadata.async;

import android.content.Context;
import android.os.AsyncTask;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.util.AppPreferences;

/**
 * Loads the requests match details from the database.
 * Created by Simon Raes on 27/08/2014.
 */
public class DetailMatchLoader extends AsyncTask<String, Void, DetailMatch> {

    private Context context;

    private DetailMatchLoaderDelegate delegate;
    public interface DetailMatchLoaderDelegate {
        void loadDone(DetailMatch match);
    }

    public DetailMatchLoader(Context context, DetailMatchLoaderDelegate delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected DetailMatch doInBackground(String... strings) {

        String matchId = strings[0];
        DetailMatch match = null;

        if (matchId != null && !matchId.equals("")) {
            MatchesDataSource mds = new MatchesDataSource(context, AppPreferences.getActiveAccountId(context));
            match = mds.getMatchByID(matchId);
//            match = mds.getMatchByIdWithoutExperience(matchId);
        }

        return match;
    }

    @Override
    protected void onPostExecute(DetailMatch detailMatch) {
        super.onPostExecute(detailMatch);
        delegate.loadDone(detailMatch);
    }
}
