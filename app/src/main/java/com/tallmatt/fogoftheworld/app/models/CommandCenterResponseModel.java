package com.tallmatt.fogoftheworld.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mmichaud on 4/16/15.
 */
public class CommandCenterResponseModel {
    @Expose
    @SerializedName("msg")
    public String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
