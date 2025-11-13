package com.example.yourtrip.model;

public class MyCourseListItemResponse {
    private String title;
    private String location;
    private String startDate;
    private String endDate;
    private int memberCount;

    public MyCourseListItemResponse(String title, String location, String startDate, String endDate, int memberCount) {
        this.title = title;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.memberCount = memberCount;
    }

    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public int getMemberCount() { return memberCount; }
}
