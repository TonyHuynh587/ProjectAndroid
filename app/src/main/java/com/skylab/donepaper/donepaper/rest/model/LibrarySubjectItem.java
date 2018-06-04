package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LibrarySubjectItem {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("file_count")
    @Expose
    private int fileCount;

    public int getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getFileCount() {
        return fileCount + " Files";
    }
}
