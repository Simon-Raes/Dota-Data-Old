package be.simonraes.dotadata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    private String[] matchColumns = {MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES, MySQLiteHelper.TABLE_MATCHES_COLUMN_DURATION};

    public PlayersInMatchesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createPlayer(DetailPlayer player, String matchID) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_PIM_ID, player.getAccount_id() + matchID + player.getPlayer_slot());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_ACCOUNTID, player.getAccount_id());
        values.put(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES_COLUMN_MATCHID, matchID);
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

    public ArrayList<DetailPlayer> getAllHeroesInMatch(String matchID) {
        ArrayList<DetailPlayer> players = new ArrayList();

        Cursor cursor = database.rawQuery("select * from player_in_match where match_id = ?", new String[]{matchID});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DetailPlayer dhb = cursorToDetailHeroBag(cursor);
            players.add(dhb);
            cursor.moveToNext();
        }
        cursor.close();

        return players;
    }

    public boolean recordExists(DetailPlayer player, String MatchID) {
        String localKey = player.getAccount_id() + MatchID + player.getPlayer_slot();
        Cursor cursor = database.rawQuery("select 1 from player_in_match where pim_id = ?", new String[]{localKey});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    private DetailPlayer cursorToDetailHeroBag(Cursor cursor) {
        DetailPlayer player = new DetailPlayer();

        player.setAccount_id(cursor.getString(1));
        player.setPlayer_slot(cursor.getString(3));
        player.setHero_id(cursor.getString(4));
        player.setItem_0(cursor.getString(5));
        player.setItem_1(cursor.getString(6));
        player.setItem_2(cursor.getString(7));
        player.setItem_3(cursor.getString(8));
        player.setItem_4(cursor.getString(9));
        player.setItem_5(cursor.getString(10));
        player.setKills(cursor.getString(11));
        player.setDeaths(cursor.getString(12));
        player.setAssists(cursor.getString(13));
        player.setLeaver_status(cursor.getString(14));
        player.setGold(cursor.getString(15));
        player.setLast_hits(cursor.getString(16));
        player.setDenies(cursor.getString(17));
        player.setGold_per_min(cursor.getString(18));
        player.setXp_per_min(cursor.getString(19));
        player.setGold_spent(cursor.getString(20));
        player.setHero_damage(cursor.getString(21));
        player.setTower_damage(cursor.getString(22));
        player.setHero_healing(cursor.getString(23));
        player.setLevel(cursor.getString(24));

        return player;
    }
}
