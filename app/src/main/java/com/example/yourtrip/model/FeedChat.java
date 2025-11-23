package com.example.yourtrip.model;

public class FeedChat {
    private String profileUrl;
    private String nickname;
    private String content;

    public FeedChat(String profileUrl, String nickname, String content) {
        this.profileUrl = profileUrl;
        this.nickname = nickname;
        this.content = content;
    }

    public String getProfileUrl() { return profileUrl; }
    public String getNickname() { return nickname; }
    public String getContent() { return content; }
}
