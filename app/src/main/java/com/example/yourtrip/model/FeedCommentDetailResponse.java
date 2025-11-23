package com.example.yourtrip.model;

public class FeedCommentDetailResponse {

    private long feedCommentId;
    private long feedId;
    private long userId;
    private String nickname;
    private String profileImageUrl;
    private String sentence;
    private String createdAt;
    private String updatedAt;

    public String getNickname() { return nickname; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getSentence() { return sentence; }
}
