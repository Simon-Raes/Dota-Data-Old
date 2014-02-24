package be.simonraes.dotadata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.detailmatch.DetailPlayer;
import be.simonraes.dotadata.detailmatch.PicksBans;

import java.util.ArrayList;

/**
 * Created by Simon on 15/02/14.
 */
public class MatchesDataSource {

    private Context context;


    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] matchesColumns = {
            MySQLiteHelper.TABLE_MATCHES_COLUMN_RADIANT_WIN,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_DURATION,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_START_TIME,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_MATCH_ID,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_MATCH_SEQ_NUM,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_TOWER_STATUS_RADIANT,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_TOWER_STATUS_DIRE,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_BARRACKS_STATUS_RADIANT,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_BARRACKS_STATUS_DIRE,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_CLUSTER,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_FIRST_BLOOD_TIME,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_LOBBY_TYPE,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_HUMAN_PLAYERS,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_LEAGUEID,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_POSITIVE_VOTES,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_NEGATIVE_VOTES,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_GAME_MODE,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_USER_WIN,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_FAVOURITE,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_NOTE,
            MySQLiteHelper.TABLE_MATCHES_COLUMN_USER};


    //todo: need middle layer/class so this can be removed
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

    //todo: needs same
    private String[] picksBansColumns = {
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_MATCH_ID,
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_IS_PICK,
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_HERO_ID,
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_TEAM,
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_ORDER
    };

    private String user_accountID;

    public MatchesDataSource(Context context, String user_accountID) {
        this.context = context;
        this.user_accountID = user_accountID;
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
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_DURATION, match.getDuration());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_START_TIME, match.getStart_time());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_MATCH_ID, matchIDForTable);
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_MATCH_SEQ_NUM, match.getMatch_seq_num());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_TOWER_STATUS_RADIANT, match.getTower_status_radiant());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_TOWER_STATUS_DIRE, match.getTower_status_dire());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_BARRACKS_STATUS_RADIANT, match.getBarracks_status_radiant());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_BARRACKS_STATUS_DIRE, match.getBarracks_status_dire());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_CLUSTER, match.getCluster());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_FIRST_BLOOD_TIME, match.getFirst_blood_time());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_LOBBY_TYPE, match.getLobby_type());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_HUMAN_PLAYERS, match.getHuman_players());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_LEAGUEID, match.getLeagueid());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_POSITIVE_VOTES, match.getPositive_votes());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_NEGATIVE_VOTES, match.getNegative_votes());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_GAME_MODE, match.getGame_mode());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_USER_WIN, String.valueOf(match.isUser_win()));
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_FAVOURITE, String.valueOf(match.isFavourite()));
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_NOTE, match.getNote());
        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_USER, user_accountID);

//        System.out.println("saving match " + match.getMatch_id());
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

    //todo: add extra class/layer that gets extra match records so classes can stay separate

    public ArrayList<DetailMatch> getAllMatches() {

        ArrayList<DetailMatch> matches = new ArrayList<DetailMatch>();
        PlayersInMatchesDataSource pimds = new PlayersInMatchesDataSource(context);
        open();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_MATCHES, matchesColumns, "user = ?", new String[]{user_accountID}, null, null, "match_id DESC", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DetailMatch detailMatch = cursorToDetailMatch(cursor);


            //test get players for all matches
            Cursor cursorPlayers = database.query(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES, playersColumns, "match_id = ?", new String[]{detailMatch.getMatch_id()}, null, null, null, null);
            ArrayList<DetailPlayer> players = new ArrayList<DetailPlayer>();
            cursorPlayers.moveToFirst();

            while (!cursorPlayers.isAfterLast()) {
                DetailPlayer detailPlayer = cursorToDetailHeroBag(cursorPlayers);
                players.add(detailPlayer);
                cursorPlayers.moveToNext();
            }
            cursorPlayers.close();
            detailMatch.setPlayers(players);

            matches.add(detailMatch);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        close();
        return matches;
    }

    //todo: add extra class/layer that gets extra match records so classes can stay separate


    public ArrayList<DetailMatch> get50MatchesStartingAtMatchID(String matchID) {

        ArrayList<DetailMatch> matches = new ArrayList<DetailMatch>();
        open();
        Cursor cursor;
        if (matchID != null && !matchID.equals("")) {
            cursor = database.query(MySQLiteHelper.TABLE_MATCHES, matchesColumns, "match_id < ? AND user = ?", new String[]{matchID, user_accountID}, null, null, "match_id DESC", "50");
        } else {
            cursor = database.query(MySQLiteHelper.TABLE_MATCHES, matchesColumns, "user = ?", new String[]{user_accountID}, null, null, "match_id DESC", "50");
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DetailMatch detailMatch = cursorToDetailMatch(cursor);

            //get players for all matches
            Cursor cursorPlayers = database.query(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES, playersColumns, "match_id = ?", new String[]{detailMatch.getMatch_id()}, null, null, null, null);
            ArrayList<DetailPlayer> players = new ArrayList<DetailPlayer>();
            cursorPlayers.moveToFirst();
            while (!cursorPlayers.isAfterLast()) {
                DetailPlayer detailPlayer = cursorToDetailHeroBag(cursorPlayers);
                players.add(detailPlayer);
                cursorPlayers.moveToNext();
            }
            cursorPlayers.close();
            detailMatch.setPlayers(players);


            //get picks/bans for all matches
            Cursor cursorPicksBans = database.query(MySQLiteHelper.TABLE_PICKS_BANS, picksBansColumns, "match_id = ?", new String[]{detailMatch.getMatch_id()}, null, null, null, null);
            ArrayList<PicksBans> picksBansList = new ArrayList<PicksBans>();
            PicksBansDataSource pbds = new PicksBansDataSource(context);
            cursorPicksBans.moveToFirst();
            while (!cursorPicksBans.isAfterLast()) {
                PicksBans picksBans = pbds.cursorToPicksBans(cursorPicksBans);
                picksBansList.add(picksBans);
                cursorPicksBans.moveToNext();
            }
            cursorPicksBans.close();
            detailMatch.setPicks_bans(picksBansList);


            matches.add(detailMatch);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        close();
        return matches;
    }


//    public boolean recordExists(DetailMatch match) {
//        String matchID = match.getMatch_id();
//        Cursor cursor = database.rawQuery("select 1 from matches where match_id = ?", new String[]{matchID});
//        boolean exists = (cursor.getCount() > 0);
//        cursor.close();
//        return exists;
//    }

    public DetailMatch getMatchByID(String matchID) {
        DetailMatch dmb = new DetailMatch();
        Cursor cs = database.query(MySQLiteHelper.TABLE_MATCHES, matchesColumns, "match_id = " + matchID, null, null, null, null);
        cs.moveToFirst();
        while (!cs.isAfterLast()) {
            dmb = cursorToDetailMatch(cs);
            cs.moveToNext();
        }
        cs.close();
        return dmb;
    }

    public DetailMatch getLatestMatch() {
        open();
        DetailMatch dmb = new DetailMatch();
        Cursor cursor = database.rawQuery("SELECT * FROM matches WHERE user = ? ORDER BY match_id DESC LIMIT 1;", new String[]{user_accountID});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            dmb = cursorToDetailMatch(cursor);
            cursor.moveToNext();
        }
        cursor.close();
        System.out.println("returing latest match, id is " + dmb.getMatch_id());
        close();
        return dmb;

    }

    private DetailMatch cursorToDetailMatch(Cursor cursor) {
        DetailMatch detailMatch = new DetailMatch();

        detailMatch.setRadiant_win(Boolean.parseBoolean(cursor.getString(0)));
        detailMatch.setDuration(cursor.getString(1));
        detailMatch.setStart_time(cursor.getString(2));
        detailMatch.setMatch_id(Integer.toString(cursor.getInt(3)));
        detailMatch.setMatch_seq_num(cursor.getString(4));
        detailMatch.setTower_status_radiant(cursor.getString(5));
        detailMatch.setTower_status_dire(cursor.getString(6));
        detailMatch.setBarracks_status_radiant(cursor.getString(7));
        detailMatch.setBarracks_status_dire(cursor.getString(8));
        detailMatch.setCluster(cursor.getString(9));
        detailMatch.setFirst_blood_time(cursor.getString(10));
        detailMatch.setLobby_type(cursor.getString(11));
        detailMatch.setHuman_players(cursor.getString(12));
        detailMatch.setLeagueid(cursor.getString(13));
        detailMatch.setPositive_votes(cursor.getString(14));
        detailMatch.setNegative_votes(cursor.getString(15));
        detailMatch.setGame_mode(cursor.getString(16));
        detailMatch.setUser_win(Boolean.parseBoolean(cursor.getString(17)));
        detailMatch.setFavourite(Boolean.parseBoolean(cursor.getString(18)));
        detailMatch.setNote(cursor.getString(19));
        detailMatch.setUser(cursor.getString(20));

        return detailMatch;
    }


    //test for players in matches all
    private DetailPlayer cursorToDetailHeroBag(Cursor cursor) {
        DetailPlayer player = new DetailPlayer();

        //pim - column 0
        player.setAccount_id(cursor.getString(1));
        player.setMatchID(cursor.getString(2));
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
        open();
        database.delete(MySQLiteHelper.TABLE_MATCHES, "match_id = ?", new String[]{matchID});
        database.delete(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES, "match_id = ?", new String[]{matchID});
        database.delete(MySQLiteHelper.TABLE_PICKS_BANS, "match_id = ?", new String[]{matchID});
        close();
    }

    public void deleteUserMatches() {
        open();
        database.delete(MySQLiteHelper.TABLE_MATCHES, "user = ?", new String[]{user_accountID});
        close();
    }
}
