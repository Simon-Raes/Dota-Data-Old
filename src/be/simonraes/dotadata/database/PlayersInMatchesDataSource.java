package be.simonraes.dotadata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import be.simonraes.dotadata.detailmatch.DetailPlayer;

import java.util.ArrayList;

/**
 * Created by Simon on 16/02/14.
 */
public class PlayersInMatchesDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] playersColumns = {
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_PIM_ID,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ACCOUNTID,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_MATCHID,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_PLAYER_SLOT,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_HERO_ID,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM0,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM1,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM2,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM3,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM4,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM5,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_KILLS,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_DEATHS,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ASSISTS,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_LEAVER_STATUS,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_GOLD,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_LAST_HITS,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_DENIES,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_GOLD_PER_MIN,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_XP_PER_MIN,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_GOLD_SPENT,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_HERO_DAMAGE,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_TOWER_DAMAGE,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_HERO_HEALING,
            MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_LEVEL};

    public PlayersInMatchesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void savePlayer(DetailPlayer player) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_PIM_ID, player.getMatchID() + player.getAccount_id() + player.getPlayer_slot()); //need all 3 to be unique (anons in match have same accountID-
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ACCOUNTID, player.getAccount_id());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_MATCHID, player.getMatchID());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_PLAYER_SLOT, player.getPlayer_slot());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_HERO_ID, player.getHero_id());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM0, player.getItem_0());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM1, player.getItem_1());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM2, player.getItem_2());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM3, player.getItem_3());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM4, player.getItem_4());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM5, player.getItem_5());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_KILLS, player.getKills());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_DEATHS, player.getDeaths());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ASSISTS, player.getAssists());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_LEAVER_STATUS, player.getLeaver_status());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_GOLD, player.getGold());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_LAST_HITS, player.getLast_hits());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_DENIES, player.getDenies());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_GOLD_PER_MIN, player.getGold_per_min());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_XP_PER_MIN, player.getXp_per_min());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_GOLD_SPENT, player.getGold_spent());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_HERO_DAMAGE, player.getHero_damage());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_TOWER_DAMAGE, player.getTower_damage());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_HERO_HEALING, player.getHero_healing());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_LEVEL, player.getLevel());

        database.insertWithOnConflict(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void savePlayers(ArrayList<DetailPlayer> players) {
        open();

        database.beginTransaction();
        try {
            for (DetailPlayer player : players) {
                savePlayer(player);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        close();
    }

}
