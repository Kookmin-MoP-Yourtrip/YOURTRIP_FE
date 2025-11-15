package com.example.yourtrip.model;

public class MyCourseCreateRequest {
    private String title;
    private String location;
    private String startDate;
    private String endDate;

    public MyCourseCreateRequest(String title, String location, String startDate, String endDate) {
        this.title = title;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // 필요하면 getter 추가
}
