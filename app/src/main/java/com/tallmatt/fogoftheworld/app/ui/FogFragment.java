package com.tallmatt.fogoftheworld.app.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.tallmatt.fogoftheworld.app.AnimationUtil;
import com.tallmatt.fogoftheworld.app.FogActivity;
import com.tallmatt.fogoftheworld.app.LocationUpdateService;
import com.tallmatt.fogoftheworld.app.MaskTileProvider;
import com.tallmatt.fogoftheworld.app.PointLatLng;
import com.tallmatt.fogoftheworld.app.R;
import com.tallmatt.fogoftheworld.app.Utility;
import com.tallmatt.fogoftheworld.app.api.CommandCenterController;
import com.tallmatt.fogoftheworld.app.models.CommandCenterResponseModel;
import com.tallmatt.fogoftheworld.app.quadtree.QuadTree;
import com.tallmatt.fogoftheworld.app.storage.LatLngPointsDBHelper;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by michaudm3 on 5/12/2014.
 */
public class FogFragment extends Fragment {

    public static FogFragment newInstance() {
        FogFragment fragment = new FogFragment();
        return fragment;
    }

    boolean exploreEnabled = false;
    ToggleButton exploreButton;
    LinearLayout exploreLayout;
    ProgressBar levelBar;
    TextView levelText;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private TileOverlay overlay;
    private MaskTileProvider tileProvider;
    ArrayList<PointLatLng> points = new ArrayList<PointLatLng>();
    LatLngPointsDBHelper mDbHelper;
    private ServiceConnection mConnection;

    QuadTree tree;

    int oldWidth = 0;
    double level;

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
        long start = System.currentTimeMillis();
        tree = new QuadTree(-180.0,-90.0,180.0,90.0);
        points = mDbHelper.getPointLatLngs(mDbHelper.getReadableDatabase());

        CommandCenterController.logDatabaseLoad(FogActivity.USERNAME, points.size(),
                System.currentTimeMillis() - start, System.currentTimeMillis(), new Callback<CommandCenterResponseModel>() {
                    @Override
                    public void success(CommandCenterResponseModel commandCenterResponseModel, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

        level = getCurrentLevel(points.size());
        Log.d("TM", "current level: "+level);
        for(PointLatLng point : points) {
            //Log.d("TM", point.address[0]+" : "+point.address[1]+" : "+point.address[2]);
            tree.set(point.latLng.latitude, point.latLng.longitude, point);
        }
        Log.d("TM", "points stored: "+points.size()+" in "+(System.currentTimeMillis()-start)+" ms");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fog, container, false);

        levelBar = (ProgressBar) root.findViewById(R.id.level_bar);
        levelBar.setProgress((int) (100 * (level % 1)));
        levelText = (TextView) root.findViewById(R.id.level_text);
        levelText.setText(Integer.toString((int) (level)));

        exploreButton = (ToggleButton) root.findViewById(R.id.explore_button);
        exploreLayout = (LinearLayout) root.findViewById(R.id.explore_layout);
        exploreButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                exploreEnabled = isChecked;
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);;
                if (exploreEnabled) {
                    // make the button a width of 48dp
                    oldWidth = exploreLayout.getWidth();
                    AnimationUtil.ResizeWidthAnimation anim = new AnimationUtil.ResizeWidthAnimation(exploreLayout, (int)(Utility.dpToPx(80, getActivity())));
                    anim.setDuration(300);
                    exploreLayout.startAnimation(anim);

                    if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        startService();
                        if(mMap!=null && mMap.getMyLocation()!=null) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()), 14), 500, new GoogleMap.CancelableCallback() {
                                @Override
                                public void onFinish() {

                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        }
                    } else {
                        exploreButton.setChecked(false);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        dialog.cancel();
                                    }
                                });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }
                } else {
                    AnimationUtil.ResizeWidthAnimation anim = new AnimationUtil.ResizeWidthAnimation(exploreLayout, oldWidth);
                    anim.setDuration(350);
                    exploreLayout.startAnimation(anim);
                    endService();
                }
            }
        });

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
        if(points.size()>0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(points.size() - 1).latLng, 14));
        }

        // Create new TileOverlayOptions instance.
        tileProvider = new MaskTileProvider(mMap, tree);
        //tileProvider.setPoints(points);
        TileOverlayOptions opts = new TileOverlayOptions();
        opts.fadeIn(true);
        // Set the tile provider to your custom implementation.
        opts.tileProvider(tileProvider);
        // Add the tile overlay to the map.
        overlay = mMap.addTileOverlay(opts);
    }

    /*public void onLocationGet(Location location, String source) {
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
    }*/
    private boolean serviceActive = false;
    public void startService() {
        /* bind the service, this lets us listen for location updates when the app is in the background */
        getActivity().bindService(new Intent(getActivity(), LocationUpdateService.class), mConnection, Context.BIND_AUTO_CREATE);
        serviceActive = true;
    }

    public void endService() {
        if(serviceActive) {
            getActivity().unbindService(mConnection);
            serviceActive = false;
        }
    }

    private double getCurrentLevel(int pointCount) {
        return 0.25f*Math.sqrt(pointCount);
    }
}
