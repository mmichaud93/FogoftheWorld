package com.tallmatt.fogoftheworld.app.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.tallmatt.fogoftheworld.app.FogConstants;
import com.tallmatt.fogoftheworld.app.LocationUpdateService;
import com.tallmatt.fogoftheworld.app.MaskTileProvider;
import com.tallmatt.fogoftheworld.app.PointLatLng;
import com.tallmatt.fogoftheworld.app.R;
import com.tallmatt.fogoftheworld.app.storage.LatLngPointsDBHelper;

import java.util.ArrayList;

/**
 * Created by michaudm3 on 5/12/2014.
 */
public class FogFragment extends Fragment {

    public static FogFragment newInstance() {
        FogFragment fragment = new FogFragment();
        return fragment;
    }

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private TileOverlay overlay;
    private MaskTileProvider tileProvider;
    ArrayList<PointLatLng> points = new ArrayList<PointLatLng>();
    LatLngPointsDBHelper mDbHelper;
    private ServiceConnection mConnection;
    LocationManager locationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* launch the service */
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("TM", "Service Connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("TM", "Service Disconnected");
            }
        };
        /* load the points from the database */
        mDbHelper = new LatLngPointsDBHelper(getActivity());
        startService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fog, container, false);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, mapFragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        points = mDbHelper.getPointLatLngs(mDbHelper.getReadableDatabase());
        if (mMap == null) {
            mMap = mapFragment.getMap();
            setUpMap();
        } else {
            tileProvider.setPoints(points);
            overlay.clearTileCache();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        endService();
    }

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setIndoorEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(points.size() - 1).latLng, 14));

        // Create new TileOverlayOptions instance.
        tileProvider = new MaskTileProvider(mMap);
        tileProvider.setPoints(points);
        TileOverlayOptions opts = new TileOverlayOptions();
        opts.fadeIn(true);
        // Set the tile provider to your custom implementation.
        opts.tileProvider(tileProvider);
        // Add the tile overlay to the map.
        overlay = mMap.addTileOverlay(opts);
    }

    public void onLocationGet(Location location, String source) {
        if (location != null) {
            for(PointLatLng point: points) {
                if(Math.abs(point.latLng.latitude-location.getLatitude()) < FogConstants.LOCATION_IDENTICAL_THRESHOLD &&
                        Math.abs(point.latLng.longitude-location.getLongitude()) < FogConstants.LOCATION_IDENTICAL_THRESHOLD) {
                    return;
                }
            }
            points.add(new PointLatLng(new LatLng(location.getLatitude(), location.getLongitude()),
                    System.currentTimeMillis(), source ));
            mDbHelper.storeSinglePointLatLng(mDbHelper.getWritableDatabase(), points.get(points.size()-1));
            tileProvider.setPoints(points);
        }
    }

    public void startService() {
        /* bind the service, this lets us listen for location updates when the app is in the background */
        getActivity().bindService(new Intent(getActivity(), LocationUpdateService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    public void endService() {
        getActivity().unbindService(mConnection);
    }
}
