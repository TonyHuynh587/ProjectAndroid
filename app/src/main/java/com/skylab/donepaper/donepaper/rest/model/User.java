package com.skylab.donepaper.donepaper.rest.model;

public class User {

    private int id;
    private String token;
    private String name;
    private String email;

    public User(TokenData tokenData) {
        this.token = tokenData.getToken();
        this.name = tokenData.getName();
        this.email = tokenData.getEmail();
        this.id = tokenData.getId();
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }
}
