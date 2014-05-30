package com.tallmatt.fogoftheworld.app.storage;

import android.provider.BaseColumns;

/**
 * Created by michaudm3 on 5/27/2014.
 */
public final class LatLngPointsContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public LatLngPointsContract() {}

    /* Inner class that defines the table contents */
    public static abstract class PointEntry implements BaseColumns {
        public static final String TABLE_NAME = "points";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LNG = "lng";
        public static final String COLUMN_NAME_ACC = "accuracy";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_SRC = "src";
    }
}
