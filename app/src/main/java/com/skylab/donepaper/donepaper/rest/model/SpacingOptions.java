
package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpacingOptions {

    @SerializedName("single")
    @Expose
    private String single;
    @SerializedName("double")
    @Expose
    private String _double;

    public String getSingle() {
        return single;
    }

    public void setSingle(String single) {
        this.single = single;
    }

    public String getDouble() {
        return _double;
    }

    public void setDouble(String _double) {
        this._double = _double;
    }

}
