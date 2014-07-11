package be.simonraes.dotadata.statistics;

import android.content.Context;
import android.os.AsyncTask;
import be.simonraes.dotadata.database.AbilityUpgradesDataSource;

/**
 * Created by Simon Raes on 10/07/2014.
 */
public class TeamExperienceGraphLoader extends AsyncTask<String, Void, TeamExperienceStats> {

    public interface ASyncResponseTeamExperience {
        public void processComplete(TeamExperienceStats graphStats);
    }

    private ASyncResponseTeamExperience delegate;
    private Context context;

    public TeamExperienceGraphLoader(Context context, ASyncResponseTeamExperience delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected TeamExperienceStats doInBackground(String... strings) {
        String matchId = strings[0];
        AbilityUpgradesDataSource auds = new AbilityUpgradesDataSource(context);
        auds.getAbilityUpgradesForMatch(matchId);


        return null;
    }

    @Override
    protected void onPostExecute(TeamExperienceStats graphStats) {
        super.onPostExecute(graphStats);
    }
}
