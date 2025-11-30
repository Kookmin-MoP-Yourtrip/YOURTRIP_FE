package com.example.yourtrip.model;

public class FeedLikeResponse {

    private int feedId;
    private boolean isLiked;
    private int heartCount;

    public int getFeedId() {
        return feedId;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public int getHeartCount() {
        return heartCount;
    }
}
