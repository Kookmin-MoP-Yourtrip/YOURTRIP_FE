package com.example.yourtrip.feed;

// URL 기반 API 데이터
public class FeedItem {

    private String imageUrl;

    // URL 생성자
    public FeedItem(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}