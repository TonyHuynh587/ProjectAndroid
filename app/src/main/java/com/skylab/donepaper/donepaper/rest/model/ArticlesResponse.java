package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ArticlesResponse {

    @SerializedName("total_articles")
    @Expose
    private int totalArticles;
    @SerializedName("total_pages")
    @Expose
    private int totalPages;
    @SerializedName("current_page")
    @Expose
    private int currentPage;
    @SerializedName("limit")
    @Expose
    private int limit;
    @SerializedName("articles")
    @Expose
    private List<ArticleItem> articlesArrayList;

    public int getTotalArticles() {
        return totalArticles;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getLimit() {
        return limit;
    }

    public List<ArticleItem> getArticlesArrayList() {
        return articlesArrayList;
    }
}
