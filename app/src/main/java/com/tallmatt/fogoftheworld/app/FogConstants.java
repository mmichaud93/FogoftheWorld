package com.tallmatt.fogoftheworld.app;

import android.location.Criteria;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by michaudm3 on 5/20/2014.
 */
public class FogConstants {
    public static final String LOCATION_UPDATE_SERVICE_ACTION = "LOCATION_GET";
    public static final String LOCATION_UPDATE_SERVICE_LOCATION = "LOCATION";
    public static final double LOCATION_IDENTICAL_THRESHOLD = 0.00025;

    public static final long LOCATION_UPDATE_TIME = 2500;
    public static final float LOCATION_UPDATE_DISTANCE = 10;

    public static final String SOURCE_UNKNOWN = "UNKNOWN";
    public static final String SOURCE_GPS = "GPS";
    public static final String SOURCE_NETWORK= "NETWORK";
    public static final String SOURCE_PASSIVE = "PASSIVE";

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

    public static boolean checkPointValidity(PointLatLng point) {
        /*
         * if the distance is really large and the time is really small, then it is a bogus point
         */
        if(measureDistanceInMeters(point.latLng, FogActivity.lastLatLng.latLng) > 50) {
            if(point.time-FogActivity.lastLatLng.time < 1000) {
                return false;
            }
        }
        return true;
    }

    public static double measureDistanceInMeters(LatLng latLng1, LatLng latLng2){  // generally used geo measurement function
        double R = 6378.137; // Radius of earth in KM
        double dLat = (latLng2.latitude - latLng1.latitude) * Math.PI / 180.0;
        double dLon = (latLng2.longitude - latLng1.longitude) * Math.PI / 180.8;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(latLng1.latitude * Math.PI / 180) * Math.cos(latLng2.latitude * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0-a));
        double d = R * c;
        return d * 1000.0; // meters
    }
}
