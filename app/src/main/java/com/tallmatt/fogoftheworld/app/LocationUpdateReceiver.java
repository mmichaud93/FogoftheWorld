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

    protected static String TAG = "PassiveLocationChangedReceiver";

    public LocationUpdateReceiver() {
        super();
    }

    /**
     * When a new location is received, extract it from the Intent and use
     * it to start the Service used to update the list of nearby places.
     *
     * This is the Passive receiver, used to receive Location updates from
     * third party apps when the Activity is not visible.
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(FogConstants.LOCATION_UPDATE_SERVICE_LOCATION)) {
            Location location = (Location)intent.getExtras().get(FogConstants.LOCATION_UPDATE_SERVICE_LOCATION);
            Log.d("TM", "Location got from update service");
            LatLngPointsDBHelper mDbHelper = new LatLngPointsDBHelper(context);
            ArrayList<LatLng> points = mDbHelper.getPoints(mDbHelper.getReadableDatabase());
            for(LatLng point: points) {
                if(Math.abs(point.latitude-location.getLatitude()) < FogConstants.LOCATION_IDENTICAL_THRESHOLD &&
                        Math.abs(point.longitude-location.getLongitude()) < FogConstants.LOCATION_IDENTICAL_THRESHOLD) {
                    return;
                }
            }
            mDbHelper.storeSinglePoint(mDbHelper.getWritableDatabase(), new LatLng(location.getLatitude(), location.getLongitude()));
            mDbHelper.close();
        }
    }
}