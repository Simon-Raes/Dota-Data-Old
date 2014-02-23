package be.simonraes.dotadata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
            MySQLiteHelper.TABLE_USERS_ACCOUNT_ID,
            MySQLiteHelper.TABLE_USERS_STEAM_ID,
            MySQLiteHelper.TABLE_USERS_NAME,
            MySQLiteHelper.TABLE_USERS_AVATAR
    };

    public UsersDataSource(Context context) {
        this.context = context;
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void saveUser(User user) {

        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.TABLE_USERS_ACCOUNT_ID, user.getAccount_id());
        values.put(MySQLiteHelper.TABLE_USERS_STEAM_ID, user.getSteam_id());
        values.put(MySQLiteHelper.TABLE_USERS_NAME, user.getName());
        values.put(MySQLiteHelper.TABLE_USERS_AVATAR, user.getAvatar());

        open();

        try {
            System.out.println("soutint:");
            System.out.println(database.insertOrThrow(MySQLiteHelper.TABLE_USERS, null, values));

        } catch (Exception e) {
            System.out.println("errerrrrrrrrrrrr+ " + e.getStackTrace());
        }


        close();
    }

    public ArrayList<User> getAllUsers() {
        open();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS, usersColumns, null, null, null, null, null, null);
        ArrayList<User> users = new ArrayList<User>();

        System.out.println(DatabaseUtils.queryNumEntries(database, "appusers"));

        System.out.println("cursor size = " + cursor.getCount());
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

    public User cursorToUser(Cursor cursor) {
        User thisUser = new User();

        thisUser.setAccount_id(cursor.getString(0));
        thisUser.setSteam_id(cursor.getString(1));
        thisUser.setName(cursor.getString(2));
        thisUser.setAvatar(cursor.getString(3));

        return thisUser;
    }

}
