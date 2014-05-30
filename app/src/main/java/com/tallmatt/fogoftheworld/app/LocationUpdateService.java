package com.tallmatt.fogoftheworld.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

/**
 * Created by michaudm3 on 5/20/2014.
 */
public class LocationUpdateService extends Service {

    LocationManager locationManager;

    long locationUpdateTime = 15000;
    float locationUpdateDistance = 15;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

       // locationManager.requestLocationUpdates(locationUpdateTime, locationUpdateDistance, FogConstants.createHighCriteria(), GPSListener, null);

        /* might work with these disabled, ill have to test it more */
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locationUpdateTime, locationUpdateDistance, GPSListener);
            Log.d("TM", "Update Service Created with GPS");
        }
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, locationUpdateTime, locationUpdateDistance, networkListener);
            Log.d("TM", "Update Service Created with Network");
        }
        if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, locationUpdateTime, locationUpdateDistance, passiveListener);
            Log.d("TM", "Update Service Created with Passive");
        }

        super.onCreate();
    }

    LocationListener GPSListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            registerLocationChange(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locationUpdateTime, locationUpdateDistance, this);
        }

        @Override
        public void onProviderDisabled(String provider) {
            locationManager.removeUpdates(this);
        }
    };

    LocationListener networkListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            registerLocationChange(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, locationUpdateTime, locationUpdateDistance, this);
        }

        @Override
        public void onProviderDisabled(String provider) {
            locationManager.removeUpdates(this);
        }
    };

    LocationListener passiveListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            registerLocationChange(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, locationUpdateTime, locationUpdateDistance, this);
        }

        @Override
        public void onProviderDisabled(String provider) {
            locationManager.removeUpdates(this);
        }
    };

    public void registerLocationChange(Location location) {
        Log.d("TM", "location changed ("+location.getLatitude()+", "+location.getLongitude()+")");
        Intent intent = new Intent();
        intent.setAction(FogConstants.LOCATION_UPDATE_SERVICE_ACTION);
        intent.putExtra(FogConstants.LOCATION_UPDATE_SERVICE_LOCATION, location);
        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        Log.d("TM", "Update Service Destroyed");
        locationManager.removeUpdates(GPSListener);
        locationManager.removeUpdates(networkListener);
        locationManager.removeUpdates(passiveListener);
    }
}
