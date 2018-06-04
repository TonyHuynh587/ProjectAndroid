package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skylab.donepaper.donepaper.utils.DateUtils;

import java.text.ParseException;

public class FileDetailItemLibrary {

    @SerializedName("file_name")
    @Expose
    private String fileName;
    @SerializedName("file_size")
    @Expose
    private int fileSize;
    @SerializedName("file_type")
    @Expose
    private String fileType;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("created_date")
    @Expose
    private long createDate;
    @SerializedName("description_excerpt")
    @Expose
    private String description;

    public String getFileName() {
        return fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public String getUrl() {
        return url;
    }

    public String getSubject() {
        return subject;
    }

    public String getCreateDate() throws ParseException {
        return DateUtils.convertDateToMonthFormat(String.valueOf(createDate));
    }

    public String getDescription() {
        return description;
    }
}
