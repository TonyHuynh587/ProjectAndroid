
package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServicesTypes {

    @SerializedName("writing")
    @Expose
    private String writing;
    @SerializedName("revising")
    @Expose
    private String revising;

    public String getWriting() {
        return writing;
    }

    public void setWriting(String writing) {
        this.writing = writing;
    }

    public String getRevising() {
        return revising;
    }

    public void setRevising(String revising) {
        this.revising = revising;
    }

}
