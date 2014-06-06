package com.tallmatt.fogoftheworld.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.tallmatt.fogoftheworld.app.storage.LatLngPointsDBHelper;

import java.util.ArrayList;

/**
 * Created by michaudm3 on 5/17/2014.
 */
public class LocationUpdateReceiver extends BroadcastReceiver {

    public LocationUpdateReceiver() {
        super();
    }

    /**
     * When a new location is received, extract it from the Intent and put it into the database
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(FogConstants.LOCATION_UPDATE_SERVICE_LOCATION)) {
            Location location = (Location)intent.getExtras().get(FogConstants.LOCATION_UPDATE_SERVICE_LOCATION);
            LatLngPointsDBHelper mDbHelper = new LatLngPointsDBHelper(context);
            ArrayList<PointLatLng> points = mDbHelper.getPointLatLngs(mDbHelper.getReadableDatabase());
            for(PointLatLng point: points) {
                if(Math.abs(point.latLng.latitude-location.getLatitude()) < FogConstants.LOCATION_IDENTICAL_THRESHOLD &&
                        Math.abs(point.latLng.longitude-location.getLongitude()) < FogConstants.LOCATION_IDENTICAL_THRESHOLD) {
                    return;
                }
            }
            mDbHelper.storeSinglePointLatLng(mDbHelper.getWritableDatabase(), new PointLatLng(
                    new LatLng(location.getLatitude(), location.getLongitude()), System.currentTimeMillis(), FogConstants.SOURCE_PASSIVE));
//            FogActivity.lastLatLng = new PointLatLng(
//                    new LatLng(location.getLatitude(), location.getLongitude()), System.currentTimeMillis(), FogConstants.SOURCE_PASSIVE);
            mDbHelper.close();
        }
    }
}