package be.simonraes.dotadata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import be.simonraes.dotadata.detailmatch.DetailMatchExtras;

import java.util.ArrayList;

/**
 * database class for personal data that can be added to a match
 * Created by Simon Raes on 30/03/2014.
 */
public class MatchesExtrasDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private String[] matchesExtrasColumns = {
            MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_MATCH_ID,
            MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_NOTE,
            MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_FAVOURITE
    };

    public MatchesExtrasDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Inserts or updates a single extras record
     */
    public void updateMatchesExtras(DetailMatchExtras extras) {
        ContentValues values = new ContentValues();

        open();
        values.put(MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_MATCH_ID, extras.getMatch_id());
        values.put(MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_NOTE, extras.getNote());
        values.put(MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_FAVOURITE, String.valueOf(extras.isFavourite()));

        database.insertWithOnConflict(MySQLiteHelper.TABLE_MATCHES_EXTRAS, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        System.out.println("saved extra with values " + extras.getMatch_id() + " " + extras.getNote() + " " + extras.isFavourite());

        close();
    }

    /**
     * Inserts list of extras objects
     */
    public void saveMatchesExtrasList(ArrayList<DetailMatchExtras> extrasList) {
        open();

        database.beginTransaction();
        try {
            for (DetailMatchExtras extras : extrasList) {
                saveMatchesExtras(extras);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        close();
    }

    /**
     * Bulk inserts records
     */
    public void saveMatchesExtras(DetailMatchExtras extras) {

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_MATCH_ID, extras.getMatch_id());
        values.put(MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_NOTE, extras.getNote());
        values.put(MySQLiteHelper.TABLE_MATCHES_EXTRAS_COLUMN_FAVOURITE, String.valueOf(extras.isFavourite()));


        database.insertWithOnConflict(MySQLiteHelper.TABLE_MATCHES_EXTRAS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public DetailMatchExtras getDetailMatchExtrasForMatch(String match_id) {
        open();
        DetailMatchExtras extras = null;
        Cursor cursor = database.query(MySQLiteHelper.TABLE_MATCHES_EXTRAS, matchesExtrasColumns, "match_id = ?", new String[]{match_id}, null, null, null, null);

        extras = cursorToDetailMatchExtras(cursor);
        cursor.close();
        close();
        return extras;

    }

    private DetailMatchExtras cursorToDetailMatchExtras(Cursor cursor) {
        DetailMatchExtras extras = new DetailMatchExtras();

        if (cursor != null && cursor.moveToFirst()) {
            extras.setMatch_id(cursor.getString(0));
            extras.setNote(cursor.getString(1));
            extras.setFavourite(Boolean.parseBoolean(cursor.getString(2)));
        }

        return extras;
    }
}
