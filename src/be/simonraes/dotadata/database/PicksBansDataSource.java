package be.simonraes.dotadata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import be.simonraes.dotadata.detailmatch.PicksBans;

import java.util.ArrayList;

/**
 * Created by Simon on 19/02/14.
 */
public class PicksBansDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private String[] picksBansColumns = {
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_KEY,
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_MATCH_ID,
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_IS_PICK,
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_HERO_ID,
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_TEAM,
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_ORDER
    };

    public PicksBansDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
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

    public void savePicksBansList(ArrayList<PicksBans> picksBansList) {
        open();

        database.beginTransaction();
        try {
            for (PicksBans picksBans : picksBansList) {
                savePicksBans(picksBans);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        close();
    }

    public void savePicksBansListNoOpen(ArrayList<PicksBans> picksBansList) {

        try {
            for (PicksBans picksBans : picksBansList) {
                savePicksBans(picksBans);
            }
        } finally {
        }

    }

    public PicksBans cursorToPicksBans(Cursor cursor) {
        PicksBans picksBans = new PicksBans();
        if (cursor != null) {

            picksBans.setMatch_id(cursor.getString(0));
            picksBans.setIs_pick(Boolean.parseBoolean(cursor.getString(1)));
            picksBans.setHero_id(cursor.getString(2));
            picksBans.setTeam(cursor.getString(3));
            picksBans.setOrder(cursor.getString(4));
        }
        return picksBans;
    }
}
