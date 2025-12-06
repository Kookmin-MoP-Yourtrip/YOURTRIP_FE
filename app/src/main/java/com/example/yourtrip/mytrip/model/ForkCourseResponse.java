package com.example.yourtrip.mytrip.model;

import com.google.gson.annotations.SerializedName;

public class ForkCourseResponse {
    @SerializedName("myCourseId")
    private long myCourseId;

    public long getMyCourseId() {
        return myCourseId;
    }
}
