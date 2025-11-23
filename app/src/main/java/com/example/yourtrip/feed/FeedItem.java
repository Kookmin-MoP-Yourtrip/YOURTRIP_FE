package com.example.yourtrip.feed;

// URL 기반 API 데이터
public class FeedItem {
    private int id;
    private String imageUrl;

    // URL 생성자

    public FeedItem(int id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}