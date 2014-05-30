package com.tallmatt.fogoftheworld.app.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tallmatt.fogoftheworld.app.R;

/**
 * Created by michaudm3 on 5/12/2014.
 */
public class FogFragment extends Fragment {

    /*
     * I dont need this class just yet but I'm just keeping it around so that the ui.utility package doesn't truncate in Android Studio.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fog, container);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
