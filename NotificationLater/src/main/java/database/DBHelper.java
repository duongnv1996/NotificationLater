package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kimcy929.notificationlater.DebugTag;

/**
 * Created by kimcy on 18/08/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notification_later.db";
    private static int dbVersion = 1;
    private final String TABLE_APP_NAME = "tbl_app_selected";
    private final String TABLE_NOTIFICATION_SAVED = "tbl_notification_saved";

    public static final String _ID= "_id";
    public static final String PACKAGE_NAME = "package_name";
    public static final String APP_NAME = "app_name";
    //Table notification
    public static final String CONTENT_TITLE = "content_title";
    public static final String CONTENT_TEXT = "content_text";
    public static final String TIME_POST = "time_post";
    public static final String IS_READ = "is_read";

    private final String CREATE_TABLE_APP_SELECTED = "CREATE TABLE tbl_app_selected(package_name TEXT PRIMARY KEY)";
    private final String CREATE_TABLE_NOTIFICATION = "CREATE TABLE tbl_notification_saved(_id INTEGER PRIMARY KEY AUTOINCREMENT, package_name TEXT NOT NULL, content_title TEXT, content_text TEXT, time_post TEXT, is_read INTEGER NOT NULL)";

    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_APP_SELECTED);
            db.execSQL(CREATE_TABLE_NOTIFICATION);
        } catch (SQLiteException e) {
            Log.e(DebugTag.TAG, "Error create table");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP IF EXISTS TABLE " + TABLE_APP_NAME);
            db.execSQL("DROP IF EXISTS TABLE " + TABLE_NOTIFICATION_SAVED);
            onCreate(db);
        }
    }

    public void open() {
        db = getWritableDatabase();
    }

    public void close() {
        if (db != null && db.isOpen()) {
            try {
                db.close();
            } catch (SQLiteException e) {
                Log.e(DebugTag.TAG, "Error close database");
            }
        }
    }

    public Cursor getAllAppSelected() {
        return  db.rawQuery("SELECT " + PACKAGE_NAME + " FROM " + TABLE_APP_NAME, null);
    }

    public Cursor getAllNotificationSaved() {
        return db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATION_SAVED + " ORDER BY " + TIME_POST + " DESC", null);
    }

    public boolean insertApp(ContentValues values) {
        return db.insert(TABLE_APP_NAME, null, values) > 0;
    }

    public boolean deleteApp(String packageName) {
        return db.delete(TABLE_APP_NAME, PACKAGE_NAME + "=?", new String[]{packageName}) > 0;
    }

    public boolean deleteAllApp() {
        return db.delete(TABLE_APP_NAME, null, null) > 0;
    }

    public boolean insertNotification(ContentValues values) {
        return db.insert(TABLE_NOTIFICATION_SAVED, null, values) > 0;
    }

    public boolean deleteNotification(int id) {
        return db.delete(TABLE_NOTIFICATION_SAVED, _ID + "=?", new String[]{String.valueOf(id)}) == 1;
    }

    public boolean deleteAllNotification() {
        return db.delete(TABLE_NOTIFICATION_SAVED, null, null) > 0;

    }

    public boolean updateIsRead(ContentValues values, int id) {
        return db.update(TABLE_NOTIFICATION_SAVED, values, _ID + "=?", new String[] {String.valueOf(id)}) > 0;
    }

    public boolean checkPackageName(String packageName) {
        String sql = "SELECT " + PACKAGE_NAME + " FROM " + TABLE_APP_NAME
                + " WHERE " + PACKAGE_NAME + "='" + packageName + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        return false;
    }
}
