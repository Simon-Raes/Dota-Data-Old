package be.simonraes.dotadata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import be.simonraes.dotadata.detailmatch.DetailMatch;

import java.util.ArrayList;

/**
 * Created by Simon on 15/02/14.
 */
public class MatchesDataSource {


    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] matchcolumns = {MySQLiteHelper.TABLE_MATCHES_COLUMN_MATCHID, MySQLiteHelper.TABLE_MATCHES_COLUMN_RADIANT_WIN,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_DURATION, MySQLiteHelper.TABLE_MATCHES_COLUMN_START_TIME, MySQLiteHelper.TABLE_MATCHES_COLUMN_MATCH_SEQ_NUM,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_TOWER_STATUS_RADIANT, MySQLiteHelper.TABLE_MATCHES_COLUMN_TOWER_STATUS_DIRE, MySQLiteHelper.TABLE_MATCHES_COLUMN_CLUSTER,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_FIRST_BLOOD_TIME, MySQLiteHelper.TABLE_MATCHES_COLUMN_LOBBY_TYPE, MySQLiteHelper.TABLE_MATCHES_COLUMN_HUMAN_PLAYERS,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_LEAGUEID, MySQLiteHelper.TABLE_MATCHES_COLUMN_POSITIVE_VOTES, MySQLiteHelper.TABLE_MATCHES_COLUMN_NEGATIVE_VOTES,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_GAME_MODE};

    public MatchesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createDetailMatchBag(String match_id, boolean radiant_win, String duration, String start_time, String match_seq_num,
                                     String tower_status_radiant, String tower_status_dire, String cluster, String first_blood_time, String lobby_type, String human_players,
                                     String leagueid, String positive_votes, String negative_votes, String game_mode) {
        //matchid komt binnen als string maar wordt de primary key als INTEGER!
        int matchidfortable = Integer.parseInt(match_id);
        String booleanfortable = String.valueOf(radiant_win);

        ContentValues values = new ContentValues();


        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_RADIANT_WIN, booleanfortable);
        System.out.println("put " + booleanfortable);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_DURATION, duration);
        System.out.println("put " + duration);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_START_TIME, start_time);
        System.out.println("put " + start_time);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_MATCHID, matchidfortable);
        System.out.println("put " + matchidfortable);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_MATCH_SEQ_NUM, match_seq_num);
        System.out.println("put " + match_seq_num);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_TOWER_STATUS_RADIANT, tower_status_radiant);
        System.out.println("put " + tower_status_radiant);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_TOWER_STATUS_DIRE, tower_status_dire);
        System.out.println("put " + tower_status_dire);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_BARRACKS_STATUS_RADIANT, tower_status_radiant);
        System.out.println("put " + tower_status_radiant);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_BARRACKS_STATUS_DIRE, tower_status_dire);
        System.out.println("put " + tower_status_dire);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_CLUSTER, cluster);
        System.out.println("put " + cluster);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_FIRST_BLOOD_TIME, first_blood_time);
        System.out.println("put " + first_blood_time);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_LOBBY_TYPE, lobby_type);
        System.out.println("put " + lobby_type);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_HUMAN_PLAYERS, human_players);
        System.out.println("put " + human_players);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_LEAGUEID, leagueid);
        System.out.println("put " + leagueid);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_POSITIVE_VOTES, positive_votes);
        System.out.println("put " + positive_votes);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_NEGATIVE_VOTES, negative_votes);
        System.out.println("put " + negative_votes);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_GAME_MODE, game_mode);
        System.out.println("put " + game_mode);

        database.insertWithOnConflict(MySQLiteHelper.TABLE_MATCHES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    //beter als je dit per 25 doet, dan meerdere "paginas" bij de listview, telkens 25 games tonen en loaden als je de onderkant bereikt
    public ArrayList<DetailMatch> getAllMatches() {
        ArrayList<DetailMatch> matches = new ArrayList<DetailMatch>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_MATCHES, matchcolumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DetailMatch dmb = cursorToDetailMatchBag(cursor);
            matches.add(dmb);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return matches;
    }

    public boolean recordExists(DetailMatch matchhere) {
        String matchid = matchhere.getMatch_id();
        Cursor cursor = database.rawQuery("select 1 from matches where match_id = ?", new String[]{matchid});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public DetailMatch getMatchByID(String matchid) {
        DetailMatch dmb = new DetailMatch();
        Cursor cs = database.query(MySQLiteHelper.TABLE_MATCHES, matchcolumns, "match_id = " + matchid, null, null, null, null);
        cs.moveToFirst();
        System.out.println(cs.toString());
        while (!cs.isAfterLast()) {
            dmb.setMatch_id(cs.getString(0));


            boolean status = Boolean.parseBoolean(cs.getString(1));
            dmb.setRadiant_win(status);
            dmb.setDuration(cs.getString(2));
            dmb.setStart_time(cs.getString(3));
            dmb.setMatch_seq_num(cs.getString(4));
            dmb.setTower_status_radiant(cs.getString(5));
            dmb.setTower_status_dire(cs.getString(6));
            dmb.setBarracks_status_radiant(cs.getString(7));
            dmb.setBarracks_status_dire(cs.getString(8));
            dmb.setCluster(cs.getString(9));
            dmb.setFirst_blood_time(cs.getString(10));
            dmb.setLobby_type(cs.getString(11));
            dmb.setHuman_players(cs.getString(12));
            dmb.setLeagueid(cs.getString(13));
            dmb.setPositive_votes(cs.getString(14));
            dmb.setNegative_votes(cs.getString(15));
            dmb.setGame_mode(cs.getString(16));

            cs.moveToNext();
        }
        cs.close();
        return dmb;
    }

    private DetailMatch cursorToDetailMatchBag(Cursor cursor) {
        DetailMatch dmb = new DetailMatch();
        dmb.setMatch_id(Integer.toString(cursor.getInt(0)));

        dmb.setRadiant_win(Boolean.parseBoolean(cursor.getString(1)));
        dmb.setDuration(cursor.getString(2));
        dmb.setStart_time(cursor.getString(3));
        dmb.setMatch_seq_num(cursor.getString(4));
        dmb.setTower_status_radiant(cursor.getString(5));
        dmb.setTower_status_dire(cursor.getString(6));
        dmb.setBarracks_status_radiant(cursor.getString(7));
        dmb.setBarracks_status_dire(cursor.getString(8));
        dmb.setCluster(cursor.getString(9));
        dmb.setFirst_blood_time(cursor.getString(10));
        dmb.setLobby_type(cursor.getString(11));
        dmb.setHuman_players(cursor.getString(12));
        dmb.setLeagueid(cursor.getString(13));
        dmb.setPositive_votes(cursor.getString(14));
        dmb.setNegative_votes(cursor.getString(15));
        dmb.setGame_mode(cursor.getString(16));

        return dmb;
    }
}
