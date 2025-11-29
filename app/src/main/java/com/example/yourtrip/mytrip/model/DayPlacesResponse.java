package com.example.yourtrip.mytrip.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * '일차별 장소 리스트 조회' API의 응답을 담는 DTO 클래스입니다.
 */public class DayPlacesResponse {

    private long dayId;
    private int day;

    // API 응답 JSON의 'places' 키와 이 필드를 매핑합니다.
    @SerializedName("places")
    private List<LocationItem> places; // 🔴 LocationItem 클래스는 이미 만들어져 있음

    // Getter 메서드들
    public long getDayId() { return dayId; }
    public int getDay() { return day; }
    public List<LocationItem> getPlaces() { return places; }
}
