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
    private String[] matchcolumns = {
            MySQLiteHelper.TABLE_MATCHES_COLUMN_MATCHID,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_RADIANT_WIN,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_DURATION,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_START_TIME,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_MATCH_SEQ_NUM,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_TOWER_STATUS_RADIANT,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_TOWER_STATUS_DIRE,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_CLUSTER,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_FIRST_BLOOD_TIME,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_LOBBY_TYPE,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_HUMAN_PLAYERS,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_LEAGUEID,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_POSITIVE_VOTES,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_NEGATIVE_VOTES,
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

    public void saveDetailMatch(DetailMatch match) {

        //convert String to int for use as key
        int matchIDForTable = Integer.parseInt(match.getMatch_id());
        String radiantWinForTable = String.valueOf(match.getRadiant_win());

        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_RADIANT_WIN, radiantWinForTable);
//        System.out.println("put " + radiantWinForTable);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_DURATION, match.getDuration());
        //System.out.println("put " + match.getDuration());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_START_TIME, match.getStart_time());
        // System.out.println("put " + match.getStart_time());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_MATCHID, matchIDForTable);
        //System.out.println("put " + matchIDForTable);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_MATCH_SEQ_NUM, match.getMatch_seq_num());
        //       System.out.println("put " + match.getMatch_seq_num());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_TOWER_STATUS_RADIANT, match.getTower_status_radiant());
        //     System.out.println("put " + match.getTower_status_radiant());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_TOWER_STATUS_DIRE, match.getTower_status_dire());
        //   System.out.println("put " + match.getTower_status_dire());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_BARRACKS_STATUS_RADIANT, match.getBarracks_status_radiant());
        // System.out.println("put " + match.getBarracks_status_radiant());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_BARRACKS_STATUS_DIRE, match.getBarracks_status_dire());
        // System.out.println("put " + match.getBarracks_status_dire());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_CLUSTER, match.getCluster());
        //System.out.println("put " + match.getCluster());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_FIRST_BLOOD_TIME, match.getFirst_blood_time());
        //System.out.println("put " + match.getFirst_blood_time());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_LOBBY_TYPE, match.getLobby_type());
        //System.out.println("put " + match.getLobby_type());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_HUMAN_PLAYERS, match.getHuman_players());
        //  System.out.println("put " + match.getHuman_players());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_LEAGUEID, match.getLeagueid());
        //  System.out.println("put " + match.getLeagueid());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_POSITIVE_VOTES, match.getPositive_votes());
        //System.out.println("put " + match.getPositive_votes());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_NEGATIVE_VOTES, match.getNegative_votes());
        //  System.out.println("put " + match.getNegative_votes());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_GAME_MODE, match.getGame_mode());
        //  System.out.println("put " + match.getGame_mode());

        System.out.println("saving match " + match.getMatch_id());
        database.insertWithOnConflict(MySQLiteHelper.TABLE_MATCHES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void saveDetailMatches(ArrayList<DetailMatch> matches) {
        open();
        database.beginTransaction();
        for (DetailMatch match : matches) {
            saveDetailMatch(match);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close();
    }

    public ArrayList<DetailMatch> getAllMatches() {

        ArrayList<DetailMatch> matches = new ArrayList<DetailMatch>();
        open();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_MATCHES, matchcolumns, null, null, null, null, null);
        close();
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

    public boolean recordExists(DetailMatch match) {
        String matchID = match.getMatch_id();
        Cursor cursor = database.rawQuery("select 1 from matches where match_id = ?", new String[]{matchID});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public DetailMatch getMatchByID(String matchID) {
        DetailMatch dmb = new DetailMatch();
        Cursor cs = database.query(MySQLiteHelper.TABLE_MATCHES, matchcolumns, "match_id = " + matchID, null, null, null, null);
        cs.moveToFirst();
        while (!cs.isAfterLast()) {
            dmb = cursorToDetailMatchBag(cs);
            cs.moveToNext();
        }
        cs.close();
        return dmb;
    }

    public DetailMatch getLatestMatch() {
        open();
        DetailMatch dmb = new DetailMatch();
        Cursor cursor = database.rawQuery("SELECT * FROM matches ORDER BY match_id DESC LIMIT 1;", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            dmb = cursorToDetailMatchBag(cursor);
            cursor.moveToNext();
        }
        cursor.close();
        System.out.println("returing latest match, id is " + dmb.getMatch_id());
        close();
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

    public int getNumberOfRecords() {
        open();
        Cursor cursor = database.rawQuery("select count(*) from matches", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        close();
        return count;

    }

    public void deleteLatestMatch() {
        String matchID = getLatestMatch().getMatch_id();
        System.out.println("got match id to delete: " + matchID);
        open();
        database.delete(MySQLiteHelper.TABLE_MATCHES, "match_id" + "=?", new String[]{matchID});
        close();

    }
}
