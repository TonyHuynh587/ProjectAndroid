package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CouponReponseOrder extends Coupon {
    @SerializedName("valid")
    @Expose
    private int valid;

    public int getValid() {
        return valid;
    }
}
