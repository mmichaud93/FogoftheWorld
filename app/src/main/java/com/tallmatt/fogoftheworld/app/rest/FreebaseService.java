package com.tallmatt.fogoftheworld.app.rest;

import com.tallmatt.fogoftheworld.app.models.LocationAreaModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by michaudm3 on 7/8/2014.
 */
public interface FreebaseService {
    @GET("/")
    void getLocationArea(@Query("query") String query, Callback<LocationAreaModel> callback);

}
