package com.tallmatt.fogoftheworld.app;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.tallmatt.fogoftheworld.app.storage.LatLngPointsDBHelper;
import com.tallmatt.fogoftheworld.app.ui.utility.AreYouSureDialogFragment;
import com.tallmatt.fogoftheworld.app.ui.utility.DataDialogFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FogActivity extends FragmentActivity {

    private GoogleMap mMap;
    private TileOverlay overlay;
    private MaskTileProvider tileProvider;
    ArrayList<PointLatLng> points = new ArrayList<PointLatLng>();
    LatLngPointsDBHelper mDbHelper;
    private ServiceConnection mConnection;
    LocationManager locationManager;

    public static PointLatLng lastLatLng;

    //Switch exploreSwitch;
    LinearLayout dragView;
    TextView dragText;
    SlidingUpPanelLayout slidingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fog);

        //exploreSwitch = (Switch) findViewById(R.id.explore_switch);
        dragView = (LinearLayout) findViewById(R.id.panel_drag_view);
        dragText = (TextView) findViewById(R.id.panel_drag_text);
        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingPanel.setDragView(dragView);
        slidingPanel.setPanelSlideListener( new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelCollapsed(View view) {
                dragText.setText("More");
            }

            @Override
            public void onPanelExpanded(View view) {
                dragText.setText("Less");
            }

            @Override
            public void onPanelAnchored(View view) {

            }
        });
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
        //exploreSwitch.setOnCheckedChangeListener(exploreSwitchChecked);

        /* load the points from the database */
        mDbHelper = new LatLngPointsDBHelper(this);
        points = mDbHelper.getPointLatLngs(mDbHelper.getReadableDatabase());
        lastLatLng = points.get(points.size()-1);

        //locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

//        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, FogConstants.LOCATION_UPDATE_TIME, FogConstants.LOCATION_UPDATE_DISTANCE, networkListener);
//            Log.d("TM", "Update Service Created with Network");
//        }
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, FogConstants.LOCATION_UPDATE_TIME, FogConstants.LOCATION_UPDATE_DISTANCE, GPSListener);
//            Log.d("TM", "Update Service Created with GPS");
//        }

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        points = mDbHelper.getPointLatLngs(mDbHelper.getReadableDatabase());
        lastLatLng = points.get(points.size()-1);
        tileProvider.setPoints(points);
        overlay.clearTileCache();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dev_menu, menu);
        return true;
    }

    AreYouSureDialogFragment areYouSure;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.dev_menu_refresh:
                points = mDbHelper.getPointLatLngs(mDbHelper.getReadableDatabase());
                tileProvider.setPoints(points);
                overlay.clearTileCache();
                return true;
            case R.id.dev_menu_wipe_database:
                areYouSure = new AreYouSureDialogFragment(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        overlay.clearTileCache();
                        mDbHelper.dropTable(mDbHelper.getWritableDatabase());
                        mDbHelper.onCreate(mDbHelper.getWritableDatabase());
                        if(areYouSure!=null) {
                            areYouSure.dismiss();
                        }
                    }
                }, null);
                areYouSure.show(this.getFragmentManager(), "");
                return true;
            case R.id.dev_menu_data:
                DataDialogFragment dataFragment = DataDialogFragment.newInstance(points);
                dataFragment.show(this.getFragmentManager(), "");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setIndoorEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(points.size()-1).latLng, 14));

        // Create new TileOverlayOptions instance.
        tileProvider = new MaskTileProvider(mMap);
        tileProvider.setPoints(points);
        TileOverlayOptions opts = new TileOverlayOptions();
        opts.fadeIn(true);
        // Set the tile provider to your custom implementation.
        opts.tileProvider(tileProvider);
        // Add the tile overlay to the map.
        overlay = mMap.addTileOverlay(opts);
        /* updater for when the app is in the forefront, this should stop working when the app goes to the back */
//        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//            @Override
//            public void onMyLocationChange(Location location) {
//                if (location != null) {
//                    for(LatLng point: points) {
//                        if(Math.abs(point.latitude-location.getLatitude()) < FogConstants.LOCATION_IDENTICAL_THRESHOLD &&
//                           Math.abs(point.longitude-location.getLongitude()) < FogConstants.LOCATION_IDENTICAL_THRESHOLD) {
//                            return;
//                        }
//                    }
//                    points.add(new LatLng(location.getLatitude(), location.getLongitude()));
//                    mDbHelper.storeSinglePoint(mDbHelper.getWritableDatabase(), points.get(points.size()-1));
//                    tileProvider.setPoints(points);
//                }
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TM", "GPS listener shut down");
        //locationManager.removeUpdates(GPSListener);
        Log.d("TM", "Network listener shut down");
        //locationManager.removeUpdates(networkListener);
        mDbHelper.close();
    }

    LocationListener GPSListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("TM", "GPS location received");
            onLocationGet(location, FogConstants.SOURCE_GPS);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        @Override
        public void onProviderEnabled(String provider) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, FogConstants.LOCATION_UPDATE_TIME, FogConstants.LOCATION_UPDATE_DISTANCE, this);
        }
        @Override
        public void onProviderDisabled(String provider) {
            locationManager.removeUpdates(this);
        }
    };

    LocationListener networkListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("TM", "Network location received");
            onLocationGet(location, FogConstants.SOURCE_NETWORK);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        @Override
        public void onProviderEnabled(String provider) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, FogConstants.LOCATION_UPDATE_TIME, FogConstants.LOCATION_UPDATE_DISTANCE, this);
        }
        @Override
        public void onProviderDisabled(String provider) {
            locationManager.removeUpdates(this);
        }
    };

    CompoundButton.OnCheckedChangeListener exploreSwitchChecked = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                /* bind the service, this lets us listen for location updates when the app is in the background */
                bindService(new Intent(getBaseContext(), LocationUpdateService.class), mConnection, Context.BIND_AUTO_CREATE);
            } else {
                getBaseContext().unbindService(mConnection);
            }
        }
    };

    public void onLocationGet(Location location, String source) {
        if (location != null) {
            for(PointLatLng point: points) {
                if(Math.abs(point.latLng.latitude-location.getLatitude()) < FogConstants.LOCATION_IDENTICAL_THRESHOLD &&
                        Math.abs(point.latLng.longitude-location.getLongitude()) < FogConstants.LOCATION_IDENTICAL_THRESHOLD) {
                    return;
                }
            }
//            if(!FogConstants.checkPointValidity(new PointLatLng(new LatLng(location.getLatitude(), location.getLongitude()),
//                    System.currentTimeMillis(), source ))) {
//                return;
//            }
            points.add(new PointLatLng(new LatLng(location.getLatitude(), location.getLongitude()),
                    System.currentTimeMillis(), source ));
            FogActivity.lastLatLng = points.get(points.size()-1);
            mDbHelper.storeSinglePointLatLng(mDbHelper.getWritableDatabase(), points.get(points.size()-1));
            tileProvider.setPoints(points);
        }
    }
}
