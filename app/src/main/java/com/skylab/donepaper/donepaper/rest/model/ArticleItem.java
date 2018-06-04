package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skylab.donepaper.donepaper.utils.DateUtils;

import java.text.ParseException;

public class ArticleItem {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("excerpt")
    @Expose
    private String excerpt;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("modified_date")
    @Expose
    private long date;

    public String getTitle() {
        return title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getDate() throws ParseException {
        return DateUtils.convertDateToMonthFormat(String.valueOf(date));
    }
}
