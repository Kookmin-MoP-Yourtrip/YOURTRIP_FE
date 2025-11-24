package com.example.yourtrip.model;

public class FeedUploadRequest {
    private String title;
    private String location;
    private String content;

    public FeedUploadRequest(String title, String location, String content) {
        this.title = title;
        this.location = location;
        this.content = content;
    }
}
