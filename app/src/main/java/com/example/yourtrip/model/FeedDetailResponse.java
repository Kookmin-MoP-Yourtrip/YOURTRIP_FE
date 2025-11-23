package com.example.yourtrip.model;

import java.util.List;

public class FeedDetailResponse {

    private int feedId;
    private int userId;
    private String nickname;
    private String profileImageUrl;
    private String title;
    private String location;
    private String content;
    private List<String> hashtags;

    private int commentCount;
    private int heartCount;
    private int viewCount;
    private int uploadCourseId;

    private List<FeedMediaDetailResponse> mediaList;

    public int getFeedId() {
        return feedId;
    }

    public int getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getContent() {
        return content;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getHeartCount() {
        return heartCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getUploadCourseId() {
        return uploadCourseId;
    }

    public List<FeedMediaDetailResponse> getMediaList() {
        return mediaList;
    }
}
