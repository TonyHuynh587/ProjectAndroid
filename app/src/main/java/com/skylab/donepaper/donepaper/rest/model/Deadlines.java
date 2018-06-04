
package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Deadlines {

    @SerializedName("12")
    @Expose
    private String halfDayText;
    @SerializedName("24")
    @Expose
    private String oneDayText;
    @SerializedName("36")
    @Expose
    private String oneAndAHalfDayText;
    @SerializedName("48")
    @Expose
    private String twoDaysText;
    @SerializedName("72")
    @Expose
    private String threeDaysText;
    @SerializedName("120")
    @Expose
    private String fiveDaysText;

    public String getHalfDayText() {
        return halfDayText;
    }

    public String getOneDayText() {
        return oneDayText;
    }

    public String getOneAndAHalfDayText() {
        return oneAndAHalfDayText;
    }

    public String getTwoDaysText() {
        return twoDaysText;
    }

    public String getThreeDaysText() {
        return threeDaysText;
    }

    public String getFiveDaysText() {
        return fiveDaysText;
    }

    public ArrayList<String> toArrayList(){
        ArrayList<String> list = new ArrayList<>();
        list.add(this.halfDayText);
        list.add(this.oneDayText);
        list.add(this.oneAndAHalfDayText);
        list.add(this.twoDaysText);
        list.add(this.threeDaysText);
        list.add(this.fiveDaysText);
        return list;
    }

}
