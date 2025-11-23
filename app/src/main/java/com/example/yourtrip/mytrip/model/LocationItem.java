package com.example.yourtrip.mytrip.model;

import java.util.ArrayList;

public class LocationItem {

    private String placeName = "";
    private String time = "";
    private String memo = "";
    private String address = "";
    private double lat = 0;
    private double lng = 0;
    private ArrayList<String> photoPaths = new ArrayList<>();

    // 기본 생성자
    public LocationItem() {}

    public LocationItem(String placeName) {
        this.placeName = placeName;
    }


    // -------- Getter / Setter --------

    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public ArrayList<String> getPhotoPaths() { return photoPaths; }
    public void setPhotoPaths(ArrayList<String> photoPaths) { this.photoPaths = photoPaths; }
    public void addPhoto(String path) { this.photoPaths.add(path); }
}
