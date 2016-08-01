package ru.bagrusss.selfchat.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bagrusss.
 */

public class HelperDB extends SQLiteOpenHelper {

    private static HelperDB mInstance;
    private static SQLiteDatabase mDB;

    public static final String DB_NAME = "selfchat.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_MESSAGES = "messages";

    public static final String ID = "_id";
    public static final String TYPE = "type";
    public static final String DATA = "dt";
    public static final String TIME = "tm";

    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + '(' +
            ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
            TYPE + "INTEGER," +
            DATA + "TEXT," +
            TIME + "INTEGER)";


    private HelperDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mDB = getWritableDatabase();
    }

    public static HelperDB getInstance(Context cont) {
        HelperDB localInstance = mInstance;
        if (localInstance == null) {
            synchronized (HelperDB.class) {
                localInstance = mInstance;
                if (localInstance == null) {
                    mInstance = localInstance = new HelperDB(cont);
                }
            }
        }
        return localInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void closeDB() {
        if (mDB != null && mDB.isOpen())
            mDB.close();
        mInstance = null;
    }
}
