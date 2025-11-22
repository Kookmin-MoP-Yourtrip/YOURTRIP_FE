package com.example.yourtrip.mytrip.model;

import java.util.ArrayList;

public class LocationItem {

    // 장소 이름 (입력값)
    public String placeName = "";

    // 장소 도착/출발 시간
    public String time = "";

    // 메모 (선택)
    public String memo = "";

    // 주소 (지도 선택 시 받아오는 값)
    public String address = "";

    // 위도/경도 (지도)
    public double lat = 0;
    public double lng = 0;

    // 사진 리스트 (여러 장 가능하게 ArrayList 사용)
    public ArrayList<String> photoPaths = new ArrayList<>();

    // 기본 생성자
    public LocationItem() {}

    // 필요하면 생성자 추가 가능
    public LocationItem(String placeName) {
        this.placeName = placeName;
    }

    public static class MyCourseCreateBasicResponse {

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
}
