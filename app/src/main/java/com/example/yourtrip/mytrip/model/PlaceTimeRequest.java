package com.example.yourtrip.mytrip.model;

import com.google.gson.annotations.SerializedName;

public class PlaceTimeRequest {

    @SerializedName("startTime")
    private String startTime;

    public PlaceTimeRequest(String startTime) {
        this.startTime = startTime;
    }
}
    