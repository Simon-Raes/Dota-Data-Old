package be.simonraes.dotadata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import be.simonraes.dotadata.detailmatch.*;
import be.simonraes.dotadata.statistics.HeroStats;

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
//            MySQLiteHelper.TABLE_MATCHES_COLUMN_KEY,
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
//            MySQLiteHelper.TABLE_MATCHES_COLUMN_USER_WIN,
//            MySQLiteHelper.TABLE_MATCHES_COLUMN_FAVOURITE,
//            MySQLiteHelper.TABLE_MATCHES_COLUMN_NOTE,
//            MySQLiteHelper.TABLE_MATCHES_COLUMN_USER
    };


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

    private String[] matchesExtrasColumns = {
            //MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_KEY,
            MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_MATCH_ID,
            //MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_ACCOUNT_ID,
            //MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_USER_WIN,
            MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_NOTE,
            MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_FAVOURITE
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
//        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_KEY, match.getKey());
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
//        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_USER_WIN, String.valueOf(match.isUser_win()));
//        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_FAVOURITE, String.valueOf(match.isFavourite()));
//        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_NOTE, match.getNote());
//        values.put(MySQLiteHelper.TABLE_MATCHES_COLUMN_USER, match.getUser());

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


//    public ArrayList<DetailMatch> getAllMatches() {
//
//        ArrayList<DetailMatch> matches = new ArrayList<DetailMatch>();
//        PlayersInMatchesDataSource pimds = new PlayersInMatchesDataSource(context);
//        open();
//        Cursor cursor = database.query(MySQLiteHelper.TABLE_MATCHES, matchesColumns, "user = ?", new String[]{user_accountID}, null, null, "match_id DESC", null);
//
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            DetailMatch detailMatch = cursorToDetailMatch(cursor);
//
//
//            //test get players for all matches
//            Cursor cursorPlayers = database.query(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES, playersColumns, "match_id = ?", new String[]{detailMatch.getMatch_id()}, null, null, null, null);
//            ArrayList<DetailPlayer> players = new ArrayList<DetailPlayer>();
//            cursorPlayers.moveToFirst();
//
//            while (!cursorPlayers.isAfterLast()) {
//                DetailPlayer detailPlayer = cursorToDetailHeroBag(cursorPlayers);
//                players.add(detailPlayer);
//                cursorPlayers.moveToNext();
//            }
//            cursorPlayers.close();
//            detailMatch.setPlayers(players);
//
//            matches.add(detailMatch);
//            cursor.moveToNext();
//        }
//        // Make sure to close the cursor
//        cursor.close();
//        close();
//        return matches;
//    }


//    public ArrayList<DetailMatch> get50MatchesStartingAtMatchID(String matchID) {
//
//        ArrayList<DetailMatch> matches = new ArrayList<DetailMatch>();
//        open();
//        Cursor cursor;
//        if (matchID != null && !matchID.equals("")) {
//            cursor = database.query(MySQLiteHelper.TABLE_MATCHES, matchesColumns, "match_id < ? AND user = ?", new String[]{matchID, user_accountID}, null, null, "match_id DESC", "50");
//        } else {
//            cursor = database.query(MySQLiteHelper.TABLE_MATCHES, matchesColumns, "user = ?", new String[]{user_accountID}, null, null, "match_id DESC", "50");
//        }
//
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            DetailMatch detailMatch = cursorToDetailMatch(cursor);
//
//            //get players for all matches
//            Cursor cursorPlayers = database.query(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES, playersColumns, "match_id = ?", new String[]{detailMatch.getMatch_id()}, null, null, null, null);
//            ArrayList<DetailPlayer> players = new ArrayList<DetailPlayer>();
//            cursorPlayers.moveToFirst();
//            while (!cursorPlayers.isAfterLast()) {
//                DetailPlayer detailPlayer = cursorToDetailHeroBag(cursorPlayers);
//                players.add(detailPlayer);
//                cursorPlayers.moveToNext();
//            }
//            cursorPlayers.close();
//            detailMatch.setPlayers(players);
//
//
//            //get picks/bans for all matches
//            Cursor cursorPicksBans = database.query(MySQLiteHelper.TABLE_PICKS_BANS, picksBansColumns, "match_id = ?", new String[]{detailMatch.getMatch_id()}, null, null, null, null);
//            ArrayList<PicksBans> picksBansList = new ArrayList<PicksBans>();
//            PicksBansDataSource pbds = new PicksBansDataSource(context);
//            cursorPicksBans.moveToFirst();
//            while (!cursorPicksBans.isAfterLast()) {
//                PicksBans picksBans = pbds.cursorToPicksBans(cursorPicksBans);
//                picksBansList.add(picksBans);
//                cursorPicksBans.moveToNext();
//            }
//            cursorPicksBans.close();
//            detailMatch.setPicks_bans(picksBansList);
//
//
//            matches.add(detailMatch);
//            cursor.moveToNext();
//        }
//        // Make sure to close the cursor
//        cursor.close();
//        close();
//        return matches;
//    }

    public ArrayList<DetailMatchLite> get50LiteMatchesStartingFromID(String matchID) {
        ArrayList<DetailMatchLite> matches = new ArrayList<DetailMatchLite>();
        open();
        Cursor cursor;
        if (matchID != null && !matchID.equals("")) {
            cursor = database.rawQuery("SELECT " +
                    "radiant_win," +
                    "duration," +
                    "start_time, " +
                    "matches.match_id," +
                    "lobby_type," +
                    "game_mode," +
                    //"matches_extras.user_win," +
                    "matches_extras.favourite," +
                    "matches_extras.note," +
                    "players_in_matches.account_id," +
                    "player_slot, " +
                    "hero_id," +
                    "item_0," +
                    "item_1," +
                    "item_2," +
                    "item_3," +
                    "item_4," +
                    "item_5," +
                    "kills," +
                    "deaths," +
                    "assists," +
                    "leaver_status," +
                    "gold," +
                    "last_hits," +
                    "denies," +
                    "gold_per_min," +
                    "xp_per_min," +
                    "gold_spent," +
                    "hero_damage," +
                    "tower_damage," +
                    "hero_healing," +
                    "level " +
                    "FROM players_in_matches " +
                    "JOIN matches " +
                    "ON matches.match_id = players_in_matches.match_id " +
                    "LEFT JOIN matches_extras " +
                    "ON matches_extras.match_id = matches.match_id  " +
                    "WHERE players_in_matches.account_id = ? " +
                    //"AND matches_extras.account_id = ?" +
                    "AND players_in_matches.match_id < ? " +
                    "ORDER BY players_in_matches.match_id DESC " +
                    "LIMIT 50;", new String[]{user_accountID, matchID}); //, user_accountID
        } else {
            cursor = database.rawQuery("SELECT " +
                    "radiant_win," +
                    "duration," +
                    "start_time, " +
                    "matches.match_id," +
                    "lobby_type," +
                    "game_mode," +
                    // "matches_extras.user_win," +
                    "matches_extras.favourite," +
                    "matches_extras.note," +
                    "players_in_matches.account_id," +
                    "player_slot, " +
                    "hero_id," +
                    "item_0," +
                    "item_1," +
                    "item_2," +
                    "item_3," +
                    "item_4," +
                    "item_5," +
                    "kills," +
                    "deaths," +
                    "assists," +
                    "leaver_status," +
                    "gold," +
                    "last_hits," +
                    "denies," +
                    "gold_per_min," +
                    "xp_per_min," +
                    "gold_spent," +
                    "hero_damage," +
                    "tower_damage," +
                    "hero_healing," +
                    "level " +
                    "FROM players_in_matches " +
                    "JOIN matches " +
                    "ON matches.match_id = players_in_matches.match_id " +
                    "LEFT JOIN matches_extras " +
                    "ON matches_extras.match_id = matches.match_id  " +
                    "WHERE players_in_matches.account_id = ? " +
                    //"AND matches_extras.account_id = ?" +
                    "ORDER BY players_in_matches.match_id DESC " +
                    "LIMIT 50;", new String[]{user_accountID}); //, new String[]{user_accountID, user_accountID}
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DetailMatchLite matchLite = cursorToDetailMatchLite(cursor);
            matches.add(matchLite);
            cursor.moveToNext();
        }

        // Make sure to close the cursor
        cursor.close();
        close();
        return matches;
    }


    public DetailMatch getMatchByID(String matchID) {
        DetailMatch dmb = new DetailMatch();
        open();
        Cursor cs = database.query(MySQLiteHelper.TABLE_MATCHES, matchesColumns, "match_id = " + matchID, null, null, null, null); // + " AND user = " + user_accountID
        cs.moveToFirst();
        dmb = cursorToDetailMatch(cs);

        //get players for the match
        Cursor cursorPlayers = database.query(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES, playersColumns, "match_id = ?", new String[]{dmb.getMatch_id()}, null, null, null, null);
        ArrayList<DetailPlayer> players = new ArrayList<DetailPlayer>();
        cursorPlayers.moveToFirst();
        while (!cursorPlayers.isAfterLast()) {
            DetailPlayer detailPlayer = cursorToDetailHeroBag(cursorPlayers);

            //todo: re-enable this when the ability_upgrades visuals have been implemented
            //disabled for now to speed up loading

//            //get the player's upgrades
//            AbilityUpgradesDataSource auds = new AbilityUpgradesDataSource(context);
//            detailPlayer.setAbilityupgrades(auds.getAbilityUpgradesForPlayerInMatch(matchID, detailPlayer.getPlayer_slot()));

            //store the player
            players.add(detailPlayer);
            cursorPlayers.moveToNext();
        }
        cursorPlayers.close();
        dmb.setPlayers(players);


        //get picks/bans for the match
        Cursor cursorPicksBans = database.query(MySQLiteHelper.TABLE_PICKS_BANS, picksBansColumns, "match_id = ?", new String[]{dmb.getMatch_id()}, null, null, null, null);
        ArrayList<PicksBans> picksBansList = new ArrayList<PicksBans>();
        PicksBansDataSource pbds = new PicksBansDataSource(context);
        cursorPicksBans.moveToFirst();
        while (!cursorPicksBans.isAfterLast()) {
            PicksBans picksBans = pbds.cursorToPicksBans(cursorPicksBans);
            picksBansList.add(picksBans);
            cursorPicksBans.moveToNext();
        }
        cursorPicksBans.close();
        dmb.setPicks_bans(picksBansList);

        //get extras for match
        MatchesExtrasDataSource meds = new MatchesExtrasDataSource(context);
        dmb.setExtras(meds.getDetailMatchExtrasForMatch(dmb.getMatch_id()));
        System.out.println("loaded extras into match " + dmb.getExtras().getMatch_id());


        Cursor cursor = database.query(MySQLiteHelper.TABLE_MATCHES_EXTRAS, matchesExtrasColumns, "match_id = ?", new String[]{dmb.getMatch_id()}, null, null, null, null);

        DetailMatchExtras extras = cursorToDetailMatchExtras(cursor);
        cursor.close();
        dmb.setExtras(extras);

        cs.close();
        close();
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
        close();
        return dmb;
    }

    /*Only gets matches used in statistics!*/
    public ArrayList<DetailMatchLite> getAllRealDetailMatchesLite() {
        open();
        ArrayList<DetailMatchLite> records = new ArrayList<DetailMatchLite>();
        Cursor cursor = database.rawQuery("SELECT " +
                "radiant_win," +
                "duration," +
                "start_time, " +
                "matches.match_id," +
                "lobby_type," +
                "game_mode," +
                // "matches_extras.user_win," +
                "matches_extras.favourite," +
                "matches_extras.note," +
                "players_in_matches.account_id," +
                "player_slot, " +
                "hero_id," +
                "item_0," +
                "item_1," +
                "item_2," +
                "item_3," +
                "item_4," +
                "item_5," +
                "kills," +
                "deaths," +
                "assists," +
                "leaver_status," +
                "gold," +
                "last_hits," +
                "denies," +
                "gold_per_min," +
                "xp_per_min," +
                "gold_spent," +
                "hero_damage," +
                "tower_damage," +
                "hero_healing," +
                "level " +
                "FROM players_in_matches " +
                "JOIN matches " +
                "ON matches.match_id = players_in_matches.match_id " +
                "LEFT JOIN matches_extras " +
                "ON matches_extras.match_id = matches.match_id  " +
                "WHERE players_in_matches.account_id = ? " +
                //"AND matches_extras.account_id = ?" +
                "AND game_mode != 0 " +
                "AND game_mode != 6 " +
                "AND game_mode != 7 " +
                "AND game_mode != 9 " +
                "AND game_mode != 10 " +
                "AND game_mode != 15 "
                //+
                //"AND user = ?;"
                , new String[]{user_accountID}); //user_accountID
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            records.add(cursorToDetailMatchLite(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        close();

        return records;
    }

    /*Gets ALL matches, also those for greeviling, diretide,...!*/
    public ArrayList<DetailMatchLite> getAllDetailMatchesLite() {
        open();
        ArrayList<DetailMatchLite> records = new ArrayList<DetailMatchLite>();
        Cursor cursor = database.rawQuery("SELECT " +
                "radiant_win," +
                "duration," +
                "start_time, " +
                "matches.match_id," +
                "lobby_type," +
                "game_mode," +
                //  "matches_extras.user_win," +
                "matches_extras.favourite," +
                "matches_extras.note," +
                "players_in_matches.account_id," +
                "player_slot, " +
                "hero_id," +
                "item_0," +
                "item_1," +
                "item_2," +
                "item_3," +
                "item_4," +
                "item_5," +
                "kills," +
                "deaths," +
                "assists," +
                "leaver_status," +
                "gold," +
                "last_hits," +
                "denies," +
                "gold_per_min," +
                "xp_per_min," +
                "gold_spent," +
                "hero_damage," +
                "tower_damage," +
                "hero_healing," +
                "level " +
                "FROM players_in_matches " +
                "JOIN matches " +
                "ON matches.match_id = players_in_matches.match_id " +
                "LEFT JOIN matches_extras " +
                "ON matches_extras.match_id = matches.match_id  " +
                "WHERE players_in_matches.account_id = ? "
                //"AND matches_extras.account_id = ?"
                , new String[]{user_accountID});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            records.add(cursorToDetailMatchLite(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        close();

        return records;
    }


    public ArrayList<DetailMatchLite> getAllRealDetailMatchesLiteForHero(String heroID) {
        open();
        ArrayList<DetailMatchLite> records = new ArrayList<DetailMatchLite>();
        Cursor cursor = database.rawQuery("SELECT " +
                "radiant_win," +
                "duration," +
                "start_time, " +
                "matches.match_id," +
                "lobby_type," +
                "game_mode," +
                //  "matches_extras.user_win," +
                "matches_extras.favourite," +
                "matches_extras.note," +
                "players_in_matches.account_id," +
                "player_slot, " +
                "hero_id," +
                "item_0," +
                "item_1," +
                "item_2," +
                "item_3," +
                "item_4," +
                "item_5," +
                "kills," +
                "deaths," +
                "assists," +
                "leaver_status," +
                "gold," +
                "last_hits," +
                "denies," +
                "gold_per_min," +
                "xp_per_min," +
                "gold_spent," +
                "hero_damage," +
                "tower_damage," +
                "hero_healing," +
                "level " +
                "FROM players_in_matches " +
                "JOIN matches " +
                "ON matches.match_id = players_in_matches.match_id " +
                "LEFT JOIN matches_extras " +
                "ON matches_extras.match_id = matches.match_id  " +
                "WHERE players_in_matches.account_id = ? " +
                //"AND matches_extras.account_id = ?" +
                "AND game_mode != 0 " +
                "AND game_mode != 6 " +
                "AND game_mode != 7 " +
                "AND game_mode != 9 " +
                "AND game_mode != 10 " +
                "AND game_mode != 15 " +
                // "AND user = ? " +
                "AND hero_id = ?;", new String[]{user_accountID, heroID}); // user_accountID,
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            records.add(cursorToDetailMatchLite(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        close();

        return records;
    }

    public ArrayList<DetailMatchLite> getAllRealDetailMatchesLiteForGameMode(String gameModeID) {
        open();
        ArrayList<DetailMatchLite> records = new ArrayList<DetailMatchLite>();
        Cursor cursor = database.rawQuery("SELECT " +
                "radiant_win," +
                "duration," +
                "start_time, " +
                "matches.match_id," +
                "lobby_type," +
                "game_mode," +
                //  "matches_extras.user_win," +
                "matches_extras.favourite," +
                "matches_extras.note," +
                "players_in_matches.account_id," +
                "player_slot, " +
                "hero_id," +
                "item_0," +
                "item_1," +
                "item_2," +
                "item_3," +
                "item_4," +
                "item_5," +
                "kills," +
                "deaths," +
                "assists," +
                "leaver_status," +
                "gold," +
                "last_hits," +
                "denies," +
                "gold_per_min," +
                "xp_per_min," +
                "gold_spent," +
                "hero_damage," +
                "tower_damage," +
                "hero_healing," +
                "level " +
                "FROM players_in_matches " +
                "JOIN matches " +
                "ON matches.match_id = players_in_matches.match_id " +
                "LEFT JOIN matches_extras " +
                "ON matches_extras.match_id = matches.match_id  " +
                "WHERE players_in_matches.account_id = ? " +
                //"AND matches_extras.account_id = ?" +
                // "AND user = ? " +
                "AND game_mode = ?;", new String[]{user_accountID, gameModeID}); //user_accountID,
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            records.add(cursorToDetailMatchLite(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        close();

        return records;
    }

    public ArrayList<DetailMatchLite> getAllRealDetailMatchesLiteForHeroAndGameMode(String heroID, String gameModeID) {
        open();
        ArrayList<DetailMatchLite> records = new ArrayList<DetailMatchLite>();
        Cursor cursor = database.rawQuery("SELECT " +
                "radiant_win," +
                "duration," +
                "start_time, " +
                "matches.match_id," +
                "lobby_type," +
                "game_mode," +
                //  "matches_extras.user_win," +
                "matches_extras.favourite," +
                "matches_extras.note," +
                "players_in_matches.account_id," +
                "players_in_matches.account_id," +
                "player_slot, " +
                "hero_id," +
                "item_0," +
                "item_1," +
                "item_2," +
                "item_3," +
                "item_4," +
                "item_5," +
                "kills," +
                "deaths," +
                "assists," +
                "leaver_status," +
                "gold," +
                "last_hits," +
                "denies," +
                "gold_per_min," +
                "xp_per_min," +
                "gold_spent," +
                "hero_damage," +
                "tower_damage," +
                "hero_healing," +
                "level " +
                "FROM players_in_matches " +
                "JOIN matches " +
                "ON matches.match_id = players_in_matches.match_id " +
                "LEFT JOIN matches_extras " +
                "ON matches_extras.match_id = matches.match_id  " +
                "WHERE players_in_matches.account_id = ? " +
                //"AND matches_extras.account_id = ?" +
                // "AND user = ? " +
                "AND hero_id = ? " +
                "AND game_mode = ?;", new String[]{user_accountID, heroID, gameModeID}); //user_accountID,
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            records.add(cursorToDetailMatchLite(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        close();

        return records;
    }

    private DetailMatch cursorToDetailMatch(Cursor cursor) {
        DetailMatch detailMatch = new DetailMatch();
        if (cursor != null) {

//        detailMatch.setKey(cursor.getString(0));
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
//        detailMatch.setUser_win(Boolean.parseBoolean(cursor.getString(18)));
//        detailMatch.setFavourite(Boolean.parseBoolean(cursor.getString(19)));
//        detailMatch.setNote(cursor.getString(20));
//        detailMatch.setUser(cursor.getString(21));
        }
        return detailMatch;
    }

//    private HeroStats cursorToHeroStats(Cursor cursor) {
//        HeroStats heroStats = new HeroStats();
//        if( cursor != null && cursor.moveToFirst() ) {
//
//            heroStats.setNumberOfGames(cursor.getInt(0));
//            heroStats.setWins(cursor.getInt(1));
//            heroStats.setLosses(cursor.getInt(2));
//            heroStats.setLongestMatch(cursor.getInt(3));
//        }
//        return heroStats;
//    }


    //test for players in matches all
    private DetailPlayer cursorToDetailHeroBag(Cursor cursor) {
        DetailPlayer player = new DetailPlayer();
        if (cursor != null) {

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
        }
        return player;
    }

    private DetailMatchLite cursorToDetailMatchLite(Cursor cursor) {
        DetailMatchLite record = new DetailMatchLite();

        //START TIME OOK NODIG VOOR HISTORY GRAPH DINGEN
        if (cursor != null) {


            record.setRadiant_win(Boolean.parseBoolean(cursor.getString(0)));
            record.setDuration(cursor.getString(1));
            record.setStart_time(cursor.getString(2));
            record.setMatch_id(cursor.getString(3));
            record.setLobby_type(cursor.getString(4));
            record.setGame_mode(cursor.getString(5));
            //record.setUser_win(Boolean.parseBoolean(cursor.getString(6)));
            record.setFavourite(Boolean.parseBoolean(cursor.getString(6)));
            record.setNote(cursor.getString(7));
            record.setAccount_id(cursor.getString(8));
            record.setPlayer_slot(cursor.getString(9));
            record.setHero_id(cursor.getString(10));
            record.setItem_0(cursor.getString(11));
            record.setItem_1(cursor.getString(12));
            record.setItem_2(cursor.getString(13));
            record.setItem_3(cursor.getString(14));
            record.setItem_4(cursor.getString(15));
            record.setItem_5(cursor.getString(16));
            record.setKills(cursor.getString(17));
            record.setDeaths(cursor.getString(18));
            record.setAssists(cursor.getString(19));
            record.setLeaver_status(cursor.getString(20));
            record.setGold(cursor.getString(21));
            record.setLast_hits(cursor.getString(22));
            record.setDenies(cursor.getString(23));
            record.setGold_per_min(cursor.getString(24));
            record.setXp_per_min(cursor.getString(25));
            record.setGold_spent(cursor.getString(26));
            record.setHero_damage(cursor.getString(27));
            record.setTower_damage(cursor.getString(28));
            record.setHero_healing(cursor.getString(29));
            record.setLevel(cursor.getString(30));

        }
        /*record.setFavourite(Boolean.parseBoolean(cursor.getString(7)));
        record.setNote(cursor.getString(8));
        record.setAccount_id(cursor.getString(9));
        record.setPlayer_slot(cursor.getString(10));
        record.setHero_id(cursor.getString(11));
        record.setItem_0(cursor.getString(12));
        record.setItem_1(cursor.getString(13));
        record.setItem_2(cursor.getString(14));
        record.setItem_3(cursor.getString(15));
        record.setItem_4(cursor.getString(16));
        record.setItem_5(cursor.getString(17));
        record.setKills(cursor.getString(18));
        record.setDeaths(cursor.getString(19));
        record.setAssists(cursor.getString(20));
        record.setLeaver_status(cursor.getString(21));
        record.setGold(cursor.getString(22));
        record.setLast_hits(cursor.getString(23));
        record.setDenies(cursor.getString(24));
        record.setGold_per_min(cursor.getString(25));
        record.setXp_per_min(cursor.getString(26));
        record.setGold_spent(cursor.getString(27));
        record.setHero_damage(cursor.getString(28));
        record.setTower_damage(cursor.getString(29));
        record.setHero_healing(cursor.getString(30));
        record.setLevel(cursor.getString(31));*/

        return record;
    }

    private DetailMatchExtras cursorToDetailMatchExtras(Cursor cursor) {
        DetailMatchExtras extras = new DetailMatchExtras();

        if (cursor != null && cursor.moveToFirst()) {
            System.out.println("extra be cool");
            extras.setMatch_id(cursor.getString(0));
            //extras.setAccount_id(cursor.getString(2));
            // extras.setUser_win(Boolean.parseBoolean(cursor.getString(3)));
            extras.setNote(cursor.getString(1));
            extras.setFavourite(Boolean.parseBoolean(cursor.getString(2)));
        }

        return extras;
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
//
//    public void deleteLatestMatch() {
//        String matchID = getLatestMatch().getMatch_id();
//        open();
//        database.delete(MySQLiteHelper.TABLE_MATCHES, "match_id = ?", new String[]{matchID});
//        database.delete(MySQLiteHelper.TABLE_PLAYERS_IN_MATCHES, "match_id = ?", new String[]{matchID});
//        database.delete(MySQLiteHelper.TABLE_PICKS_BANS, "match_id = ?", new String[]{matchID});
//        close();
//    }

    public void deleteUserMatches() {
        open();

        //database.delete(MySQLiteHelper.TABLE_MATCHES, "user = ?", new String[]{user_accountID});

        //delete nothing, really
        // database.delete(MySQLiteHelper.TABLE_MATCHES_EXTRAS, "account_id = ?", new String[]{user_accountID});
        close();
    }
}
