package com.tallmatt.fogoftheworld.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tallmatt.fogoftheworld.app.R;

/**
 * Created by michaudm3 on 6/5/2014.
 */
public class StatsFragment  extends Fragment {

    public static StatsFragment newInstance() {
        StatsFragment fragment = new StatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stats, container, false);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}