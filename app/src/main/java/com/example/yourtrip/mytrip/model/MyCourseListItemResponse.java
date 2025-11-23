package com.example.yourtrip.mytrip.model;
import java.io.Serializable;


public class MyCourseListItemResponse implements Serializable {
    private String title;
    private String location;
    private String startDate;
    private String endDate;
    private int memberCount;

    // 생성자
    public MyCourseListItemResponse(String title, String location, String startDate, String endDate, int memberCount) {
        this.title = title;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.memberCount = memberCount;
    }

    // getter, setter 메서드
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
}


//package com.example.yourtrip.model;
//
//public class MyCourseListItemResponse {
//    private String title;
//    private String location;
//    private String startDate;
//    private String endDate;
//    private int memberCount;
//
//    public MyCourseListItemResponse(String title, String location, String startDate, String endDate, int memberCount) {
//        this.title = title;
//        this.location = location;
//        this.startDate = startDate;
//        this.endDate = endDate;
//        this.memberCount = memberCount;
//    }
//
//    public String getTitle() { return title; }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//    public String getLocation() { return location; }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//    public String getStartDate() { return startDate; }
//    public void setStartDate(String startDate) {
//        this.startDate = startDate;
//    }
//    public String getEndDate() { return endDate; }
//    public void setEndDate(String endDate) {
//        this.endDate = endDate;
//    }
//    public int getMemberCount() { return memberCount; }
//    public void setMemberCount(int memberCount) {
//        this.memberCount = memberCount;
//    }
//}
//}
