package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResults {

    @SerializedName("results")
    List<OrderData> listOrderData;

    public List<OrderData> getListOrderData() {
        return listOrderData;
    }

    public void setListOrderData(List<OrderData> listOrderData) {
        this.listOrderData = listOrderData;
    }
}
