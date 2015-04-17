package com.tallmatt.fogoftheworld.app;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.tallmatt.fogoftheworld.app.ui.TabsPagerAdapter;
import com.tallmatt.fogoftheworld.app.ui.utility.AreYouSureDialogFragment;

public class FogActivity extends FragmentActivity implements ActionBar.TabListener {

    public static final String USERNAME = "TheTallMatt";

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
