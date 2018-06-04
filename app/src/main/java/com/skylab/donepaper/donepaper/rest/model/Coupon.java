package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Coupon {
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("value")
    @Expose
    private int value; // Value of the coupon (%)
    @SerializedName("expiry")
    @Expose
    private int expiry;

    public String getCode() {
        return code;
    }

    public int getValue() {
        return value;
    }

    public int getExpiry() {
        return expiry;
    }
}
