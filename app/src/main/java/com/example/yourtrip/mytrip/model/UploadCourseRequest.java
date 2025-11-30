package com.example.yourtrip.mytrip.model;

import java.util.List;

public class UploadCourseRequest {
    private long myCourseId;
    private String title;
    private String introduction;
    private List<String> keywords;

    public UploadCourseRequest(long myCourseId, String title, String introduction, List<String> keywords) {
        this.myCourseId = myCourseId;
        this.title = title;
        this.introduction = introduction;
        this.keywords = keywords;
    }

    public long getMyCourseId() { return myCourseId; }
    public String getTitle() { return title; }
    public String getIntroduction() { return introduction; }
    public List<String> getKeywords() { return keywords; }

}
