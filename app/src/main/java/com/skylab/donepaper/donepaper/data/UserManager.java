package com.skylab.donepaper.donepaper.data;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.skylab.donepaper.donepaper.rest.model.TokenData;
import com.skylab.donepaper.donepaper.rest.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private User user;
    private SharedPreferences sharedPreferences;
    private List<String> listRecentSearch = new ArrayList<>();

    public void init(TokenData tokenData, SharedPreferences sharedPreferences) {
        this.user = new User(tokenData);
        this.sharedPreferences = sharedPreferences;
        cacheTokenData(tokenData);
    }

    public void cacheRecentSearch(List<String> recentSearchList){
        this.listRecentSearch.clear();
        this.listRecentSearch.addAll(recentSearchList);
    }

    public List<String> getListRecentSearch() {
        return listRecentSearch;
    }

    public User getUser() {
        return user;
    }

    public void clear() {
        user = null;
    }

    private void cacheTokenData(TokenData tokenData) {
        Gson gson = new Gson();
        String json = gson.toJson(tokenData);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token_data", json);
        editor.apply();
    }
}