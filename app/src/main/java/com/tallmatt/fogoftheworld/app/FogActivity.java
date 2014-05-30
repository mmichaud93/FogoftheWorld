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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
import com.tallmatt.fogoftheworld.app.storage.LatLngPointsDBHelper;
import com.tallmatt.fogoftheworld.app.ui.utility.AreYouSureDialogFragment;
import com.tallmatt.fogoftheworld.app.ui.utility.DataDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FogActivity extends FragmentActivity {

    private GoogleMap mMap;
    private TileOverlay overlay;
    private MaskTileProvider tileProvider;
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    LatLngPointsDBHelper mDbHelper;
    private ServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fog);

        /* load the points from the database */
        mDbHelper = new LatLngPointsDBHelper(this);
        points = mDbHelper.getPoints(mDbHelper.getReadableDatabase());

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
        /* bind the service, this lets us listen for location updates when the app is in the background */
        bindService(new Intent(this, LocationUpdateService.class), mConnection, Context.BIND_AUTO_CREATE);

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        points = mDbHelper.getPoints(mDbHelper.getReadableDatabase());
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
                points = mDbHelper.getPoints(mDbHelper.getReadableDatabase());
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
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(points.size()-1), 14));

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
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (location != null) {
                    for(LatLng point: points) {
                        if(Math.abs(point.latitude-location.getLatitude()) < FogConstants.LOCATION_IDENTICAL_THRESHOLD &&
                           Math.abs(point.longitude-location.getLongitude()) < FogConstants.LOCATION_IDENTICAL_THRESHOLD) {
                            return;
                        }
                    }
                    points.add(new LatLng(location.getLatitude(), location.getLongitude()));
                    mDbHelper.storeSinglePoint(mDbHelper.getWritableDatabase(), points.get(points.size()-1));
                    tileProvider.setPoints(points);
                }
            }
        });
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
        getBaseContext().unbindService(mConnection);
    }
}
