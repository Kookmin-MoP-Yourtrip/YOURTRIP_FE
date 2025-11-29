package com.example.yourtrip.mytrip.model;

import java.io.Serializable;
import java.util.List;

// Serializable 인터페이스를 구현하여 객체를 Intent나 Bundle을 통해 전달할 수 있도록 합니다.
public class LocationItem implements Serializable {

    private long placeId;
    private String placeName;
    private String placeLocation; // 주소
    private String memo;
    private String startTime;
    private List<String> imageUrls;

    // 생성자 1: API 응답을 통해 객체를 생성할 때 사용
    public LocationItem(long placeId, String placeName, String placeLocation, String memo, String startTime) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeLocation = placeLocation;
        this.memo = memo;
        this.startTime = startTime;
    }


    // Getter 메서드
    public long getPlaceId() { return placeId; }
    public String getPlaceName() { return placeName; }
    public String getPlaceLocation() { return placeLocation; }
    public String getMemo() { return memo; }
    public String getStartTime() { return startTime; }
    public List<String> getImageUrls() { return imageUrls; }


    //Setter 메서드
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
