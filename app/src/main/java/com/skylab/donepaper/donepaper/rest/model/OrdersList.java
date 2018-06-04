package com.skylab.donepaper.donepaper.rest.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class OrdersList {
    private String result;

    @SerializedName("data")
    private List<OrderData> orders;

    @SerializedName("errors")
    private List<?> error;

    public String getResult() {
        return result;
    }

    public List<OrderData> getOrders() {
        return orders;
    }

    public List<?> getError() {
        return error;
    }
}
