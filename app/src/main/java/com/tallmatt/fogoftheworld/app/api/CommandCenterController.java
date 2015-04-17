package com.tallmatt.fogoftheworld.app.api;

import com.tallmatt.fogoftheworld.app.models.CommandCenterResponseModel;

import retrofit.Callback;
import retrofit.RestAdapter;

/**
 * Created by mmichaud on 4/16/15.
 */
public class CommandCenterController {

    private static String TAG = "CommandCenterController";

    static CommandCenterService commandCenterService;

    static {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://command-center-api.herokuapp.com/api")
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();

        commandCenterService = restAdapter.create(CommandCenterService.class);
    }

    public static void logLatLng(String user, double lat, double lng, long timestamp,
                                 Callback<CommandCenterResponseModel> callback) {
        commandCenterService.putLatLng(user, lat, lng, timestamp, callback);
    }

    public static void logDatabaseLoad(String user, int dbsize, long loadtime, long timestamp,
                                 Callback<CommandCenterResponseModel> callback) {
        commandCenterService.putDatabaseInfo(user, dbsize, loadtime, timestamp, callback);
    }
}
