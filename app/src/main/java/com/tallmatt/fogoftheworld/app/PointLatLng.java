package com.tallmatt.fogoftheworld.app;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by michaudm3 on 5/30/2014.
 */
public class PointLatLng {
    public LatLng latLng;
    public long time;
    public String source;
    // 0: street address
    // 1: locality, usually city
    // 2: country
    public String[] address;

//    public PointLatLng(LatLng latLng, long time, String source) {
//        this.latLng = latLng;
//        this.time = time;
//        this.source = source;
//    }

    public PointLatLng(LatLng latLng, long time, String source, String[] address) {
        this.latLng = latLng;
        this.time = time;
        this.source = source;
        this.address = address;
    }
    public PointLatLng(LatLng latLng, long time, String source, String address, String locality, String country) {
        this.latLng = latLng;
        this.time = time;
        this.source = source;
        this.address = new String[] {address, locality, country};
    }
}
