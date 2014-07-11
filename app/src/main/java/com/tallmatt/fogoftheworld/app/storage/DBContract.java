package com.tallmatt.fogoftheworld.app.storage;

import android.provider.BaseColumns;

/**
 * Created by michaudm3 on 5/27/2014.
 */
public final class DBContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DBContract() {}

    /* Inner class that defines the table contents */
    public static abstract class PointEntry implements BaseColumns {
        public static final String TABLE_NAME = "points";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LNG = "lng";
        public static final String COLUMN_NAME_ACC = "accuracy";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_LOCALITY = "locality";
        public static final String COLUMN_NAME_COUNTRY = "country";
        public static final String COLUMN_NAME_SRC = "src";
    }

    public static abstract class AreaEntry implements BaseColumns {
        public static final String TABLE_NAME = "area";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_AREA = "area";
        public static final String COLUMN_NAME_FREEBASE_ID = "freebase_id";
    }
}
