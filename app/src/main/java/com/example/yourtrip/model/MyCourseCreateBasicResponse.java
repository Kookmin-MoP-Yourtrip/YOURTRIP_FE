package com.example.yourtrip.model;

public class MyCourseCreateBasicResponse {

    private int myCourseId;   // 코스 ID
    private String title;     // 코스 제목
    private String location;  // 코스 위치
    private String startDate; // 코스 시작 날짜
    private String endDate;   // 코스 종료 날짜
    private int memberCount;  // 참여자 수

    // 기본 생성자 (필수는 아니지만 필요할 수 있음)
    public MyCourseCreateBasicResponse() {}

    // Getter and Setter methods

    // myCourseId를 반환하는 메소드
    public int getMyCourseId() {
        return myCourseId;
    }

    // myCourseId를 설정하는 메소드
    public void setMyCourseId(int myCourseId) {
        this.myCourseId = myCourseId;
    }

    // title을 반환하는 메소드
    public String getTitle() {
        return title;
    }

    // title을 설정하는 메소드
    public void setTitle(String title) {
        this.title = title;
    }

    // location을 반환하는 메소드
    public String getLocation() {
        return location;
    }

    // location을 설정하는 메소드
    public void setLocation(String location) {
        this.location = location;
    }

    // startDate를 반환하는 메소드
    public String getStartDate() {
        return startDate;
    }

    // startDate를 설정하는 메소드
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    // endDate를 반환하는 메소드
    public String getEndDate() {
        return endDate;
    }

    // endDate를 설정하는 메소드
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    // memberCount를 반환하는 메소드
    public int getMemberCount() {
        return memberCount;
    }

    // memberCount를 설정하는 메소드
    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
}
