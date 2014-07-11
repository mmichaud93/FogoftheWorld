package com.tallmatt.fogoftheworld.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tallmatt.fogoftheworld.app.PointLatLng;
import com.tallmatt.fogoftheworld.app.R;
import com.tallmatt.fogoftheworld.app.models.LocationAreaModel;
import com.tallmatt.fogoftheworld.app.rest.FreebaseService;
import com.tallmatt.fogoftheworld.app.storage.AreaDBHelper;
import com.tallmatt.fogoftheworld.app.storage.LatLngPointsDBHelper;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by michaudm3 on 6/5/2014.
 */
public class StatsFragment  extends Fragment {

    public static StatsFragment newInstance() {
        StatsFragment fragment = new StatsFragment();
        return fragment;
    }

    LatLngPointsDBHelper mDbHelper;
    AreaDBHelper areaDbHelper;
    ArrayList<PointLatLng> points = new ArrayList<PointLatLng>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* load the points from the database */
        mDbHelper = new LatLngPointsDBHelper(getActivity());
        points = mDbHelper.getPointLatLngs(mDbHelper.getReadableDatabase());
        mDbHelper.close();

        areaDbHelper = new AreaDBHelper(getActivity());

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://www.googleapis.com/freebase/v1/mqlread")
                .build();

        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpclient = new DefaultHttpClient();

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    String query = "[{\"type\": \"/location/location\", \"id\": null, \"name~=\": \"Boston\", \"area\": null, \"containedby\": []}]";
                    params.add(new BasicNameValuePair("query", query));
                    params.add(new BasicNameValuePair("key", "AIzaSyBRYV66vCg8lBPn9pQZvWEdlvuk7qIm4eQ"));

                    String serviceURL = "https://www.googleapis.com/freebase/v1/mqlread";
                    String url = serviceURL + "?" + URLEncodedUtils.format(params, "UTF-8");
                    HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

                    Log.d("TM", EntityUtils.toString(httpResponse.getEntity()));
                } catch(Exception e) {
                    Log.d("TM", e.getMessage());
                }
            }
        };
        Thread thread = new Thread(run);
        thread.start();

       /*FreebaseService service = restAdapter.create(FreebaseService.class);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            String query = "[{\"type\": \"/location/location\", \"id\": null, \"name~=\": \"Boston\", \"area\": null, \"containedby\": []}]";
            params.add(new BasicNameValuePair("query", query));
            params.add(new BasicNameValuePair("key", "AIzaSyBRYV66vCg8lBPn9pQZvWEdlvuk7qIm4eQ"));
            service.getLocationArea(URLEncodedUtils.format(params, "UTF-8"),
                    new Callback<LocationAreaModel>() {
                        @Override
                        public void success(LocationAreaModel locationAreaModel, Response response) {
                            Log.d("TM", "success: "+response.getBody().toString());
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if(error!=null) {
                                Log.d("TM", "failure: " + error.toString());
                                Log.d("TM", "url: " + error.getUrl());
                                Log.d("TM", "failure: " + error.getResponse().getBody());
                                Log.d("TM", "failure: " + error.getResponse().getReason());
                                Log.d("TM", "failure: " + error.getResponse().getStatus());
                            } else {
                                Log.d("TM", "just an error");
                            }
                        }
                    });*/
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