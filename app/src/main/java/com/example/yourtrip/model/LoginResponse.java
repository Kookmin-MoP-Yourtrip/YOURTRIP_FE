package com.example.yourtrip.model;

public class LoginResponse {
    private int userId;
    private String nickname;
    private String accessToken;

    public int getUserId() { return userId; }
    public String getNickname() { return nickname; }
    public String getAccessToken() { return accessToken; }
}
