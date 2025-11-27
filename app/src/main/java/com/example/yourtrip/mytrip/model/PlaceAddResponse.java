package com.example.yourtrip.mytrip.model;

import java.io.Serializable;

/**
 * '일차에 장소 추가' API (POST /api/my-courses/{courseId}/days/{dayId}/places)의
 * 201 성공 응답을 담는 DTO(Data Transfer Object) 클래스입니다.
 * AddLocationActivity에서 결과를 받아 Fragment로 전달하기 위해 Serializable 인터페이스를 구현합니다.
 */
public class PlaceAddResponse implements Serializable {
    private long placeId;
    private String placeName;
    private Double latitude;
    private Double longitude;
    private String placeUrl;
    private String placeLocation;
    private String memo;
    private String startTime;

    // 모든 필드에 대한 Getter 메서드
    public long getPlaceId() {
        return placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getPlaceUrl() {
        return placeUrl;
    }

    public String getPlaceLocation() {
        return placeLocation;
    }

    public String getMemo() {
        return memo;
    }

    public String getStartTime() {
        return startTime;
    }
}
