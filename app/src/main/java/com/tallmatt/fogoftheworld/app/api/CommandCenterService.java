package com.tallmatt.fogoftheworld.app.api;

import com.tallmatt.fogoftheworld.app.models.CommandCenterResponseModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by mmichaud on 4/16/15.
 */
public interface CommandCenterService {
    @GET("/putLatLng")
    public void putLatLng(@Query("user") String user, @Query("lat") double lat,
                          @Query("lng") double lng, @Query("timestamp") long timestamp,
                          Callback<CommandCenterResponseModel> callback);

    @GET("/putDatabaseInfo")
    public void putDatabaseInfo(@Query("user") String user, @Query("dbsize") int dbsize,
                          @Query("loadtime") long loadtime, @Query("timestamp") long timestamp,
                          Callback<CommandCenterResponseModel> callback);
}
