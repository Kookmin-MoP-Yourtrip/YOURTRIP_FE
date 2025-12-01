package com.example.yourtrip.model;

import java.util.List;

public class FeedUpdateRequest {

    private String title;
    private String location;
    private String content;
    private List<String> hashtags;
    private Integer uploadCourseId;

    // ✅ 유지할 기존 이미지 ID 목록 (Long으로 변경)
    private List<Long> keepMediaIds;

    public FeedUpdateRequest(String title,
                             String location,
                             String content,
                             List<String> hashtags,
                             Integer uploadCourseId,
                             List<Long> keepMediaIds) {
        this.title = title;
        this.location = location;
        this.content = content;
        this.hashtags = hashtags;
        this.uploadCourseId = uploadCourseId;
        this.keepMediaIds = keepMediaIds;
    }

    // Getter (필요한 것만)
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getContent() { return content; }
    public List<String> getHashtags() { return hashtags; }
    public Integer getUploadCourseId() { return uploadCourseId; }
    public List<Long> getKeepMediaIds() { return keepMediaIds; }
}
