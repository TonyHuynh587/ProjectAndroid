package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseOrder {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("price")
    @Expose
    private double price;
    private CouponReponseOrder coupon;

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
}
