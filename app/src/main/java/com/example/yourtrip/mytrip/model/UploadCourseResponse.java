package com.example.yourtrip.mytrip.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

/**
 * '코스 업로드' API (POST /api/upload-courses/) 성공 시
 * 서버로부터 받는 응답 데이터를 담는 클래스 (DTO).
 */
public class UploadCourseResponse implements Serializable {

    @SerializedName("uploadCourseId")
    private long uploadCourseId;

    @SerializedName("title")
    private String title;

    @SerializedName("location")
    private String location;

    @SerializedName("introduction")
    private String introduction;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("keywords")
    private List<String> keywords;

    // =============================================================
    // [핵심 수정] 서버 응답 구조에 맞춰 내부 클래스를 새로 정의합니다.
    // =============================================================
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

        // Getter
        public long getDayScheduleId() { return dayScheduleId; }
        public int getDay() { return day; }
        public List<Place> getPlaces() { return places; }
    }

    public static class Place implements Serializable {
        @SerializedName("placeId")
        private long placeId;

        @SerializedName("placeName")
        private String placeName;

        @SerializedName("startTime")
        private String startTime;

        @SerializedName("memo")
        private String memo;

        @SerializedName("latitude")
        private double latitude;

        @SerializedName("longitude")
        private double longitude;

        @SerializedName("placeUrl")
        private String placeUrl;

        @SerializedName("placeLocation")
        private String placeLocation;

        @SerializedName("placeImages")
        private List<PlaceImage> placeImages;

        // Getter
        public long getPlaceId() { return placeId; }
        public String getPlaceName() { return placeName; }
        public String getStartTime() { return startTime; }
        public String getMemo() { return memo; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getPlaceUrl() { return placeUrl; }
        public String getPlaceLocation() { return placeLocation; }
        public List<PlaceImage> getPlaceImages() { return placeImages; }
    }

    public static class PlaceImage implements Serializable {
        @SerializedName("placeId")
        private long placeId;

        @SerializedName("placeImageId")
        private long placeImageId;

        @SerializedName("imageUrl")
        private String imageUrl;

        // Getter
        public long getPlaceId() { return placeId; }
        public long getPlaceImageId() { return placeImageId; }
        public String getImageUrl() { return imageUrl; }
    }


    // --- 최상위 클래스의 Getter 메서드들 ---

    public long getUploadCourseId() { return uploadCourseId; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getIntroduction() { return introduction; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public List<String> getKeywords() { return keywords; }
    public List<DaySchedule> getDaySchedules() { return daySchedules; }
}

