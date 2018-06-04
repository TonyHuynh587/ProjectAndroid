package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenData {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("expiry")
    @Expose
    private int expiry;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;

    public String getToken() {
        return token;
    }

    public int getExpiry() {
        return expiry;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }
}
