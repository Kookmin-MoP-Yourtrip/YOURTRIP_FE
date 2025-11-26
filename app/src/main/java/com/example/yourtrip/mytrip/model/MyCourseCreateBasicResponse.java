package com.example.yourtrip.mytrip.model;

public class MyCourseCreateBasicResponse {
    private long myCourseId;
    private String title;
    private String location;
    private String startDate;
    private String endDate;
    private int memberCount;

    // Getter 메서드들
    public long getMyCourseId() { return myCourseId; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public int getMemberCount() { return memberCount; }

    @Override
    public String toString() {
        return "MyCourseCreateBasicResponse{" +
                "myCourseId=" + myCourseId +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", memberCount=" + memberCount +
                '}';
    }
}
