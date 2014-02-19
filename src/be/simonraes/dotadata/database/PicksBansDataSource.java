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
 * Created by Simon on 19/02/14.
 */
public class PicksBansDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private Context context;

    private String[] picksBansColumns = {
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_MATCH_ID,
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_IS_PICK,
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_HERO_ID,
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_TEAM,
            MySQLiteHelper.TABLE_PICKS_BANS_COLUMN_ORDER
    };

    public PicksBansDataSource(Context context) {
        this.context = context;
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

    public ArrayList<PicksBans> getAllPicksBansForMatch(String matchID) {
        open();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PICKS_BANS, picksBansColumns, "match_id = ?", new String[]{matchID}, null, null, null, null);
        ArrayList<PicksBans> picksBansList = new ArrayList();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            PicksBans picksBans = cursorToPicksBans(cursor);
            picksBansList.add(picksBans);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return picksBansList;
    }

    public PicksBans cursorToPicksBans(Cursor cursor) {
        PicksBans picksBans = new PicksBans();

        picksBans.setMatch_id(cursor.getString(0));
        picksBans.setIs_pick(Boolean.parseBoolean(cursor.getString(1)));
        picksBans.setHero_id(cursor.getString(2));
        picksBans.setTeam(cursor.getString(3));
        picksBans.setOrder(cursor.getString(4));


        return picksBans;
    }
}
