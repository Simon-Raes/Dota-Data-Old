package be.simonraes.dotadata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import be.simonraes.dotadata.detailmatch.AbilityUpgrades;

import java.util.ArrayList;

/**
 * Created by Simon on 19/02/14.
 */
public class AbilityUpgradesDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private String[] abilityUpgradeColumns = {
            MySQLiteHelper.TABLE_ABILITY_UPGRADES_COLUMN_KEY,
            MySQLiteHelper.TABLE_ABILITY_UPGRADES_COLUMN_MATCH_ID,
            MySQLiteHelper.TABLE_ABILITY_UPGRADES_COLUMN_PLAYER_SLOT,
            MySQLiteHelper.TABLE_ABILITY_UPGRADES_COLUMN_ABILITY,
            MySQLiteHelper.TABLE_ABILITY_UPGRADES_COLUMN_TIME,
            MySQLiteHelper.TABLE_ABILITY_UPGRADES_COLUMN_LEVEL
    };

    public AbilityUpgradesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
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

    /*Bulk insert the abilityupgrades*/
    public void saveAbilityUpgradesList(ArrayList<AbilityUpgrades> abilityUpgradesList) {
        open();

        database.beginTransaction();
        try {
            for (AbilityUpgrades abilityUpgrades : abilityUpgradesList) {
                saveAbilityUpgrades(abilityUpgrades);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        close();
    }

    public void saveAbilityUpgradesListNoOpen(ArrayList<AbilityUpgrades> abilityUpgradesList) {

        try {
            for (AbilityUpgrades abilityUpgrades : abilityUpgradesList) {
                saveAbilityUpgrades(abilityUpgrades);
            }
        } finally {
        }

    }

    public ArrayList<AbilityUpgrades> getAbilityUpgradesForPlayerInMatch(String match_id, String player_slot) {
        open();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ABILITY_UPGRADES, abilityUpgradeColumns, "match_id = ? AND player_slot = ?", new String[]{match_id, player_slot}, null, null, null, null);
        ArrayList<AbilityUpgrades> abilityUpgradeses = new ArrayList();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            abilityUpgradeses.add(cursorToAbilityUpgrades(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return abilityUpgradeses;
    }

    private AbilityUpgrades cursorToAbilityUpgrades(Cursor cursor) {
        AbilityUpgrades au = new AbilityUpgrades();
        if (cursor != null) {

            //skip key (0)
            au.setMatch_id(cursor.getString(1));
            au.setPlayer_slot(cursor.getString(2));
            au.setAbility(cursor.getString(3));
            au.setTime(cursor.getString(4));
            au.setLevel(cursor.getString(5));
        }
        return au;
    }
}
