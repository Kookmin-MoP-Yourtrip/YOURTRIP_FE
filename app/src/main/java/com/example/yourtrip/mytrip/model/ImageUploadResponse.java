package com.example.yourtrip.mytrip.model;

import com.google.gson.annotations.SerializedName;

public class ImageUploadResponse {

    @SerializedName("placeImageId")
    private long imageId;    @SerializedName("placeImageUrl")
    private String imageUrl;

    // Getter 메서드
    public long getImageId() {
        return imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
