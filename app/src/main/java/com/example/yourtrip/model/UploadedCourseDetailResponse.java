package com.example.yourtrip.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

/**
 * '업로드 코스 상세 조회' API (GET /api/upload-courses/{id}) 성공 시
 * 서버로부터 받는 응답 데이터를 담는 클래스 (DTO).
 */
public class UploadedCourseDetailResponse implements Serializable {

    @SerializedName("uploadCourseId")
    private long uploadCourseId;

    @SerializedName("title")
    private String title;

    @SerializedName("location")
    private String location;

    @SerializedName("introduction")
    private String introduction;

    @SerializedName("thumbnailImageUrl")
    private String thumbnailImageUrl;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("forkCount")
    private int forkCount;

    @SerializedName("keywords")
    private List<String> keywords;

    @SerializedName("daySchedules")
    private List<DaySchedule> daySchedules;

    // --- 내부 클래스 정의 ---

    public static class DaySchedule implements Serializable {
        @SerializedName("dayScheduleId")
        private long dayScheduleId;
        @SerializedName("day")
        private int day;
        @SerializedName("places")
        private List<Place> places;

        public long getDayScheduleId() { return dayScheduleId; }
        public int getDay() { return day; }
        public List<Place> getPlaces() { return places; }
    }

    public static class Place implements Serializable {
        @SerializedName("placeId")
        private long placeId;
        @SerializedName("placeName")
        private String placeName;
        // ... Place의 모든 필드와 getter 추가
    }

    public static class PlaceImage implements Serializable {
        // ... PlaceImage의 모든 필드와 getter 추가
    }

    // --- 최상위 클래스의 Getter 메서드들 ---

    public long getUploadCourseId() { return uploadCourseId; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getIntroduction() { return introduction; }
    public String getThumbnailImageUrl() { return thumbnailImageUrl; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public int getForkCount() { return forkCount; }
    public List<String> getKeywords() { return keywords; }
    public List<DaySchedule> getDaySchedules() { return daySchedules; }
}
