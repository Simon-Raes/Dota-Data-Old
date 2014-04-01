package be.simonraes.dotadata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import be.simonraes.dotadata.user.User;

import java.util.ArrayList;

/**
 * Created by Simon on 23/02/14.
 * Save and get users of the app
 */

public class UsersDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private Context context;


    private String[] usersColumns = {
            MySQLiteHelper.TABLE_USERS_COLUMN_ACCOUNT_ID,
            MySQLiteHelper.TABLE_USERS_COLUMN_STEAM_ID,
            MySQLiteHelper.TABLE_USERS_COLUMN_NAME,
            MySQLiteHelper.TABLE_USERS_COLUMN_AVATAR,
            MySQLiteHelper.TABLE_USERS_COLUMN_LAST_SAVED_MATCH
    };

    public UsersDataSource(Context context) {
        this.context = context;
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        if (dbHelper != null) {
            database = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        dbHelper.close();
    }

    public void saveUser(User user) {
        System.out.println("saved user " + user.getName() + "with last game " + user.getLast_saved_match());
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.TABLE_USERS_COLUMN_ACCOUNT_ID, user.getAccount_id());
        values.put(MySQLiteHelper.TABLE_USERS_COLUMN_STEAM_ID, user.getSteam_id());
        values.put(MySQLiteHelper.TABLE_USERS_COLUMN_NAME, user.getName());
        values.put(MySQLiteHelper.TABLE_USERS_COLUMN_AVATAR, user.getAvatar());
        values.put(MySQLiteHelper.TABLE_USERS_COLUMN_LAST_SAVED_MATCH, user.getLast_saved_match());

        open();
        database.insertWithOnConflict(MySQLiteHelper.TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        close();
    }

    public ArrayList<User> getAllUsers() {
        open();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS, usersColumns, null, null, null, null, null, null);
        ArrayList<User> users = new ArrayList<User>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            users.add(cursorToUser(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        close();
        return users;
    }

    public User getUserByID(String accountID) {
        open();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS, usersColumns, "account_id = ?", new String[]{accountID}, null, null, null, null);
        User thisUser = new User();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            thisUser = cursorToUser(cursor);
            cursor.moveToNext();
        }

        cursor.close();
        close();
        return thisUser;
    }

    public void deleteUserByID(String accountID) {
        open();
        database.delete(MySQLiteHelper.TABLE_USERS, "account_id = ?", new String[]{accountID});
        close();
    }

    public User cursorToUser(Cursor cursor) {
        User thisUser = new User();
        if (cursor != null) {

            thisUser.setAccount_id(cursor.getString(0));
            thisUser.setSteam_id(cursor.getString(1));
            thisUser.setName(cursor.getString(2));
            thisUser.setAvatar(cursor.getString(3));
            thisUser.setLast_saved_match(cursor.getString(4));
        }
        return thisUser;
    }

}
