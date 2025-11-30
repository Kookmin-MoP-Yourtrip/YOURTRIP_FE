package com.example.yourtrip.model;

import java.util.List;

public class FeedUpdateRequest {

    private String title;
    private String location;
    private String content;
    private List<String> hashtags;
    private Integer uploadCourseId;

    public FeedUpdateRequest(String title, String location, String content,
                             List<String> hashtags, Integer uploadCourseId) {
        this.title = title;
        this.location = location;
        this.content = content;
        this.hashtags = hashtags;
        this.uploadCourseId = uploadCourseId;
    }

    // Getter & Setter
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getContent() { return content; }
    public List<String> getHashtags() { return hashtags; }
    public Integer getUploadCourseId() { return uploadCourseId; }
}
