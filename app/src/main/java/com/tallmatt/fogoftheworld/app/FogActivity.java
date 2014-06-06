package com.tallmatt.fogoftheworld.app;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
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
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.tallmatt.fogoftheworld.app.ui.TabsPagerAdapter;
import com.tallmatt.fogoftheworld.app.ui.utility.AreYouSureDialogFragment;
import com.tallmatt.fogoftheworld.app.ui.utility.DataDialogFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FogActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private ActionBar actionBar;
    private TabsPagerAdapter mAdapter;
    private String[] tabs = {"Map", "Stats"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fog);

        viewPager = (ViewPager) findViewById(R.id.fog_pager);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
//        switch (item.getItemId()) {
//            case R.id.dev_menu_refresh:
//                points = mDbHelper.getPointLatLngs(mDbHelper.getReadableDatabase());
//                tileProvider.setPoints(points);
//                overlay.clearTileCache();
//                return true;
//            case R.id.dev_menu_wipe_database:
//                areYouSure = new AreYouSureDialogFragment(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        overlay.clearTileCache();
//                        mDbHelper.dropTable(mDbHelper.getWritableDatabase());
//                        mDbHelper.onCreate(mDbHelper.getWritableDatabase());
//                        if(areYouSure!=null) {
//                            areYouSure.dismiss();
//                        }
//                    }
//                }, null);
//                areYouSure.show(this.getFragmentManager(), "");
//                return true;
//            case R.id.dev_menu_data:
//                DataDialogFragment dataFragment = DataDialogFragment.newInstance(points);
//                dataFragment.show(this.getFragmentManager(), "");
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
        return false;
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
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
