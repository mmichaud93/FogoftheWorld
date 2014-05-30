package com.tallmatt.fogoftheworld.app;

import android.location.Criteria;

/**
 * Created by michaudm3 on 5/20/2014.
 */
public class FogConstants {
    public static final String LOCATION_UPDATE_SERVICE_ACTION = "LOCATION_GET";
    public static final String LOCATION_UPDATE_SERVICE_LOCATION = "LOCATION";
    public static final double LOCATION_IDENTICAL_THRESHOLD = 0.000125;

    /** this criteria needs high accuracy, high power, and cost */
    public static Criteria createHighCriteria() {
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_LOW);
        return c;
    }
}
