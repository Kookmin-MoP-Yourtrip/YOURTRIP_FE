package com.example.yourtrip.model;

public class FeedUploadRequest {
    private String title;
    private String location;
    private String content;
//    private List<String> hashtags;


    public FeedUploadRequest(String title, String location, String content) {
        this.title = title;
        this.location = location;
        this.content = content;
//        this.hashtags = hashtags;
//        this.uploadCourseld = uploadCourseld;
    }
}
