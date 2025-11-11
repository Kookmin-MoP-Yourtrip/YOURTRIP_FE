package com.example.yourtrip.model;

public class SignupResponse {
    private int userId;
    private String email;
    private String nickname;
    private String createdAt;

    public int getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public String getCreatedAt() { return createdAt; }
}
