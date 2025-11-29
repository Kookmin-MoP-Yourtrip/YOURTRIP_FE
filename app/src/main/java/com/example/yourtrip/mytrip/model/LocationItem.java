package com.example.yourtrip.mytrip.model;

import java.io.Serializable;

// Serializable 인터페이스를 구현하여 객체를 Intent나 Bundle을 통해 전달할 수 있도록 합니다.
public class LocationItem implements Serializable {

    private long placeId;
    private String placeName;
    private String placeLocation; // 주소
    private String memo;
    private String startTime;
    // 위도, 경도 등 필요한 필드들은 나중에 추가할 수 있습니다.

    // 생성자 1: API 응답을 통해 객체를 생성할 때 사용
    public LocationItem(long placeId, String placeName, String placeLocation, String memo, String startTime) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeLocation = placeLocation;
        this.memo = memo;
        this.startTime = startTime;
    }

    // 생성자 2: 테스트 또는 임시 데이터를 만들 때 사용
    public LocationItem(String placeName, String placeLocation) {
        this.placeName = placeName;
        this.placeLocation = placeLocation;
    }

    // 각 필드의 값을 가져오기 위한 Getter 메서드들
    public long getPlaceId() { return placeId; }
    public String getPlaceName() { return placeName; }
    public String getPlaceLocation() { return placeLocation; }
    public String getMemo() { return memo; }
    public String getStartTime() { return startTime; }
}


//package com.example.yourtrip.mytrip.model;
//
//import java.util.ArrayList;
//
//public class LocationItem {
//
//    private String placeName = "";
//    private String time = "";
//    private String memo = "";
//    private String address = "";
//    private double lat = 0;
//    private double lng = 0;
//    private ArrayList<String> photoPaths = new ArrayList<>();
//
//    // 기본 생성자
//    public LocationItem() {}
//
//    public LocationItem(String placeName) {
//        this.placeName = placeName;
//    }
//
//
//    // -------- Getter / Setter --------
//
//    public String getPlaceName() { return placeName; }
//    public void setPlaceName(String placeName) { this.placeName = placeName; }
//
//    public String getTime() { return time; }
//    public void setTime(String time) { this.time = time; }
//
//    public String getMemo() { return memo; }
//    public void setMemo(String memo) { this.memo = memo; }
//
//    public String getAddress() { return address; }
//    public void setAddress(String address) { this.address = address; }
//
//    public double getLat() { return lat; }
//    public void setLat(double lat) { this.lat = lat; }
//
//    public double getLng() { return lng; }
//    public void setLng(double lng) { this.lng = lng; }
//
//    public ArrayList<String> getPhotoPaths() { return photoPaths; }
//    public void setPhotoPaths(ArrayList<String> photoPaths) { this.photoPaths = photoPaths; }
//    public void addPhoto(String path) { this.photoPaths.add(path); }
//}
