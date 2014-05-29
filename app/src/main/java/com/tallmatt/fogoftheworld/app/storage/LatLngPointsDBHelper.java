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
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public void dropTable(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
    }
    public void storePoints(SQLiteDatabase db, ArrayList<LatLng> points) {
        dropTable(db);
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d("TM", "saving "+points.size()+" points");
        for(LatLng point : points) {
            storeSinglePoint(db, point);
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
        //Log.d("TM", "c count: "+c.getCount());
        if(c.moveToFirst()) {
            do {
                points.add(new LatLng(c.getFloat(0), c.getFloat(1)));
                //Log.d("TM", c.getPosition()+": ("+c.getFloat(0)+", "+c.getFloat(1)+")");
            } while(c.moveToNext());
        }
        return points;
    }
    private static final String TEXT_TYPE = " TEXT";
    private static final String FLOAT_TYPE = " FLOAT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LatLngPointsContract.PointEntry.TABLE_NAME + " (" +
                    LatLngPointsContract.PointEntry._ID + " INTEGER PRIMARY KEY," +
                    LatLngPointsContract.PointEntry.COLUMN_NAME_LAT + FLOAT_TYPE + COMMA_SEP +
                    LatLngPointsContract.PointEntry.COLUMN_NAME_LNG + FLOAT_TYPE + COMMA_SEP +
                    LatLngPointsContract.PointEntry.COLUMN_NAME_ACC + FLOAT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LatLngPointsContract.PointEntry.TABLE_NAME;
}