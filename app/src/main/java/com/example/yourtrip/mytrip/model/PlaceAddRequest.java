package com.example.yourtrip.mytrip.model;

public class PlaceAddRequest {
    private String placeName;
    private Double latitude;
    private Double longitude;
    private String placeUrl;
    private String placeLocation;

    public PlaceAddRequest(String placeName, Double latitude, Double longitude, String placeUrl, String placeLocation) {
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeUrl = placeUrl;
        this.placeLocation = placeLocation;
    }
}
