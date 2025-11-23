package com.example.yourtrip.feed;

public class FeedItem {
    private int id;
    private String imageUrl;

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
