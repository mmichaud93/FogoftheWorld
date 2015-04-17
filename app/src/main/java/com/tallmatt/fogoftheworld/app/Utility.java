package com.tallmatt.fogoftheworld.app;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by michaudm3 on 7/7/2014.
 */
public class Utility {
    public static String[] getAddress(Context context, Location location) {
        String[] addr = new String[3];

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        // Create a list to contain the result address
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e1) {
        } catch (IllegalArgumentException e2) {
        }
        // If the reverse geocode returned an address
        if (addresses != null && addresses.size() > 0) {
            // Get the first address
            Address address = addresses.get(0);
                /*
                 * Format the first line of address (if available),
                 * city, and country name.
                 */
            addr[0] = address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "";
            addr[1] = address.getLocality();
            addr[2] = address.getCountryName();
        } else {

        }

        return addr;
    }

    public static float dpToPx(float dp, Context context) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    public static float pxToDp(float px, Context context) {
        return px / context.getResources().getDisplayMetrics().density;
    }

}
