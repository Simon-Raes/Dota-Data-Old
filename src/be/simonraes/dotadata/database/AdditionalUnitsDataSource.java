package be.simonraes.dotadata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import be.simonraes.dotadata.detailmatch.AbilityUpgrades;
import be.simonraes.dotadata.detailmatch.AdditionalUnits;

import java.util.ArrayList;

/**
 * Created by Simon Raes on 18/04/2014.
 */
public class AdditionalUnitsDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private String[] additionalUnitsColumns = {
            MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_KEY,
            MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_MATCH_ID,
            MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_PLAYER_SLOT,
            MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_UNIT_NAME,
            MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_ITEM_0,
            MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_ITEM_1,
            MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_ITEM_2,
            MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_ITEM_3,
            MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_ITEM_4,
            MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_ITEM_5
    };

    public AdditionalUnitsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void saveAdditionalUnits(AdditionalUnits additionalUnits) {

        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_KEY, additionalUnits.getMatch_id() + additionalUnits.getPlayer_slot());
        values.put(MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_MATCH_ID, additionalUnits.getMatch_id());
        values.put(MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_PLAYER_SLOT, additionalUnits.getPlayer_slot());
        values.put(MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_UNIT_NAME, additionalUnits.getUnitname());
        values.put(MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_ITEM_0, additionalUnits.getItem_0());
        values.put(MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_ITEM_1, additionalUnits.getItem_1());
        values.put(MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_ITEM_2, additionalUnits.getItem_2());
        values.put(MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_ITEM_3, additionalUnits.getItem_3());
        values.put(MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_ITEM_4, additionalUnits.getItem_4());
        values.put(MySQLiteHelper.TABLE_ADDITIONAL_UNITS_COLUMN_ITEM_5, additionalUnits.getItem_5());

        database.insertWithOnConflict(MySQLiteHelper.TABLE_ADDITIONAL_UNITS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public ArrayList<AdditionalUnits> getAdditionalUnitsForPlayerInMatch(SQLiteDatabase database, String player_slot, String match_id) {
        //just get the database from the matchesdatasource for now, todo: clean up code
        this.database = database;
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ADDITIONAL_UNITS, additionalUnitsColumns, "match_id = ? AND player_slot = ?", new String[]{match_id, player_slot}, null, null, null, null);
        ArrayList<AdditionalUnits> additionalUnitses = new ArrayList<AdditionalUnits>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            additionalUnitses.add(cursorToAdditionalUnits(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return additionalUnitses;
    }

    private AdditionalUnits cursorToAdditionalUnits(Cursor cursor) {
        AdditionalUnits au = new AdditionalUnits();
        if (cursor != null) {

            //skip key (0)
            au.setMatch_id(cursor.getString(1));
            au.setPlayer_slot(cursor.getString(2));
            au.setUnitname(cursor.getString(3));
            au.setItem_0(cursor.getString(4));
            au.setItem_1(cursor.getString(5));
            au.setItem_2(cursor.getString(6));
            au.setItem_3(cursor.getString(7));
            au.setItem_4(cursor.getString(8));
            au.setItem_5(cursor.getString(9));
        }
        return au;
    }
}
