package com.tallmatt.fogoftheworld.app;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by michaudm3 on 5/30/2014.
 */
public class PointLatLng {
    public LatLng latLng;
    public long time;
    public String source;

    public PointLatLng(LatLng latLng, long time, String source) {
        this.latLng = latLng;
        this.time = time;
        this.source = source;
    }
}
