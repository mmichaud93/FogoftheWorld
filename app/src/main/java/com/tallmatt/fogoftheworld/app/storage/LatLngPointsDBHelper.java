package com.tallmatt.fogoftheworld.app.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.util.Log;
import android.view.ActionMode;

import com.google.android.gms.maps.model.LatLng;
import com.tallmatt.fogoftheworld.app.PointLatLng;

import java.util.ArrayList;

/**
 * Created by michaudm3 on 5/27/2014.
 */
public class LatLngPointsDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LngLatPoint.db";
    public Context context;

    public LatLngPointsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public void dropTable(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
    }
    public void storePoints(SQLiteDatabase db, ArrayList<PointLatLng> points) {
        dropTable(db);
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d("TM", "saving " + points.size() + " points");
        for(PointLatLng point : points) {
            storeSinglePointLatLng(db, point);
        }
    }
    interface StoreSinglePointCallback {
        void complete();
    }
    public void storeSinglePoint(SQLiteDatabase db, LatLng point) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LatLngPointsContract.PointEntry.COLUMN_NAME_LAT, point.latitude);
        values.put(LatLngPointsContract.PointEntry.COLUMN_NAME_LNG, point.longitude);

        // Insert the new row, returning the primary key value of the new row
        db.insert(
                LatLngPointsContract.PointEntry.TABLE_NAME,
                "null",
                values);
    }
    public void storeSinglePoint(SQLiteDatabase db, LatLng point, long time) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LatLngPointsContract.PointEntry.COLUMN_NAME_LAT, point.latitude);
        values.put(LatLngPointsContract.PointEntry.COLUMN_NAME_LNG, point.longitude);
        values.put(LatLngPointsContract.PointEntry.COLUMN_NAME_TIME, time);

        // Insert the new row, returning the primary key value of the new row
        db.insert(
                LatLngPointsContract.PointEntry.TABLE_NAME,
                "null",
                values);
    }
    public void storeSinglePoint(SQLiteDatabase db, LatLng point, StoreSinglePointCallback callback) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LatLngPointsContract.PointEntry.COLUMN_NAME_LAT, point.latitude);
        values.put(LatLngPointsContract.PointEntry.COLUMN_NAME_LNG, point.longitude);

        // Insert the new row, returning the primary key value of the new row
        db.insert(
                LatLngPointsContract.PointEntry.TABLE_NAME,
                "null",
                values);
        callback.complete();
    }
    public void storeSinglePointLatLng(SQLiteDatabase db, PointLatLng point) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LatLngPointsContract.PointEntry.COLUMN_NAME_LAT, point.latLng.latitude);
        values.put(LatLngPointsContract.PointEntry.COLUMN_NAME_LNG, point.latLng.longitude);
        values.put(LatLngPointsContract.PointEntry.COLUMN_NAME_TIME, point.time);
        values.put(LatLngPointsContract.PointEntry.COLUMN_NAME_SRC, point.source);

        // Insert the new row, returning the primary key value of the new row
        db.insert(
                LatLngPointsContract.PointEntry.TABLE_NAME,
                "null",
                values);
    }
    public ArrayList<PointLatLng> getPointLatLngs(SQLiteDatabase db) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                LatLngPointsContract.PointEntry.COLUMN_NAME_LAT,
                LatLngPointsContract.PointEntry.COLUMN_NAME_LNG,
                LatLngPointsContract.PointEntry.COLUMN_NAME_TIME,
                LatLngPointsContract.PointEntry.COLUMN_NAME_SRC
        };

        // How you want the results sorted in the resulting Cursor

        Cursor c = db.query(
                LatLngPointsContract.PointEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        ArrayList<PointLatLng> points = new ArrayList<PointLatLng>();
        if(c.moveToFirst()) {
            do {
                points.add(new PointLatLng(
                        new LatLng(c.getFloat(0), c.getFloat(1)),
                        c.getLong(2), c.getString(3)));
            } while(c.moveToNext());
        }
        return points;
    }
    public ArrayList<LatLng> getPoints(SQLiteDatabase db) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                LatLngPointsContract.PointEntry.COLUMN_NAME_LAT,
                LatLngPointsContract.PointEntry.COLUMN_NAME_LNG
        };

        // How you want the results sorted in the resulting Cursor

        Cursor c = db.query(
                LatLngPointsContract.PointEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        ArrayList<LatLng> points = new ArrayList<LatLng>();
        if(c.moveToFirst()) {
            do {
                points.add(new LatLng(c.getFloat(0), c.getFloat(1)));
            } while(c.moveToNext());
        }
        return points;
    }
    public ArrayList<Long> getTimes(SQLiteDatabase db) {
        String[] projection = {
                LatLngPointsContract.PointEntry.COLUMN_NAME_TIME
        };

        Cursor c = db.query(
                LatLngPointsContract.PointEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        ArrayList<Long> times = new ArrayList<Long>();
        if(c.moveToFirst()) {
            do {
                times.add(new Long(c.getLong(0)));
            } while(c.moveToNext());
        }
        return times;
    }
    public ArrayList<String> getSources(SQLiteDatabase db) {
        String[] projection = {
                LatLngPointsContract.PointEntry.COLUMN_NAME_SRC
        };

        Cursor c = db.query(
                LatLngPointsContract.PointEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        ArrayList<String> times = new ArrayList<String>();
        if(c.moveToFirst()) {
            do {
                times.add(c.getString(0));
            } while(c.moveToNext());
        }
        return times;
    }
    private static final String TEXT_TYPE = " TEXT";
    private static final String FLOAT_TYPE = " FLOAT";
    private static final String LONG_TYPE = " LONG";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LatLngPointsContract.PointEntry.TABLE_NAME + " (" +
                    LatLngPointsContract.PointEntry._ID + " INTEGER PRIMARY KEY," +
                    LatLngPointsContract.PointEntry.COLUMN_NAME_LAT + FLOAT_TYPE + COMMA_SEP +
                    LatLngPointsContract.PointEntry.COLUMN_NAME_LNG + FLOAT_TYPE + COMMA_SEP +
                    LatLngPointsContract.PointEntry.COLUMN_NAME_ACC + FLOAT_TYPE + COMMA_SEP +
                    LatLngPointsContract.PointEntry.COLUMN_NAME_TIME + LONG_TYPE + COMMA_SEP +
                    LatLngPointsContract.PointEntry.COLUMN_NAME_SRC + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LatLngPointsContract.PointEntry.TABLE_NAME;
}