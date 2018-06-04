
package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpacingMultiplications {

    @SerializedName("double")
    @Expose
    private int _double;
    @SerializedName("single")
    @Expose
    private int single;

    public int getDouble() {
        return _double;
    }

    public void setDouble(int _double) {
        this._double = _double;
    }

    public int getSingle() {
        return single;
    }

    public void setSingle(int single) {
        this.single = single;
    }

}
