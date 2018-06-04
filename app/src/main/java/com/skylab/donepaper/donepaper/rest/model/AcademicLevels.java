
package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AcademicLevels {

    @SerializedName("highschool")
    @Expose
    private String highschool;
    @SerializedName("college")
    @Expose
    private String college;
    @SerializedName("university")
    @Expose
    private String university;

    public String getHighschool() {
        return highschool;
    }

    public String getCollege() {
        return college;
    }

    public String getUniversity() {
        return university;
    }

}
