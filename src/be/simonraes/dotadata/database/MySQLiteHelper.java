package be.simonraes.dotadata.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Simon on 15/02/14.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "be.simonraes.dotadata.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MATCHES = "matches";
    public static final String TABLE_MATCHES_COLUMN_RADIANT_WIN = "radiant_win";
    public static final String TABLE_MATCHES_COLUMN_DURATION = "duration";
    public static final String TABLE_MATCHES_COLUMN_START_TIME = "start_time";
    public static final String TABLE_MATCHES_COLUMN_MATCH_ID = "match_id";
    public static final String TABLE_MATCHES_COLUMN_MATCH_SEQ_NUM = "match_seq_num";
    public static final String TABLE_MATCHES_COLUMN_TOWER_STATUS_RADIANT = "tower_status_radiant";
    public static final String TABLE_MATCHES_COLUMN_TOWER_STATUS_DIRE = "tower_status_dire";
    public static final String TABLE_MATCHES_COLUMN_BARRACKS_STATUS_RADIANT = "barracks_status_radiant";
    public static final String TABLE_MATCHES_COLUMN_BARRACKS_STATUS_DIRE = "barracks_status_dire";
    public static final String TABLE_MATCHES_COLUMN_CLUSTER = "cluster";
    public static final String TABLE_MATCHES_COLUMN_FIRST_BLOOD_TIME = "first_blood_time";
    public static final String TABLE_MATCHES_COLUMN_LOBBY_TYPE = "lobby_type";
    public static final String TABLE_MATCHES_COLUMN_HUMAN_PLAYERS = "human_players";
    public static final String TABLE_MATCHES_COLUMN_LEAGUEID = "leagueid";
    public static final String TABLE_MATCHES_COLUMN_POSITIVE_VOTES = "positive_votes";
    public static final String TABLE_MATCHES_COLUMN_NEGATIVE_VOTES = "negative_votes";
    public static final String TABLE_MATCHES_COLUMN_GAME_MODE = "game_mode";
    public static final String TABLE_MATCHES_COLUMN_USER_WIN = "user_win";
    public static final String TABLE_MATCHES_COLUMN_FAVOURITE = "favourite";
    public static final String TABLE_MATCHES_COLUMN_NOTE = "note";

    public static final String TABLE_PLAYERS_IN_MATCHES = "players_in_matches";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_PIM_ID = "pim_id"; //extra field for database key and relation
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_MATCHID = "match_id"; //extra field for database key and relation
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_ACCOUNTID = "account_id";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_PLAYER_SLOT = "player_slot";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_HERO_ID = "hero_id";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM0 = "item_0";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM1 = "item_1";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM2 = "item_2";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM3 = "item_3";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM4 = "item_4";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_ITEM5 = "item_5";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_KILLS = "kills";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_DEATHS = "deaths";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_ASSISTS = "assists";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_LEAVER_STATUS = "leaver_status";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_GOLD = "gold";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_LAST_HITS = "last_hits";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_DENIES = "denies";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_GOLD_PER_MIN = "gold_per_min";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_XP_PER_MIN = "xp_per_min";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_GOLD_SPENT = "gold_spent";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_HERO_DAMAGE = "hero_damage";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_TOWER_DAMAGE = "tower_damage";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_HERO_HEALING = "hero_healing";
    public static final String TABLE_PLAYERS_IN_MATCHES_COLUMN_LEVEL = "level";

    public static final String TABLE_PICKS_BANS = "picks_bans";
    public static final String TABLE_PICKS_BANS_COLUMN_MATCH_ID = "match_id"; //extra field for database relation
    public static final String TABLE_PICKS_BANS_COLUMN_IS_PICK = "is_pick";
    public static final String TABLE_PICKS_BANS_COLUMN_HERO_ID = "hero_id";
    public static final String TABLE_PICKS_BANS_COLUMN_TEAM = "team";
    public static final String TABLE_PICKS_BANS_COLUMN_ORDER = "orders"; //orders instead of order (is a reserved sqlite word)

//    public static final String TABLE_PLAYERS = "players";
//    public static final String TABLE_PLAYERS_COLUMN_STEAMID32 = "steam_id32";
//    public static final String TABLE_PLAYERS_COLUMN_STEAMID64 = "steam_id64";
//    public static final String TABLE_PLAYERS_COLUMN_PERSONANAME = "personaname";
//    public static final String TABLE_PLAYERS_COLUMN_AVATAR = "avatar";

    private static final String CREATE_TABLE_MATCHES = "create table IF NOT EXISTS matches(radiant_win text, " +
            "duration text, start_time text, match_id integer primary key, match_seq_num text, tower_status_radiant text, tower_status_dire text, barracks_status_radiant text," +
            "barracks_status_dire text, cluster text, first_blood_time text, lobby_type text, human_players text, leagueid text, " +
            "positive_votes text, negative_votes text, game_mode text, user_win text, favourite text, note text);";


    private static final String CREATE_TABLE_PLAYERS_IN_MATCHES = "create table IF NOT EXISTS players_in_matches (pim_id text primary key, account_id text, " +
            "match_id text, player_slot text, hero_id text, item_0 text, item_1 text, item_2 text, item_3 text, item_4 text, item_5 text, kills text, deaths text, assists text, " +
            "leaver_status text, gold text, last_hits text, denies text, gold_per_min text, xp_per_min text, gold_spent text, hero_damage text, tower_damage text, " +
            "hero_healing text, level text);";

    private static final String CREATE_TABLE_PICKS_BANS = "create table IF NOT EXISTS picks_bans(match_id text, is_pick text, hero_id text, team text, orders text);";

//    private static final String CREATE_TABLE_PLAYERS = "create table IF NOT EXISTS players(steam_id32 integer primary key, steam_id64 text, personaname text, avatar text);";

//    private static final String CREATE_TABLE_FRIENDS = "create table IF NOT EXISTS friends(steam_id64 integer primary key, accountid integer);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_MATCHES);
        database.execSQL(CREATE_TABLE_PLAYERS_IN_MATCHES);
        database.execSQL(CREATE_TABLE_PICKS_BANS);
        //database.execSQL(CREATE_TABLE_PLAYERS);
        // database.execSQL(CREATE_TABLE_FRIENDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //todo: code for database upgrade
    }

}
