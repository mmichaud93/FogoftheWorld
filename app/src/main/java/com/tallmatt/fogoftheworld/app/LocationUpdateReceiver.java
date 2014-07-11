package com.tallmatt.fogoftheworld.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.tallmatt.fogoftheworld.app.quadtree.QuadTree;
import com.tallmatt.fogoftheworld.app.storage.LatLngPointsDBHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by michaudm3 on 5/17/2014.
 */
public class LocationUpdateReceiver extends BroadcastReceiver {

    ArrayList<PointLatLng> points;
    QuadTree quadTree;
    LatLngPointsDBHelper mDbHelper;
    public LocationUpdateReceiver() {
        super();
        quadTree = new QuadTree(-180.0, -90.0, 180.0, 90.0);
    }

    /**
     * When a new location is received, extract it from the Intent and put it into the database
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        if(points==null) {
            mDbHelper = new LatLngPointsDBHelper(context);
            points = mDbHelper.getPointLatLngs(mDbHelper.getReadableDatabase());
            for(PointLatLng point : points) {
                quadTree.set(point.latLng.latitude, point.latLng.longitude, point);
            }
        }
        if (intent.hasExtra(FogConstants.LOCATION_UPDATE_SERVICE_LOCATION)) {
            Location location = (Location)intent.getExtras().get(FogConstants.LOCATION_UPDATE_SERVICE_LOCATION);

            for(PointLatLng point: points) {
                if(Math.abs(point.latLng.latitude-location.getLatitude()) < FogConstants.LOCATION_IDENTICAL_THRESHOLD &&
                        Math.abs(point.latLng.longitude-location.getLongitude()) < FogConstants.LOCATION_IDENTICAL_THRESHOLD) {
                    return;
                }
            }
            points.add(new PointLatLng(
                    new LatLng(location.getLatitude(), location.getLongitude()), System.currentTimeMillis(), FogConstants.SOURCE_GPS,
                    Utility.getAddress(context, location)));
            quadTree.set(points.get(points.size()-1).latLng.latitude, points.get(points.size()-1).latLng.longitude, points.get(points.size()-1));
            mDbHelper.storeSinglePointLatLng(mDbHelper.getWritableDatabase(), points.get(points.size()-1));
            mDbHelper.close();
        }
    }
}