package com.example.yourtrip.mytrip.model;

import java.io.Serializable;

// Fragment로 결과를 전달하기 위해 Serializable 구현
public class PlaceAddResponse implements Serializable {
    private long placeId;
    private String placeName;
    private Double latitude;
    private Double longitude;
    private String placeUrl;
    private String placeLocation;
    private String memo;
    private String startTime;

    // Getter 메서드들
    public long getPlaceId() { return placeId; }
    public String getPlaceName() { return placeName; }
    public String getPlaceLocation() { return placeLocation; }
    // ... 나머지 Getter들도 필요에 따라 추가
}
