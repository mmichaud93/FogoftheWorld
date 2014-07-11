package com.tallmatt.fogoftheworld.app.storage;

/**
 * Created by michaudm3 on 7/9/2014.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tallmatt.fogoftheworld.app.ui.utility.DBUtil;

public class AreaDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LngLatPoint2.db";
    public Context context;

    public AreaDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(db);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public void dropTable(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
    }
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.AreaEntry.TABLE_NAME + " (" +
                    DBContract.AreaEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.AreaEntry.COLUMN_NAME_NAME + DBUtil.TEXT_TYPE + DBUtil.COMMA_SEP +
                    DBContract.AreaEntry.COLUMN_NAME_AREA + DBUtil.FLOAT_TYPE + DBUtil.COMMA_SEP +
                    DBContract.AreaEntry.COLUMN_NAME_FREEBASE_ID + DBUtil.TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.AreaEntry.TABLE_NAME;
}