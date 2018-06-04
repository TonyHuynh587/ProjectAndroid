package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceTokenReponse {

    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("platform")
    @Expose
    private String platform;

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getPlatform() {
        return platform;
    }
}
