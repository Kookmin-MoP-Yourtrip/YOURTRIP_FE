package com.example.yourtrip.auth.model;

public class ProfileRequest {
    private String email;
    private String nickname;
    private String profileImageUrl;

    public ProfileRequest(String email, String nickname, String profileImageUrl) {
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}

