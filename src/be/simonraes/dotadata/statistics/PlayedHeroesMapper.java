package be.simonraes.dotadata.statistics;

import android.content.Context;
import android.os.AsyncTask;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.util.AppPreferences;
import be.simonraes.dotadata.util.GameModes;
import be.simonraes.dotadata.util.HeroList;

/**
 * Used to load a hashmap of played heroes. Loading this in the background when the app starts prevents the app from freezing when opening the stats screen the first time.
 * Created by Simon Raes on 28/03/2014.
 */
public class PlayedHeroesMapper extends AsyncTask<String, Integer, PlayedHeroesAndGameModes> {

    private Context context;
    private static PlayedHeroesMapper mapper;
    private static PlayedHeroesAndGameModes maps;


    private PlayedHeroesMapper(Context context) {
        this.context = context;
        maps = new PlayedHeroesAndGameModes();
    }

    public static PlayedHeroesMapper getInstance(Context context) {
        if (mapper == null) {
            mapper = new PlayedHeroesMapper(context);
        }
        return mapper;
    }

    public static void clearInstance() {
        mapper = null;
    }


    public static PlayedHeroesAndGameModes getMaps() {
        return maps;
    }

    @Override
    protected PlayedHeroesAndGameModes doInBackground(String... params) {
        MatchesDataSource mds = new MatchesDataSource(context, AppPreferences.getActiveAccountId(context));

        for (DetailMatchLite rec : mds.getAllDetailMatchesLite()) {
            //don't store "unknown hero"
            try {
                if (Integer.parseInt(rec.getHero_id()) > 0) {
                    maps.getPlayedHeroes().put(rec.getHero_id(), HeroList.getHeroName(rec.getHero_id()));
                }
            } catch (NumberFormatException ex) {
                //hero_id did not contain a number, no need to add anything to the playedHeroesList
            }

            maps.getPlayedGameModes().put(rec.getGame_mode(), GameModes.getGameMode(rec.getGame_mode()));
        }
        return maps;
    }

    @Override
    protected void onPostExecute(PlayedHeroesAndGameModes v) {
        super.onPostExecute(v);
    }
}
