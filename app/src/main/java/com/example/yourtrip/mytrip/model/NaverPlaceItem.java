package com.example.yourtrip.mytrip.model;

import com.google.gson.annotations.SerializedName;

// Naver 지역 검색 API의 개별 검색 결과를 담는 데이터 클래스
public class NaverPlaceItem {

    @SerializedName("title")
    private String title;

    @SerializedName("link")
    private String link;

    @SerializedName("description")
    private String description;

    @SerializedName("address")
    private String address;

    @SerializedName("roadAddress")
    private String roadAddress;

    @SerializedName("mapx")
    private int mapx;

    @SerializedName("mapy")
    private int mapy;

    // 데이터를 사용하기 위한 Getter 메서드들
    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public int getMapx() {
        return mapx;
    }

    public int getMapy() {
        return mapy;
    }
}