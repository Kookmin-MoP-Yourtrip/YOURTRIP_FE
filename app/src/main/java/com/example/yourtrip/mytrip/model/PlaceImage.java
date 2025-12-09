package com.example.yourtrip.mytrip.model;

import com.google.gson.annotations.SerializedName;

public class PlaceImage {
    @SerializedName("placeId")
    private long placeId;

    @SerializedName("placeImageId")
    private long imageId;

    @SerializedName("placeImageUrl")
    private String imageUrl;

    // Glide에서 사용
    public PlaceImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getPlaceId() { return placeId; }
    public long getImageId() { return imageId; }
    public String getImageUrl() { return imageUrl; }
}
