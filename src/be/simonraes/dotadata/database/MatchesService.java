package be.simonraes.dotadata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import be.simonraes.dotadata.detailmatch.*;

import java.util.ArrayList;

/**
 * Created by Simon Raes on 14/04/2014.
 */
public class MatchesService {


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
    };


    //todo: db system needs middle layer/class so this can be removed
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

    private String accountID;

    public MatchesService(Context context, String user_accountID) {
        this.context = context;
        this.accountID = user_accountID;
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private void saveDetailMatch(DetailMatch match) {
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

        database.insertWithOnConflict(MySQLiteHelper.TABLE_MATCHES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
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

    public void savePicksBans(PicksBans picksBans) {

        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_KEY, picksBans.getMatch_id() + picksBans.getOrder()); //unique primary key
        values.put(MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_MATCH_ID, picksBans.getMatch_id());
        values.put(MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_IS_PICK, String.valueOf(picksBans.isIs_pick())); //store boolean as string
        values.put(MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_HERO_ID, picksBans.getHero_id());
        values.put(MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_TEAM, picksBans.getTeam());
        values.put(MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_ORDER, picksBans.getOrder());

        database.insertWithOnConflict(MySQLiteHelper.TABLE_PICKS_BANS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    public void saveAbilityUpgrades(AbilityUpgrades abilityUpgrades) {

        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.TABLE_ABILITY_UPGRADES_COLUMN_KEY, abilityUpgrades.getMatch_id() + abilityUpgrades.getPlayer_slot() + abilityUpgrades.getLevel());
        values.put(MySQLiteHelper.TABLE_ABILITY_UPGRADES_COLUMN_MATCH_ID, abilityUpgrades.getMatch_id());
        values.put(MySQLiteHelper.TABLE_ABILITY_UPGRADES_COLUMN_PLAYER_SLOT, abilityUpgrades.getPlayer_slot());
        values.put(MySQLiteHelper.TABLE_ABILITY_UPGRADES_COLUMN_ABILITY, abilityUpgrades.getAbility());
        values.put(MySQLiteHelper.TABLE_ABILITY_UPGRADES_COLUMN_TIME, abilityUpgrades.getTime());
        values.put(MySQLiteHelper.TABLE_ABILITY_UPGRADES_COLUMN_LEVEL, abilityUpgrades.getLevel());

        database.insertWithOnConflict(MySQLiteHelper.TABLE_ABILITY_UPGRADES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void saveSingleMatch(DetailMatch match) {
        open();
        database.beginTransaction();
        System.out.println("matchesservice: saving match " + match.getMatch_id());

        ArrayList<DetailPlayer> players = new ArrayList<DetailPlayer>();
        ArrayList<PicksBans> picksBansList = new ArrayList<PicksBans>();
        ArrayList<AbilityUpgrades> abilityUpgradesList = new ArrayList<AbilityUpgrades>();

        for (DetailPlayer player : match.getPlayers()) {
            player.setMatchID(match.getMatch_id());
            players.add(player);

            //get abilityupgrades and prep them
            for (AbilityUpgrades au : player.getAbilityupgrades()) {
                au.setMatch_id(match.getMatch_id());
                au.setPlayer_slot(player.getPlayer_slot());
                abilityUpgradesList.add(au);
            }
        }
        //get picksbans
        if (match.getPicks_bans().size() > 0) {
            for (PicksBans picksBans : match.getPicks_bans()) {
                picksBans.setMatch_id(match.getMatch_id());
                picksBansList.add(picksBans);
            }
        }
        saveDetailMatch(match);

        //save players
//        PlayersInMatchesDataSource pimds = new PlayersInMatchesDataSource(context);
//        pimds.savePlayersNoOpen(players);
        for (DetailPlayer player : players) {
            savePlayer(player);
        }

        //save picksbans
//        PicksBansDataSource pbds = new PicksBansDataSource(context);
//        pbds.savePicksBansListNoOpen(picksBansList);
        for (PicksBans picksBans : picksBansList) {
            savePicksBans(picksBans);
        }

        //save ability upgrades
//        AbilityUpgradesDataSource auds = new AbilityUpgradesDataSource(context);
//        auds.saveAbilityUpgradesListNoOpen(abilityUpgradesList);
        for (AbilityUpgrades abilityUpgrades : abilityUpgradesList) {
            saveAbilityUpgrades(abilityUpgrades);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        System.out.println("saved match ");
        close();
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
}
